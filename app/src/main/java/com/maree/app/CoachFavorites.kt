package com.maree.app

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

/** A prepared favorite is a reference; its paid answer is never persisted. */
data class CoachFavorite(
    val id: String,
    val preparedId: String?,
    val topicId: String,
    val label: String,
    val reply: String?,
    val language: AppLocale,
    val minimumPlan: PlanType
) {
    fun isLocked(plan: PlanType): Boolean = plan.ordinal < minimumPlan.ordinal

    /** Resolve against the current entitlement and language, including after a downgrade. */
    fun preparedQuestion(language: AppLocale, plan: PlanType): PreparedCoachQuestion? =
        preparedId?.takeUnless { isLocked(plan) }?.let { preparedCoachQuestionById(it, language, plan) }
}

/** User-selected advice only, local to the same preferences erased by Store. */
class CoachFavoritesStore internal constructor(private val prefs: SharedPreferences) {
    constructor(context: Context) : this(
        context.applicationContext.getSharedPreferences("maree", Context.MODE_PRIVATE)
    )

    fun entries(): List<CoachFavorite> = synchronized(LocalFeatureLock.lock) { readEntries() }

    fun savePrepared(id: String, language: AppLocale): String? = synchronized(LocalFeatureLock.lock) {
        val favorite = preparedFavorite(id, language) ?: return@synchronized null
        save(favorite)
    }

    fun saveAdvice(topicId: String, label: String, reply: String, language: AppLocale): String? =
        synchronized(LocalFeatureLock.lock) {
            val favorite = adviceFavorite(topicId, label, reply, language) ?: return@synchronized null
            save(favorite)
        }

    fun remove(id: String): Boolean = synchronized(LocalFeatureLock.lock) {
        val retained = readEntries().filterNot { it.id == id }
        persist(retained)
    }

    /** Whitelist validated favorite fields, never arbitrary preferences or entered questions. */
    fun exportJson(): JSONArray = synchronized(LocalFeatureLock.lock) {
        JSONArray().apply { readEntries().forEach { put(encode(it)) } }
    }

    private fun save(favorite: CoachFavorite): String? {
        val current = readEntries()
        val retained = if (current.any { it.id == favorite.id }) current
            else (current + favorite).takeLast(MAX_ENTRIES)
        // A failed Android commit can still update memory; duplicates also verify disk persistence.
        return if (persist(retained)) favorite.id else null
    }

    private fun persist(entries: List<CoachFavorite>): Boolean = try {
        prefs.edit().putString(KEY, JSONArray().apply { entries.forEach { put(encode(it)) } }.toString()).commit()
    } catch (_: Exception) { false }

    private fun readEntries(): List<CoachFavorite> = try {
        val raw = prefs.getString(KEY, "[]") ?: "[]"
        if (raw.length > MAX_SERIALIZED_LENGTH) emptyList() else {
            val array = JSONArray(raw)
            buildList {
                // A malformed or overlarge record is discarded without trusting saved labels/plans.
                for (index in 0 until minOf(array.length(), MAX_ENTRIES * 4)) {
                    val record = array.optJSONObject(index) ?: continue
                    val language = AppLocale.entries.firstOrNull { it.tag == record.optString("language") }
                        ?: continue
                    val favorite = when (record.optString("kind")) {
                        "prepared" -> preparedFavorite(record.optString("preparedId"), language)
                        "advice" -> adviceFavorite(record.optString("topicId"), record.optString("label"),
                            record.optString("reply"), language)
                        else -> null
                    } ?: continue
                    if (record.optString("id") != favorite.id) continue
                    add(favorite)
                }
            }.distinctBy { it.id }.takeLast(MAX_ENTRIES)
        }
    } catch (_: Exception) { emptyList() }

    private fun encode(favorite: CoachFavorite): JSONObject = JSONObject().apply {
        put("id", favorite.id)
        put("language", favorite.language.tag)
        put("minimumPlan", favorite.minimumPlan.name)
        if (favorite.preparedId != null) {
            put("kind", "prepared")
            put("preparedId", favorite.preparedId)
        } else {
            put("kind", "advice")
            put("topicId", favorite.topicId)
            put("label", favorite.label)
            put("reply", favorite.reply)
        }
    }

    companion object {
        internal const val KEY = "coach_favorites_v1"
        private const val MAX_ENTRIES = 50
        private const val MAX_REPLY_LENGTH = 4_000
        private const val MAX_SERIALIZED_LENGTH = 230_000

        private fun preparedFavorite(id: String, language: AppLocale): CoachFavorite? {
            if (id.isBlank() || id.length > 160) return null
            val minimum = PlanType.entries.firstOrNull { preparedCoachQuestionById(id, language, it) != null }
                ?: return null
            val question = preparedCoachQuestionById(id, language, minimum) ?: return null
            val topic = coachTopicsFor(language).firstOrNull { it.id == question.topicId } ?: return null
            return CoachFavorite("prepared:${language.tag}:$id", id, topic.id, topic.label, null, language, minimum)
        }

        private fun adviceFavorite(topicId: String, label: String, reply: String, language: AppLocale): CoachFavorite? {
            if (!coachFavoriteAdviceIsValid(topicId, label, reply, language)) return null
            val digest = MessageDigest.getInstance("SHA-256").digest(reply.toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
            return CoachFavorite("advice:${language.tag}:$topicId:$digest", null, topicId, label, reply,
                language, PlanType.FREE)
        }

        /** Exact authored advice plus a constrained, nonprivate usage/balance suffix only. */
        internal fun coachFavoriteAdviceIsValid(topicId: String, label: String, reply: String,
            language: AppLocale): Boolean {
            if (reply.isBlank() || reply.length > MAX_REPLY_LENGTH) return false
            val topic = coachTopicsFor(language).firstOrNull { it.id == topicId } ?: return false
            if (label != topic.label) return false
            val base = topic.replies.firstOrNull { reply == it || reply.startsWith("$it\n\n") } ?: return false
            val suffix = reply.removePrefix(base)
            if (suffix.isEmpty()) return true
            // User-defined app names are not accepted; only aliases from the authored topic are safe.
            if (OfflineCoachHistory.sensitive(suffix)) return false
            val balancePrefix = if (language == AppLocale.ENGLISH) "\n\nYour current Selkie balance: "
                else "\n\nTon solde actuel dans Selkie : "
            val pointTopics = setOf("points", "earn_points", "lose_points", "zero_points", "discipline_bonus",
                "exceeded", "relapse", "streak")
            if (topicId in pointTopics && suffix.matches(Regex(Regex.escape(balancePrefix) +
                    "[0-9]{1,10}(?:[.,](?:25|5|75))? points\\."))) return true
            val usagePrefix = if (language == AppLocale.ENGLISH) "\n\nRecorded usage for "
                else "\n\nSuivi enregistré pour "
            val separator = if (language == AppLocale.ENGLISH) ": " else " : "
            val ending = if (language == AppLocale.ENGLISH) " min during the current tracking period."
                else " min sur la période de suivi en cours."
            if (!suffix.startsWith(usagePrefix)) return false
            val content = suffix.removePrefix(usagePrefix)
            val name = content.substringBefore(separator, "")
            if (topic.appAliases.none { it.equals(name.lowercase(Locale.ROOT), ignoreCase = true) }) return false
            return content.removePrefix(name + separator).matches(Regex("[0-9]{1,12}" + Regex.escape(ending)))
        }
    }
}
