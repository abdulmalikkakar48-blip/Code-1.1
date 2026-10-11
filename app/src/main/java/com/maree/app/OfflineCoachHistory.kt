package com.maree.app

import android.content.Context
import java.text.Normalizer
import java.util.Locale
import org.json.JSONArray

/** Phrases inconnues facultatives, privées et locales ; aucun historique du dialogue. */
internal class OfflineCoachHistory(context: Context) {
    private val prefs = context.getSharedPreferences("selkie_offline_coach", Context.MODE_PRIVATE)

    val enabled: Boolean
        get() = synchronized(lock) { readEnabled() }

    fun generation(): Long = synchronized(lock) { readGeneration() }

    /** Désactiver retire également toutes les phrases précédemment enregistrées. */
    fun setEnabled(enabled: Boolean, expectedGeneration: Long = generation()): Boolean = synchronized(lock) {
        if (readGeneration() != expectedGeneration) return@synchronized false
        safelyCommit {
            prefs.edit().putBoolean(ENABLED, enabled).apply {
                if (!enabled) {
                    remove(ENTRIES)
                    putLong(GENERATION, expectedGeneration + 1L)
                }
            }.commit()
        }
    }

    /** L'appelant doit déjà avoir exclu les réponses reconnues et les messages sensibles. */
    fun recordUnknown(message: String, expectedGeneration: Long = generation()): Boolean = synchronized(lock) {
        if (readGeneration() != expectedGeneration) return@synchronized false
        val entry = message.trim()
        if (!readEnabled() || entry.isBlank() || entry.length > MAX_LENGTH || sensitive(entry))
            return@synchronized true
        val entries = readEntries().toMutableList()
        if (entries.any { duplicateKey(it) == duplicateKey(entry) }) return@synchronized true
        entries.add(entry)
        val retained = entries.takeLast(MAX_ENTRIES)
        safelyCommit { prefs.edit().putString(ENTRIES, JSONArray(retained).toString()).commit() }
    }

    fun entries(): List<String> = synchronized(lock) {
        if (readEnabled()) readEntries() else emptyList()
    }

    /** Retire les phrases sans changer le choix de conservation. */
    fun clearEntries(expectedGeneration: Long = generation()): Boolean = synchronized(lock) {
        if (readGeneration() != expectedGeneration) return@synchronized false
        safelyCommit {
            prefs.edit().remove(ENTRIES).putLong(GENERATION, expectedGeneration + 1L).commit()
        }
    }

    /** L'effacement global désactive aussi la conservation pour les futurs messages. */
    fun clear(): Boolean = synchronized(lock) {
        val nextGeneration = readGeneration() + 1L
        safelyCommit { prefs.edit().clear().putLong(GENERATION, nextGeneration).commit() }
    }

    private fun readGeneration(): Long = try {
        prefs.getLong(GENERATION, 0L)
    } catch (_: Exception) {
        0L
    }

    private fun readEnabled(): Boolean = try {
        prefs.getBoolean(ENABLED, false)
    } catch (_: Exception) {
        false
    }

    private fun readEntries(): List<String> = try {
        val json = JSONArray(prefs.getString(ENTRIES, "[]") ?: "[]")
        buildList {
            for (index in 0 until json.length()) {
                val item = json.opt(index) as? String ?: continue
                if (item.isNotBlank() && item.length <= MAX_LENGTH && !sensitive(item)) add(item)
            }
        }.distinctBy(::duplicateKey).takeLast(MAX_ENTRIES)
    } catch (_: Exception) {
        emptyList()
    }

    private fun safelyCommit(operation: () -> Boolean): Boolean = try {
        operation()
    } catch (_: Exception) {
        false
    }

    companion object {
        private val lock = Any()
        private const val ENABLED = "enabled"
        private const val ENTRIES = "unknown_entries"
        private const val GENERATION = "generation"
        private const val MAX_ENTRIES = 50
        private const val MAX_LENGTH = 800
        private val whitespace = Regex("\\s+")
        private val accents = Regex("\\p{M}+")
        private val contactOrNumber = Regex(
            "[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}|https?://|www\\.|(?:\\+?\\d[\\s().-]*){7,}"
        )
        private val privateOrCrisis = Regex(
            "\\b(?:suicid\\w*|automutil\\w*|scarifi\\w*|mourir|meurs|tuer|" +
                "depress\\w*|maltrait\\w*|agress\\w*|viol|violee?|cancer|diagnostic|" +
                "medicament\\w*|traitement|enceinte|grossesse|iban|rib|password)\\b|" +
                "me faire du mal|faire mal a moi|en finir|ne plus vivre|plus envie de vivre|" +
                "vais tres mal|va tres mal|je m.?appelle|mon nom|mon adresse|" +
                "date de naissance|mot de passe|cle.?api|numero de (?:carte|telephone|securite)"
        )

        private fun duplicateKey(value: String): String =
            value.lowercase(Locale.ROOT).replace(whitespace, " ")

        /** Filtre de précaution ; il ne garantit pas la détection de toute information privée. */
        private fun sensitive(value: String): Boolean {
            val plain = Normalizer.normalize(value.lowercase(Locale.ROOT), Normalizer.Form.NFD)
                .replace(accents, "")
            return contactOrNumber.containsMatchIn(value) || privateOrCrisis.containsMatchIn(plain)
        }
    }
}
