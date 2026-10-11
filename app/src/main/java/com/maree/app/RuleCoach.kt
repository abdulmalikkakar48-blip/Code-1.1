package com.maree.app

import java.text.Normalizer
import java.util.Locale
import kotlin.random.Random

data class CoachTopic(
    val id: String,
    val label: String,
    val keywords: List<String>,
    val replies: List<String>,
    val appAliases: List<String> = emptyList()
)

data class OfflineCoachApp(val name: String, val trackedMillis: Long)

data class OfflineCoachContext(
    val pointUnits: Int,
    val trackingEnabled: Boolean,
    val apps: List<OfflineCoachApp>
)

data class OfflineCoachReply(
    val topicId: String?,
    val text: String,
    val suggestions: List<String> = emptyList(),
    val unrecognized: Boolean = false,
    val sensitive: Boolean = false
)

/** Conseils écrits à l'avance : aucun modèle, compte, stockage ou appel réseau. */
class RuleCoach(
    topics: List<CoachTopic> = coachTopics,
    private val random: Random = Random.Default
) {
    private data class PreparedTopic(val topic: CoachTopic, val keywords: List<List<String>>)

    private val prepared = topics.filter { it.replies.any(String::isNotBlank) }.map { topic ->
        PreparedTopic(topic.copy(replies = topic.replies.filter(String::isNotBlank).distinct()),
            (topic.keywords + topic.appAliases).map(::words).filter { it.isNotEmpty() }.distinct())
    }
    private val lastReplies = mutableMapOf<String, Int>()

    @Synchronized
    fun reply(message: String, context: OfflineCoachContext): OfflineCoachReply {
        val tokens = words(message)
        if (requiresHumanSupport(tokens)) return humanSupport()
        val scored = prepared.map { it to score(tokens, it.keywords) }
        // Le score prime ; à égalité, une demande d'action prime sur un nom d'app.
        val best = scored.maxWithOrNull(compareBy<Pair<PreparedTopic, Int>> { it.second }
            .thenBy { if (it.first.topic.id in actionTopics) 1 else 0 })
        return if (best != null && best.second > 0) knownReply(best.first.topic, context)
        else unknownReply(tokens)
    }

    @Synchronized
    fun replyForTopic(topicId: String, context: OfflineCoachContext): OfflineCoachReply {
        val topic = prepared.firstOrNull { it.topic.id == topicId }?.topic
        return if (topic == null) unknownReply(emptyList()) else knownReply(topic, context)
    }

    private fun knownReply(topic: CoachTopic, context: OfflineCoachContext): OfflineCoachReply {
        val last = lastReplies[topic.id]
        val count = topic.replies.size
        val index = if (count == 1) 0 else {
            val draw = random.nextInt(if (last == null) count else count - 1)
            if (last != null && draw >= last) draw + 1 else draw
        }
        lastReplies[topic.id] = index
        val text = buildString {
            append(topic.replies[index])
            if (topic.id in pointTopics) {
                append("\n\nTon solde actuel dans Selkie : ")
                append(formatPointUnits(context.pointUnits.coerceAtLeast(0)))
                append(" points.")
            }
            if (context.trackingEnabled && topic.appAliases.isNotEmpty()) {
                val aliases = topic.appAliases.map(::words).filter { it.isNotEmpty() }
                val app = context.apps.firstOrNull { candidate ->
                    candidate.name.isNotBlank() && candidate.trackedMillis >= 0 &&
                        aliases.any { containsWords(words(candidate.name), it) }
                }
                if (app != null) {
                    append("\n\nSuivi enregistré pour ")
                    append(app.name.replace(Regex("\\s+"), " ").trim().take(80))
                    append(" : ")
                    append(app.trackedMillis / 60_000L)
                    append(" min sur la période de suivi en cours.")
                }
            }
        }
        return OfflineCoachReply(topic.id, text)
    }

    private fun unknownReply(tokens: List<String>): OfflineCoachReply {
        val near = prepared.map { it.topic.id to nearbyScore(tokens, it.keywords) }
            .filter { it.second > 0 }.sortedByDescending { it.second }.map { it.first }
        val available = prepared.map { it.topic.id }
        val suggestions = (near + defaultSuggestions.filter { it in available } + available)
            .distinct().take(4)
        return OfflineCoachReply(null,
            "Je n’ai pas encore de conseil prévu pour cette phrase. Je réponds à partir de conseils écrits à l’avance. Choisis un sujet ci-dessous ou reformule ta question.",
            suggestions = suggestions, unrecognized = true)
    }

    private fun score(tokens: List<String>, keywords: List<List<String>>): Int = keywords.sumOf { keyword ->
        when {
            containsWords(tokens, keyword) -> if (keyword.size == 1) 4 else keyword.size * 4 + 4
            containsSimilarWords(tokens, keyword) -> if (keyword.size == 1) 2 else keyword.size * 3 + 2
            else -> 0
        }
    }

    private fun nearbyScore(tokens: List<String>, keywords: List<List<String>>): Int =
        keywords.flatten().distinct().count { keyword ->
            keyword.length >= 6 && tokens.any { token ->
                token.length >= 6 && kotlin.math.abs(keyword.length - token.length) <= 1 &&
                    withinOneEdit(keyword, token)
            }
        }

    private fun requiresHumanSupport(tokens: List<String>): Boolean =
        tokens.any { it.startsWith("suicid") || it in harmWords } ||
            distressPhrases.any { phrase ->
                containsWords(tokens, phrase) &&
                    !(phrase == deathPhrase && containsWords(tokens, laughterPhrase))
            }

    private fun humanSupport() = OfflineCoachReply(null,
        "Tu n’as pas à traverser une situation aussi difficile seul. Contacte maintenant une personne de confiance et dis-lui ce qui se passe. En France, tu peux appeler le 3114, gratuitement, 24 h/24. Si tu es en danger immédiat, appelle le 112 ou rejoins les urgences. Ce coach ne peut pas évaluer une urgence ni remplacer une aide humaine.",
        sensitive = true)

    companion object {
        private val pointTopics = setOf("points", "exceeded", "relapse", "streak")
        private val actionTopics = setOf("reset", "limits", "add_app", "permissions", "custom_challenges",
            "challenges", "pause_tracking", "usage", "red_screen", "points", "premium")
        private val defaultSuggestions = listOf("tiktok", "sleep", "boredom", "points")
        private val harmWords = setOf("automutilation", "automutiler", "suicide", "suicidaire")
        private val typoAliases = mapOf(
            "tiktoc" to "tiktok", "tikto" to "tiktok", "youtub" to "youtube",
            "insta" to "instagram", "instagrame" to "instagram",
            "snap" to "snapchat", "whatsap" to "whatsapp"
        )
        private val deathPhrase = words("je vais mourir")
        private val laughterPhrase = words("mourir de rire")
        private val distressPhrases = listOf(
            "me faire du mal", "se faire du mal", "me blesser volontairement",
            "je veux mourir", "je vais mourir", "envie de mourir", "veux me tuer", "vais me tuer",
            "je veux en finir", "envie d en finir", "je vais tres mal", "je me sens tres mal",
            "je ne vais pas bien du tout", "je vais pas bien du tout", "je n en peux plus",
            "j en peux plus", "plus envie de vivre", "ne veux plus vivre", "veux plus vivre",
            "kill myself", "hurt myself", "want to die"
        ).map(::words)
        private fun words(value: String): List<String> {
            val expanded = value.lowercase(Locale.ROOT).replace("œ", "oe").replace("æ", "ae")
            val text = Normalizer.normalize(expanded, Normalizer.Form.NFD)
                .replace(Regex("\\p{M}+"), "").replace(Regex("[^a-z0-9]+"), " ").trim()
            return if (text.isEmpty()) emptyList() else text.split(Regex("\\s+"))
                .map { typoAliases[it] ?: it }
        }

        private fun containsWords(tokens: List<String>, phrase: List<String>): Boolean {
            if (phrase.isEmpty() || phrase.size > tokens.size) return false
            return (0..tokens.size - phrase.size).any { start ->
                phrase.indices.all { offset -> tokens[start + offset] == phrase[offset] }
            }
        }

        /** Une seule petite faute par expression, uniquement sur un mot assez long. */
        private fun containsSimilarWords(tokens: List<String>, phrase: List<String>): Boolean {
            if (phrase.isEmpty() || phrase.size > tokens.size) return false
            return (0..tokens.size - phrase.size).any { start ->
                var changed = 0
                phrase.indices.all { offset ->
                    val expected = phrase[offset]
                    val actual = tokens[start + offset]
                    if (expected == actual) true else {
                        changed++
                        changed <= 1 && expected.length >= 6 && actual.length >= 5 &&
                            expected.take(2) == actual.take(2) && withinOneEdit(expected, actual)
                    }
                }
            }
        }

        /** Distance maximale 1, avec inversion de deux lettres voisines admise. */
        private fun withinOneEdit(left: String, right: String): Boolean {
            if (kotlin.math.abs(left.length - right.length) > 1) return false
            if (left == right) return true
            if (left.length == right.length) {
                val different = left.indices.filter { left[it] != right[it] }
                return different.size == 1 || (different.size == 2 &&
                    different[1] == different[0] + 1 &&
                    left[different[0]] == right[different[1]] && left[different[1]] == right[different[0]])
            }
            val longer = if (left.length > right.length) left else right
            val shorter = if (left.length > right.length) right else left
            var shortIndex = 0
            var skipped = false
            for (character in longer) {
                if (shortIndex < shorter.length && character == shorter[shortIndex]) shortIndex++
                else if (skipped) return false else skipped = true
            }
            return shortIndex == shorter.length
        }
    }
}
