package com.maree.app

import java.text.Normalizer
import java.util.Locale
import kotlin.random.Random

data class CoachTopic(
    val id: String,
    val label: String,
    val keywords: List<String>,
    val replies: List<String>,
    val appAliases: List<String> = emptyList(),
    val appHelp: Boolean = false
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
    topics: List<CoachTopic>? = null,
    private val random: Random = Random.Default,
    private val language: AppLocale = AppLanguage.current
) {
    private data class PreparedTopic(val topic: CoachTopic, val keywords: List<List<String>>)

    // Les mots des deux langues sont compris ; les réponses gardent la langue choisie.
    // Un catalogue fourni explicitement reste autonome (utile pour des sujets/tests ciblés).
    private val selectedTopics = topics ?: coachTopicsFor(language).let { selected ->
        val alternate = coachTopicsFor(if (language == AppLocale.FRENCH) AppLocale.ENGLISH else AppLocale.FRENCH)
            .associateBy { it.id }
        selected.map { topic -> topic.copy(keywords = (topic.keywords +
            alternate[topic.id]?.keywords.orEmpty()).distinct()) }
    }
    private fun text(french: String, english: String): String =
        if (language == AppLocale.ENGLISH) english else french

    private fun pointBalance(pointUnits: Int): String {
        val safeUnits = pointUnits.coerceAtLeast(0)
        val whole = (safeUnits / POINT_UNITS_PER_POINT).toString()
        val fraction = when (safeUnits % POINT_UNITS_PER_POINT) {
            1 -> "25"; 2 -> "5"; 3 -> "75"; else -> ""
        }
        return if (fraction.isEmpty()) whole else whole + text(",", ".") + fraction
    }

    private val prepared = selectedTopics.filter { it.replies.any(String::isNotBlank) }.map { topic ->
        PreparedTopic(topic.copy(replies = topic.replies.filter(String::isNotBlank).distinct()),
            (topic.keywords + topic.appAliases).map(::words).filter { it.isNotEmpty() }.distinct())
    }
    private val lastReplies = mutableMapOf<String, Int>()

    @Synchronized
    fun reply(message: String, context: OfflineCoachContext): OfflineCoachReply {
        val tokens = words(message)
        if (requiresHumanSupport(tokens)) return humanSupport()
        val appContext = hasAppContext(tokens)
        val requestedAction = requestedAppAction(tokens)
        val scored = prepared.map { preparedTopic ->
            val keywords = preparedTopic.keywords.filter { keyword ->
                // Le nom de Selkie situe une question ; il n'en donne pas la réponse.
                !(keyword in brandKeywords && tokens.size > 1) &&
                    !(preparedTopic.topic.appHelp && keyword in genericNavigationKeywords && !appContext)
            }
            val actionScore = if (preparedTopic.topic.id == requestedAction) 12 else 0
            preparedTopic to score(tokens, keywords) + actionScore
        }
        // Le score prime ; à égalité, une demande d'action prime sur un nom d'app.
        val best = scored.maxWithOrNull(compareBy<Pair<PreparedTopic, Int>> { it.second }
            .thenBy { if (it.first.topic.id in actionTopics) 1 else 0 })
        return if (best != null && best.second > 0) knownReply(best.first.topic, context)
        else unknownReply(tokens, appContext)
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
                append(text("\n\nTon solde actuel dans Selkie : ", "\n\nYour current Selkie balance: "))
                append(pointBalance(context.pointUnits))
                append(" points.")
            }
            if (context.trackingEnabled && topic.appAliases.isNotEmpty()) {
                val aliases = topic.appAliases.map(::words).filter { it.isNotEmpty() }
                val app = context.apps.firstOrNull { candidate ->
                    candidate.name.isNotBlank() && candidate.trackedMillis >= 0 &&
                        aliases.any { containsWords(words(candidate.name), it) }
                }
                if (app != null) {
                    append(text("\n\nSuivi enregistré pour ", "\n\nRecorded usage for "))
                    append(app.name.replace(Regex("\\s+"), " ").trim().take(80))
                    append(text(" : ", ": "))
                    append(app.trackedMillis / 60_000L)
                    append(text(" min sur la période de suivi en cours.", " min during the current tracking period."))
                }
            }
        }
        return OfflineCoachReply(topic.id, text)
    }

    private fun unknownReply(tokens: List<String>, appContext: Boolean = false): OfflineCoachReply {
        val candidates = prepared.filter { if (appContext) it.topic.appHelp else !it.topic.appHelp }
            .ifEmpty { prepared }
        val near = candidates.map { it.topic.id to nearbyScore(tokens, it.keywords) }
            .filter { it.second > 0 }.sortedByDescending { it.second }.map { it.first }
        val available = candidates.map { it.topic.id }
        val defaults = if (appContext) defaultHelpSuggestions else defaultSuggestions
        val suggestions = (near + defaults.filter { it in available } + available)
            .distinct().take(4)
        return OfflineCoachReply(null,
            if (appContext) text("Je n’ai pas encore de réponse prévue pour cette question sur Selkie. Choisis une rubrique d’aide ci-dessous ou reformule ta question.",
                "I don’t have a prepared answer to this question about Selkie yet. Choose a help topic below or rephrase your question.")
            else text("Je n’ai pas encore de conseil prévu pour cette phrase. Je réponds à partir de conseils écrits à l’avance. Choisis un sujet ci-dessous ou reformule ta question.",
                "I don’t have prepared advice for this message yet. I use advice written in advance. Choose a topic below or rephrase your question."),
            suggestions = suggestions, unrecognized = true)
    }

    private fun hasAppContext(tokens: List<String>): Boolean = tokens.any { it in appContextWords }

    /** Les verbes seuls sont ambigus : une action doit aussi désigner sa cible. */
    private fun requestedAppAction(tokens: List<String>): String? {
        val namedApp = prepared.any { entry ->
            entry.topic.appAliases.any { containsWords(tokens, words(it)) }
        }
        val appTarget = tokens.any { it in appTargetWords } || namedApp
        val subscriptionTarget = tokens.any { it in subscriptionWords }
        val pointTarget = tokens.any { it in pointWords }
        return when {
            pointTarget && tokens.any { it in gainingWords } -> "earn_points"
            pointTarget && tokens.any { it in losingWords } -> "lose_points"
            subscriptionTarget && tokens.any { it in cancellationWords } &&
                tokens.none { it in challengeWords } -> "cancel_subscription"
            subscriptionTarget && tokens.any { it in restorationWords } &&
                tokens.none { it in pointWords || it in challengeWords } -> "restore_subscription"
            subscriptionTarget && tokens.any { it in paymentWords } -> "payments"
            appTarget && tokens.any { it in addingWords } &&
                (namedApp || tokens.none { it in pointWords }) -> "add_app"
            appTarget && tokens.any { it in limitWords } &&
                tokens.any { it in editingWords } -> "limits"
            appTarget && tokens.any { it in blockedWords } -> "red_screen"
            appTarget && tokens.any { it in removalWords } &&
                (tokens.any { it in trackingWords } ||
                    (tokens.any { it in trackedRemovalWords } && tokens.none { it in phoneWords })) -> "remove_app"
            appTarget && tokens.any { it in removalWords } -> "uninstall"
            appTarget && tokens.any { it in durationWords } &&
                (tokens.any { it in measurementQuestionWords } ||
                    containsWords(tokens, words("how much")) || containsWords(tokens, words("how long")) ||
                    tokens.any { it in viewingWords }) -> "usage"
            else -> null
        }
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
        text("Tu n’as pas à traverser une situation aussi difficile seul. Contacte maintenant une personne de confiance et dis-lui ce qui se passe. En France, tu peux appeler le 3114, gratuitement, 24 h/24. Si tu es en danger immédiat, appelle le 112 ou rejoins les urgences. Ce coach ne peut pas évaluer une urgence ni remplacer une aide humaine.",
            "You don’t have to face something this difficult alone. Contact someone you trust now and tell them what is happening. In France, you can call 3114 free of charge, 24/7. If you are in immediate danger, call 112 in France or the EU, or your local emergency number elsewhere. Outside France, contact a local crisis helpline. This coach cannot assess an emergency or replace human support."),
        sensitive = true)

    companion object {
        private val pointTopics = setOf("points", "earn_points", "lose_points", "zero_points", "discipline_bonus",
            "exceeded", "relapse", "streak")
        private val actionTopics = setOf("reset", "limits", "add_app", "permissions", "custom_challenges",
            "challenges", "pause_tracking", "usage", "red_screen", "points", "premium")
        private val defaultSuggestions = listOf("tiktok", "sleep", "boredom", "points")
        private val defaultHelpSuggestions = listOf("app_overview", "navigation", "premium", "privacy")
        private val brandKeywords = setOf(listOf("selkie"), listOf("maree"))
        private val genericNavigationKeywords = setOf(
            listOf("ou", "trouver"), listOf("ou", "aller"), listOf("ou", "ouvrir"),
            listOf("comment", "ca", "marche"), listOf("comment", "faire"), listOf("comment", "utiliser"),
            listOf("where", "to", "find"), listOf("where", "to", "go"), listOf("how", "to", "use")
        )
        private val appContextWords = setOf("selkie", "maree", "application", "applications", "app", "apps",
            "suivi", "premium", "ultra", "abonnement", "abonnements", "compte", "confidentialite", "rgpd",
            "renouvele", "renouvellement", "prelevement", "tracking", "subscription", "subscriptions", "account", "privacy", "gdpr", "renewal", "renew", "billing")
        private val appTargetWords = setOf("application", "applications", "app", "apps", "suivi", "tracking", "tracked")
        private val subscriptionWords = setOf("abonnement", "abonnements", "premium", "ultra", "achat", "achats", "subscription", "subscriptions", "purchase", "purchases", "plan")
        private val cancellationWords = setOf("annuler", "resilier", "resiliation", "annulation", "desabonner", "desabonnement", "cancel", "cancelling", "cancellation", "unsubscribe")
        private val challengeWords = setOf("defi", "defis", "challenge", "challenges")
        private val pointWords = setOf("point", "points", "solde", "balance")
        private val gainingWords = setOf("gagner", "regagner", "obtenir", "recuperer", "augmenter", "earn", "earning", "gain", "get", "increase")
        private val losingWords = setOf("perdre", "perdu", "perds", "perd", "retires", "lose", "lost", "losing", "deducted")
        private val restorationWords = setOf("restaurer", "restauration", "recuperer", "retrouver", "restore", "restoration", "recover")
        private val paymentWords = setOf("payer", "paiement", "paiements", "acheter", "facturation", "prelevement", "pay", "payment", "payments", "buy", "billing", "charged")
        private val addingWords = setOf("ajouter", "ajoute", "suivre", "selectionner", "add", "adding", "track", "select")
        private val limitWords = setOf("limite", "limites", "limit", "limits")
        private val editingWords = setOf("modifier", "changer", "regler", "fixer", "choisir", "edit", "change", "set", "choose")
        private val blockedWords = setOf("bloque", "bloquee", "bloques", "bloquees", "verrouille", "verrouillee", "blocked", "locked")
        private val removalWords = setOf("retirer", "supprimer", "enlever", "desinstaller", "remove", "delete", "uninstall")
        private val trackedRemovalWords = setOf("retirer", "enlever", "remove")
        private val phoneWords = setOf("telephone", "phone", "device")
        private val trackingWords = setOf("suivi", "selkie", "maree", "tracking", "tracked")
        private val durationWords = setOf("temps", "minutes", "minute", "duree", "utilisation", "usage", "time", "duration", "used")
        private val measurementQuestionWords = setOf("combien", "quelle", "quel", "what")
        private val viewingWords = setOf("voir", "consulter", "afficher", "trouver", "see", "view", "show", "find")
        private val noFuzzyKeywords = setOf("compte", "compteur", "profil", "account", "profile")
        private val harmWords = setOf("automutilation", "automutiler", "suicide", "suicidaire", "selfharm", "suicidal")
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
            "kill myself", "hurt myself", "want to die", "end my life", "take my own life",
            "harm myself", "self harm", "i don t want to live", "i do not want to live",
            "i don t want to be alive", "i do not want to be alive", "i feel extremely unwell"
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
                        changed <= 1 && expected.length >= 6 && expected !in noFuzzyKeywords && actual.length >= 5 &&
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
