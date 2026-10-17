package com.maree.app

import org.junit.Assert.*
import org.junit.Test
import java.text.Normalizer
import java.util.Locale
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
            "Comment gagner des POINTS D’HONNEUR ?" to "earn_points",
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

    @Test fun appHelpAnswersNaturalQuestionsAboutNavigationAndSubscriptions() {
        val coach = RuleCoach(random = Random(4))
        for ((message, expected) in listOf(
            "Quels sont les onglets de Selkie ?" to "navigation",
            "Où aller pour mes exercices quotidiens ?" to "daily",
            "Comment ajouter TikTok ?" to "add_app",
            "Je veux suivre YouTube" to "add_app",
            "Combien de temps ai-je utilisé TikTok ?" to "usage",
            "Comment retirer TikTok du suivi ?" to "remove_app",
            "Comment retirer TikTok ?" to "remove_app",
            "Comment modifier la limite de TikTok ?" to "limits",
            "Pourquoi TikTok est bloqué ?" to "red_screen",
            "Comment annuler mon abonnement premium ?" to "cancel_subscription",
            "Je veux résilier Premium" to "cancel_subscription",
            "Est-ce renouvelé tous les mois ?" to "subscription_monthly",
            "Combien coûte l'abonnement par an ?" to "subscription_annual",
            "Comment restaurer mon achat Premium ?" to "restore_subscription",
            "Comment payer mon abonnement Premium ?" to "payments"
        )) {
            assertEquals(message, expected, coach.reply(message, context).topicId)
        }
    }

    @Test fun appHelpExplainsPrivacyContactAndAccountsWithoutPretendingToPerformActions() {
        val coach = RuleCoach(random = Random(12))
        for ((message, expected) in listOf(
            "Où est la politique de confidentialité ?" to "privacy",
            "Comment contacter Selkie par email ?" to "contact",
            "Est-ce que je dois créer un compte ?" to "account",
            "Je veux effacer toutes mes données" to "delete_data",
            "Puis-je utiliser un seul compte sur plusieurs téléphones ?" to "single_device"
        )) {
            val reply = coach.reply(message, context)
            assertEquals(message, expected, reply.topicId)
            assertFalse(message, reply.unrecognized)
            assertFalse(message, reply.sensitive)
            assertFalse(message, reply.text.contains("Suivi enregistré"))
        }
    }

    @Test fun unrecognizedAppQuestionsOfferHelpAndOtherUnknownQuestionsOfferAdvice() {
        val coach = RuleCoach()
        for (message in listOf("Selkie peut-elle traduire des poèmes ?", "L'application peut-elle dessiner un dinosaure ?")) {
            val reply = coach.reply(message, context)
            assertTrue(message, reply.unrecognized)
            assertEquals(4, reply.suggestions.size)
            assertTrue(message, reply.suggestions.all { id -> coachTopics.any { it.id == id && it.appHelp } })
            assertTrue(message, reply.text.contains("question sur Selkie"))
            reply.suggestions.forEach { id -> assertEquals(id, coach.replyForTopic(id, context).topicId) }
        }
        val unrelated = coach.reply("Je regarde les dinosaures dans le musée", context)
        assertTrue(unrelated.unrecognized)
        assertEquals(4, unrelated.suggestions.size)
        assertTrue(unrelated.suggestions.all { id -> coachTopics.any { it.id == id && !it.appHelp } })
    }

    @Test fun brandAndVagueNavigationWordsDoNotReplaceSleepEmotionOrAppAdvice() {
        val coach = RuleCoach()
        for ((message, expected) in listOf(
            "Selkie, je n'arrive pas à dormir" to "sleep",
            "Où trouver des conseils pour dormir ?" to "sleep",
            "Je suis seul et je regarde Selkie" to "loneliness",
            "Je passe mes soirées sur TikTok" to "tiktok",
            "Je passe trop de temps sur TikTok" to "tiktok"
        )) {
            assertEquals(message, expected, coach.reply(message, context).topicId)
        }
        assertTrue(coach.reply("J'aimerais compter des poissons", context).unrecognized)
        val distress = coach.reply("Comment annuler Selkie ? Je veux me faire du mal", context)
        assertTrue(distress.sensitive)
        assertTrue(distress.suggestions.isEmpty())
    }

    @Test fun englishAndFrenchCataloguesHaveTheSameTopicsAndCompleteHelp() {
        val french = coachTopicsFor(AppLocale.FRENCH)
        val english = coachTopicsFor(AppLocale.ENGLISH)
        assertEquals(67, english.size)
        assertEquals(french.map { it.id }, english.map { it.id })
        assertEquals(125, english.sumOf { it.replies.size })
        assertTrue(english.all { it.label.isNotBlank() && it.keywords.isNotEmpty() &&
            it.replies.all(String::isNotBlank) })
        assertEquals(38, helpEntriesFor(AppLocale.ENGLISH).size)
        assertEquals(helpEntriesFor(AppLocale.FRENCH).map { it.topic.id },
            helpEntriesFor(AppLocale.ENGLISH).map { it.topic.id })
        assertTrue(helpEntriesFor(AppLocale.ENGLISH).all { it.topic.appHelp })
    }

    @Test fun englishHelpRecognizesSubscriptionAndTrackingQuestions() {
        val coach = RuleCoach(random = Random(14), language = AppLocale.ENGLISH)
        for ((message, expected) in listOf(
            "How do I cancel my Premium subscription?" to "cancel_subscription",
            "How much does the annual subscription cost?" to "subscription_annual",
            "Is the subscription renewed every month?" to "subscription_monthly",
            "How do I restore my Premium purchase?" to "restore_subscription",
            "How do I add TikTok?" to "add_app",
            "How do I change the TikTok limit?" to "limits",
            "Why is TikTok blocked?" to "red_screen",
            "How much time have I used TikTok?" to "usage",
            "How do I remove TikTok from tracking?" to "remove_app",
            "Where is the privacy policy?" to "privacy"
        )) {
            assertEquals(message, expected, coach.reply(message, context).topicId)
        }
        val cancellation = coach.replyForTopic("cancel_subscription", context).text
        assertTrue(cancellation, cancellation.contains("Google Play"))
        assertFalse(cancellation.contains("Your subscription has been cancelled"))
        val annual = coach.replyForTopic("subscription_annual", context).text
        assertTrue(annual, annual.contains("60") && annual.contains("120"))
        assertTrue(annual, annual.contains("year", ignoreCase = true))
    }

    @Test fun englishAdviceUnderstandsTyposAndDoesNotConfuseOveruseWithUsageHelp() {
        val coach = RuleCoach(random = Random(3), language = AppLocale.ENGLISH)
        for ((message, expected) in listOf(
            "I spend too much time on tiktoc!" to "tiktok",
            "I can’t sleep at night" to "sleep",
            "Turn off notificatons" to "notifications"
        )) {
            assertEquals(message, expected, coach.reply(message, context).topicId)
        }
        val usage = coach.reply("tiktoc", context).text
        assertTrue(usage, usage.contains("TikTok: 40 min during the current tracking period"))
        assertFalse(usage.contains("today", ignoreCase = true))
        assertFalse(coach.reply("TikTok", context.copy(trackingEnabled = false)).text
            .contains("Recorded usage"))
    }

    @Test fun languageSwitchUsesTheSameBalanceAndKeepsEachCoachLanguageIndependent() {
        val french = RuleCoach(random = Random(4), language = AppLocale.FRENCH)
        val english = RuleCoach(random = Random(4), language = AppLocale.ENGLISH)
        val frenchPoints = french.replyForTopic("points", context).text
        val englishPoints = english.replyForTopic("points", context).text
        assertTrue(frenchPoints, frenchPoints.contains("35,25 points"))
        assertTrue(englishPoints, englishPoints.contains("35.25 points"))
        assertEquals(141, context.pointUnits)
        // Les mots des deux langues n’imposent pas la langue des réponses.
        assertTrue(english.reply("Comment gagner des points ?", context).text.contains("35.25 points"))
        assertTrue(french.reply("How do I earn points?", context).text.contains("35,25 points"))
        assertTrue(english.replyForTopic("points", context.copy(pointUnits = 2)).text.contains("0.5 points"))
        assertTrue(english.replyForTopic("points", context.copy(pointUnits = -8)).text.contains("0 points"))
        assertTrue(french.replyForTopic("points", context).text.contains("35,25 points"))
    }

    @Test fun coachShowsAtMostOneHundredPointsWithoutChangingItsContext() {
        val legacyBalance = context.copy(pointUnits = 160 * POINT_UNITS_PER_POINT)
        for (language in AppLocale.entries) {
            val coach = RuleCoach(language = language)
            val reply = coach.replyForTopic("points", legacyBalance).text
            assertTrue(reply, reply.contains("100 points"))
            assertFalse(reply, reply.contains("160 points"))
            assertTrue(coach.replyForTopic("points", context.copy(pointUnits = 0)).text.contains("0 points"))
            assertTrue(coach.replyForTopic("points", context).text.contains(
                if (language == AppLocale.ENGLISH) "35.25 points" else "35,25 points"))
        }
        assertEquals(160 * POINT_UNITS_PER_POINT, legacyBalance.pointUnits)
    }

    @Test fun explainsOptionalPointsDiceWithoutConfusingItWithTheCalendarModes() {
        val french = RuleCoach(language = AppLocale.FRENCH)
            .reply("Comment marche le mode Points du dé ?", context)
        val english = RuleCoach(language = AppLocale.ENGLISH)
            .reply("How does dice points mode work?", context)
        for (reply in listOf(french, english)) {
            assertEquals("lose_points", reply.topicId)
            assertFalse(reply.unrecognized)
        }
        assertTrue(french.text, french.text.contains("10 à 60 points"))
        assertTrue(french.text, french.text.contains("gratuit et facultatif"))
        assertTrue(french.text, french.text.contains("ne changent pas tes points"))
        assertTrue(english.text, english.text.contains("10 to 60 points"))
        assertTrue(english.text, english.text.contains("free and optional"))
        assertTrue(english.text, english.text.contains("do not change your points"))
        assertEquals(141, context.pointUnits)
    }

    @Test fun englishUnknownQuestionsHaveHonestFallbackAndTranslatedSuggestions() {
        val coach = RuleCoach(language = AppLocale.ENGLISH)
        val unknown = coach.reply("Which dinosaur has the longest tail?", context)
        assertTrue(unknown.unrecognized)
        assertFalse(unknown.sensitive)
        assertNull(unknown.topicId)
        assertEquals(4, unknown.suggestions.size)
        assertTrue(unknown.text, unknown.text.contains("written in advance"))
        assertFalse(unknown.text.contains("35.25"))
        val appUnknown = coach.reply("Can Selkie translate poems?", context)
        assertTrue(appUnknown.unrecognized)
        assertTrue(appUnknown.text, appUnknown.text.contains("question about Selkie"))
        val topics = coachTopicsFor(AppLocale.ENGLISH).associateBy { it.id }
        assertTrue(appUnknown.suggestions.all { topics[it]?.appHelp == true })
        assertEquals("Sleep", topics.getValue("sleep").label)
        assertEquals("Boredom", topics.getValue("boredom").label)
        appUnknown.suggestions.forEach { id ->
            assertEquals(id, coach.replyForTopic(id, context).topicId)
        }
    }

    @Test fun englishSeriousDistressGivesHumanSupportBeforeSubscriptionOrAppAdvice() {
        val coach = RuleCoach(language = AppLocale.ENGLISH)
        for (message in listOf("TikTok makes me want to hurt myself", "I want to die",
            "I don’t want to be alive", "I want to end my life", "Premium and suicide")) {
            val reply = coach.reply(message, context)
            assertTrue(message, reply.sensitive)
            assertFalse(reply.unrecognized)
            assertNull(reply.topicId)
            assertTrue(reply.text, reply.text.contains("someone you trust"))
            assertTrue(reply.text.contains("In France") && reply.text.contains("3114"))
            assertTrue(reply.text.contains("112") && reply.text.contains("local emergency number"))
            assertFalse(reply.text.contains("35.25"))
            assertTrue(reply.suggestions.isEmpty())
        }
        for (message in listOf("I feel sad", "This TikTok video has me dying of laughter", "My foot hurts")) {
            assertFalse(message, coach.reply(message, context).sensitive)
        }
    }

    @Test fun completeQuestionLibraryIsAvailableInBothLanguagesWithExistingParentTopics() {
        val french = preparedCoachQuestionsFor(AppLocale.FRENCH)
        val english = preparedCoachQuestionsFor(AppLocale.ENGLISH)
        assertTrue("The coach needs more than 1,000 prepared question-answer pairs", french.size > 1_000)
        assertEquals(french.map { it.id }, english.map { it.id })
        assertEquals(french.size, french.map { it.id }.distinct().size)
        for ((language, questions) in listOf(AppLocale.FRENCH to french, AppLocale.ENGLISH to english)) {
            val coach = RuleCoach(language = language)
            val topicIds = coachTopicsFor(language).map { it.id }.toSet()
            val noMeasurements = context.copy(trackingEnabled = false)
            for (question in questions) {
                assertTrue(question.id, question.question.isNotBlank() && question.answer.isNotBlank())
                assertTrue(question.id, question.topicId in topicIds)
                val reply = coach.replyForPreparedQuestion(question.id, noMeasurements)
                assertEquals(question.id, question.topicId, reply.topicId)
                assertTrue(question.id, reply.text.startsWith(question.answer))
                assertFalse(question.id, reply.unrecognized || reply.sensitive)
            }
        }
    }

    @Test fun exactPreparedQuestionKeepsItsFullAnswerRatherThanOneGenericTopicReply() {
        val question = preparedCoachQuestionsFor(AppLocale.FRENCH).first { it.topicId == "tiktok" }
        val coach = RuleCoach(language = AppLocale.FRENCH)
        val noMeasurements = context.copy(trackingEnabled = false)
        val typed = coach.reply(question.question, noMeasurements)
        val selected = coach.replyForPreparedQuestion(question.id, noMeasurements)
        assertEquals(question.answer, typed.text)
        assertEquals(question.answer, selected.text)
        assertEquals(question.topicId, typed.topicId)
        assertFalse(typed.unrecognized)
        assertFalse(coachTopicsFor(AppLocale.FRENCH).first { it.id == "tiktok" }.replies.contains(typed.text))
    }

    @Test fun preparedQuestionsIgnoreAccentsPunctuationCaseAndApostropheStyle() {
        val question = preparedCoachQuestionsFor(AppLocale.FRENCH).first { it.topicId == "tiktok" }
        val normalized = Normalizer.normalize(question.question, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "").uppercase(Locale.ROOT)
            .replace('’', '\'').replace('?', '!') + " !!!"
        val reply = RuleCoach(language = AppLocale.FRENCH)
            .reply(normalized, context.copy(trackingEnabled = false))
        assertEquals(question.answer, reply.text)
        assertEquals(question.topicId, reply.topicId)
    }

    @Test fun aPreparedQuestionInEitherLanguageGetsTheChosenCoachLanguage() {
        val french = preparedCoachQuestionsFor(AppLocale.FRENCH).first { it.topicId == "tiktok" }
        val english = preparedCoachQuestionsFor(AppLocale.ENGLISH).first { it.id == french.id }
        val noMeasurements = context.copy(trackingEnabled = false)
        val frenchCoach = RuleCoach(language = AppLocale.FRENCH)
        val englishCoach = RuleCoach(language = AppLocale.ENGLISH)
        assertEquals(english.answer, englishCoach.reply(french.question, noMeasurements).text)
        assertEquals(french.answer, frenchCoach.reply(english.question, noMeasurements).text)
        assertEquals(english.answer, englishCoach.replyForPreparedQuestion(french.id, noMeasurements).text)
        assertEquals(french.answer, frenchCoach.replyForPreparedQuestion(english.id, noMeasurements).text)
    }

    @Test fun preparedAppAnswersAppendOnlyTheRealMatchingCurrentTrackingPeriod() {
        val question = preparedCoachQuestionsFor(AppLocale.ENGLISH).first { it.topicId == "tiktok" }
        val coach = RuleCoach(language = AppLocale.ENGLISH)
        val measured = coach.replyForPreparedQuestion(question.id, context).text
        assertTrue(measured, measured.startsWith(question.answer))
        assertTrue(measured, measured.endsWith("TikTok: 40 min during the current tracking period."))
        assertEquals(question.answer, coach.replyForPreparedQuestion(question.id,
            context.copy(trackingEnabled = false)).text)
        assertEquals(question.answer, coach.replyForPreparedQuestion(question.id,
            context.copy(apps = listOf(OfflineCoachApp("YouTube", 6_000_000L)))).text)
        assertEquals(question.answer, coach.replyForPreparedQuestion(question.id,
            context.copy(apps = listOf(OfflineCoachApp("TikTok", -1L)))).text)
    }

    @Test fun preparedPointAnswersUseExactQuarterPointsInTheChosenLanguage() {
        for ((language, balance) in listOf(AppLocale.FRENCH to "35,25", AppLocale.ENGLISH to "35.25")) {
            val question = preparedCoachQuestionsFor(language).first {
                it.topicId in setOf("points", "exceeded", "relapse", "streak")
            }
            val coach = RuleCoach(language = language)
            val reply = coach.replyForPreparedQuestion(question.id, context)
            assertTrue(reply.text, reply.text.startsWith(question.answer))
            assertTrue(reply.text, reply.text.endsWith("$balance points."))
            assertFalse(reply.text.contains("Recorded usage") || reply.text.contains("Suivi enregistré"))
            assertTrue(coach.replyForPreparedQuestion(question.id,
                context.copy(pointUnits = -5)).text.endsWith("0 points."))
        }
    }

    @Test fun distressStillTakesPriorityWhenAQuestionAlsoContainsLibraryWords() {
        for ((language, distress) in listOf(AppLocale.FRENCH to "Je veux me faire du mal",
            AppLocale.ENGLISH to "I want to hurt myself")) {
            val question = preparedCoachQuestionsFor(language).first { it.topicId == "tiktok" }
            val reply = RuleCoach(language = language).reply(question.question + " " + distress, context)
            assertTrue(reply.sensitive)
            assertFalse(reply.unrecognized)
            assertNull(reply.topicId)
            assertTrue(reply.text.contains("3114"))
            assertFalse(reply.text.contains("35,25") || reply.text.contains("35.25"))
            assertFalse(reply.text.contains("Recorded usage") || reply.text.contains("Suivi enregistré"))
        }
    }

    @Test fun missingPreparedQuestionAndExplicitCustomCatalogueKeepHonestFallback() {
        val coach = RuleCoach(language = AppLocale.ENGLISH)
        val missing = coach.replyForPreparedQuestion("missing-question", context)
        assertTrue(missing.unrecognized)
        assertEquals(4, missing.suggestions.size)
        assertFalse(missing.sensitive)
        val question = preparedCoachQuestionsFor(AppLocale.ENGLISH).first { it.topicId == "tiktok" }
        val custom = RuleCoach(listOf(CoachTopic("custom", "Custom", listOf("sapphire"),
            listOf("A custom answer."))), language = AppLocale.ENGLISH)
        assertTrue(custom.replyForPreparedQuestion(question.id, context).unrecognized)
        assertTrue(custom.reply(question.question, context).unrecognized)
        assertEquals("A custom answer.", custom.reply("sapphire", context).text)
    }
}
