package com.maree.app

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class RuleCoachTest {
    private val context = OfflineCoachContext(141, true, listOf(OfflineCoachApp("TikTok", 2_430_000L)))
    private fun topic(id: String, vararg words: String, aliases: List<String> = emptyList()) =
        CoachTopic(id, id, words.toList(), listOf("Premier conseil.", "Deuxième conseil.", "Troisième conseil."), aliases)

    @Test fun understandsFrenchAccentsPunctuationApostrophesAndLigatures() {
        val coach = RuleCoach(listOf(topic("sleep", "j arrive pas a dormir"), topic("heart", "coeur")))
        assertEquals("sleep", coach.reply("J’arrive pas à DORMIR !!!", context).topicId)
        assertEquals("heart", coach.reply("J’ai le cœur lourd.", context).topicId)
    }

    @Test fun recognizesApplicationTyposAndOneSmallLongWordTypo() {
        val coach = RuleCoach(listOf(topic("tiktok", "tiktok"), topic("youtube", "youtube"),
            topic("notifications", "notifications")))
        assertEquals("tiktok", coach.reply("Je scrolle sur tiktoc", context).topicId)
        assertEquals("youtube", coach.reply("youtub", context).topicId)
        assertEquals("notifications", coach.reply("Couper les notificatons", context).topicId)
        assertEquals("notifications", coach.reply("Couper les notificaitons", context).topicId)
    }

    @Test fun doesNotMatchInsideWordsOrFuzzilyMatchShortWords() {
        val coach = RuleCoach(listOf(topic("points", "points"), topic("sleep", "lit"), topic("reading", "livre")))
        for (message in listOf("Quels sont les appoints ?", "J’ai acheté un litre de lait", "Je suis libre", "un joint")) {
            assertTrue(message, coach.reply(message, context).unrecognized)
        }
    }

    @Test fun bestMatchingSpecificPhraseWinsAndRepeatedKeywordDoesNotInflateScore() {
        val coach = RuleCoach(listOf(topic("screen", "ecran"), topic("sleep", "ecran avant de dormir")))
        assertEquals("sleep", coach.reply("écran écran écran avant de dormir", context).topicId)
    }

    @Test fun actionIntentBreaksAnAppNameTieButNeverOverridesAHigherScore() {
        val coach = RuleCoach(listOf(topic("tiktok", "tiktok", "videos"), topic("reset", "reset")))
        assertEquals("reset", coach.reply("reset TikTok", context).topicId)
        assertEquals("tiktok", coach.reply("reset TikTok videos", context).topicId)
    }

    @Test fun consecutiveRepliesOfSameTopicDoNotRepeatEvenWithOtherTopicBetween() {
        val coach = RuleCoach(listOf(topic("tiktok", "tiktok"), topic("sleep", "sommeil")), Random(20))
        var previous = coach.replyForTopic("tiktok", context).text
        repeat(20) {
            coach.replyForTopic("sleep", context)
            val next = coach.replyForTopic("tiktok", context).text
            assertNotEquals(previous, next)
            previous = next
        }
    }

    @Test fun addsOnlyRealMatchingAppUsageFromCurrentPeriodWhenTrackingIsEnabled() {
        val coach = RuleCoach(listOf(topic("tiktok", "tiktok", aliases = listOf("tiktok"))))
        val measured = coach.reply("TikTok", context).text
        assertTrue(measured, measured.contains("TikTok : 40 min"))
        assertTrue(measured, measured.contains("période de suivi en cours"))
        assertFalse(measured, measured.contains("aujourd’hui"))
        assertFalse(coach.reply("TikTok", context.copy(trackingEnabled = false)).text.contains("min"))
        assertFalse(coach.reply("TikTok", context.copy(apps = listOf(OfflineCoachApp("YouTube", 9_000_000)))).text.contains("min"))
    }

    @Test fun printsExactQuarterPointBalanceOnlyForRelevantTopics() {
        val coach = RuleCoach(listOf(topic("points", "points"), topic("sleep", "sommeil")))
        assertTrue(coach.reply("mes points", context).text.contains("35,25 points"))
        assertFalse(coach.reply("sommeil", context).text.contains("35,25"))
        assertTrue(coach.reply("points", context.copy(pointUnits = 0)).text.contains("0 points"))
    }

    @Test fun unknownQuestionsOfferAvailableTopicsWithoutInventingAnAnswer() {
        val coach = RuleCoach(listOf(topic("tiktok", "tiktok"), topic("sleep", "sommeil"),
            topic("boredom", "ennui"), topic("points", "points")))
        val reply = coach.reply("Quelle est la capitale du Pérou ?", context)
        assertTrue(reply.unrecognized)
        assertFalse(reply.sensitive)
        assertNull(reply.topicId)
        assertEquals(listOf("tiktok", "sleep", "boredom", "points"), reply.suggestions)
        assertTrue(reply.text.contains("écrits à l’avance"))
        assertFalse(reply.text.lowercase().contains("erreur"))
    }

    @Test fun seriousDistressTakesPriorityOverAppAdviceAndPointData() {
        val coach = RuleCoach(listOf(topic("tiktok", "tiktok"), topic("points", "points")))
        for (message in listOf("TikTok me rend triste et je veux me faire du mal", "Je veux mourir, j’ai zéro points",
            "J’en peux plus", "je vais très mal", "Je pense au suicide")) {
            val reply = coach.reply(message, context)
            assertTrue(message, reply.sensitive)
            assertFalse(reply.unrecognized)
            assertNull(reply.topicId)
            assertTrue(reply.text.contains("3114"))
            assertTrue(reply.text.contains("112"))
            assertFalse(reply.text.contains("35,25"))
            assertFalse(reply.text.contains("Suivi enregistré"))
            assertTrue(reply.suggestions.isEmpty())
        }
    }

    @Test fun benignMentionsOfDeathOrFeelingBadAreNotMistakenForImmediateDanger() {
        val coach = RuleCoach(listOf(topic("tiktok", "tiktok")))
        assertFalse(coach.reply("Cette vidéo TikTok me fait mourir de rire", context).sensitive)
        assertFalse(coach.reply("Je vais mourir de rire devant cette vidéo TikTok", context).sensitive)
        assertFalse(coach.reply("J’ai mal au pied", context).sensitive)
    }

    @Test fun gracefullyHandlesUnknownTopicAndSingleReplyCatalogue() {
        val coach = RuleCoach(listOf(CoachTopic("sleep", "Sommeil", listOf("sommeil"), listOf("Un conseil."))))
        assertEquals("Un conseil.", coach.replyForTopic("sleep", context).text)
        assertEquals("Un conseil.", coach.replyForTopic("sleep", context).text)
        assertTrue(coach.replyForTopic("missing", context).unrecognized)
    }

    @Test fun actualCatalogueUnderstandsCommonUserQuestionsAndSelectsTheirIntent() {
        val coach = RuleCoach(random = Random(6))
        for ((message, expected) in listOf(
            "Je passe mes soirées sur Tiktoc !" to "tiktok",
            "Le soir, je n’arrive pas à dormir" to "sleep",
            "Comment gagner des POINTS D’HONNEUR ?" to "points",
            "Je veux réinitialiser TikTok" to "reset",
            "Réinitialiser TikTok" to "reset",
            "Je veux créer mon défi personnalisé" to "custom_challenges",
            "J’ai craqué et recommencé à scroller" to "relapse"
        )) {
            assertEquals(message, expected, coach.reply(message, context).topicId)
        }
    }

    @Test fun actualCataloguePreservesAppMeasurementsAndDoesNotInventAStreak() {
        val coach = RuleCoach()
        val appReply = coach.reply("Tiktoc", context).text
        assertTrue(appReply, appReply.contains("TikTok : 40 min sur la période de suivi en cours"))
        val gameContext = context.copy(apps = context.apps + OfflineCoachApp("Free Fire", 600_000))
        val gameReply = coach.reply("Free Fire", gameContext).text
        assertTrue(gameReply, gameReply.contains("Free Fire : 10 min"))
        assertFalse(gameReply.contains("TikTok :"))
        val streakReply = coach.reply("Comment garder ma série de jours réussis ?", context)
        assertEquals("streak", streakReply.topicId)
        assertTrue(streakReply.text.contains("35,25 points"))
        assertFalse(streakReply.text.contains("Tu as réussi"))
        assertFalse(streakReply.text.contains("Tu as tenu"))
    }

    @Test fun actualCatalogueUnknownQuestionsOfferFourTopicsAndRespectWordBoundaries() {
        val coach = RuleCoach()
        for (message in listOf("Je prends un litre de lait", "Une histoire de dinosaures", "Quelle est la capitale du Pérou ?")) {
            val reply = coach.reply(message, context)
            assertTrue(message, reply.unrecognized)
            assertEquals(4, reply.suggestions.size)
            assertTrue(reply.suggestions.all { id -> coachTopics.any { it.id == id } })
            assertFalse(reply.text.contains("35,25"))
            assertFalse(reply.text.contains("Suivi enregistré"))
        }
    }
}
