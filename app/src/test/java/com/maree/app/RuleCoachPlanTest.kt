package com.maree.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The library is tiered; typing, application help and human support are not quotas. */
class RuleCoachPlanTest {
    private val context = OfflineCoachContext(pointUnits = 0, trackingEnabled = false, apps = emptyList())

    private fun exclusiveQuestion(plan: PlanType, excludedPlan: PlanType,
        language: AppLocale): PreparedCoachQuestion =
        preparedCoachQuestionsFor(language, plan).first {
            preparedCoachQuestionById(it.id, language, excludedPlan) == null
        }

    @Test fun defaultCoachUsesFreeLibraryAndCannotRetrievePremiumAnswersByIdOrTypedQuestion() {
        for (language in AppLocale.entries) {
            val coach = RuleCoach(language = language)
            val freeQuestion = preparedCoachQuestionsFor(language, PlanType.FREE).first()
            assertTrue(coach.replyForPreparedQuestion(freeQuestion.id, context).text.startsWith(freeQuestion.answer))

            val premiumQuestion = exclusiveQuestion(PlanType.PREMIUM, PlanType.FREE, language)
            assertTrue(coach.replyForPreparedQuestion(premiumQuestion.id, context).unrecognized)
            // A free coach can still offer its usual topic advice, without returning the paid entry.
            assertNotEquals(premiumQuestion.answer, coach.reply(premiumQuestion.question, context).text)
        }
    }

    @Test fun premiumIncludesTheFreeAndPremiumLibrariesButCannotSelectUltraEntries() {
        for (language in AppLocale.entries) {
            val coach = RuleCoach(language = language, plan = PlanType.PREMIUM)
            val freeQuestion = preparedCoachQuestionsFor(language, PlanType.FREE).first()
            val premiumQuestion = exclusiveQuestion(PlanType.PREMIUM, PlanType.FREE, language)
            for (question in listOf(freeQuestion, premiumQuestion)) {
                val reply = coach.replyForPreparedQuestion(question.id, context)
                assertFalse(reply.unrecognized)
                assertTrue(reply.text, reply.text.startsWith(question.answer))
            }
            val ultraQuestion = exclusiveQuestion(PlanType.ULTRA, PlanType.PREMIUM, language)
            assertTrue(coach.replyForPreparedQuestion(ultraQuestion.id, context).unrecognized)
            assertNotEquals(ultraQuestion.answer, coach.reply(ultraQuestion.question, context).text)
        }
    }

    @Test fun ultraCanRetrieveEveryTierAndAnswerATranslatedUltraQuestionInItsSelectedLanguage() {
        for (language in AppLocale.entries) {
            val alternate = if (language == AppLocale.FRENCH) AppLocale.ENGLISH else AppLocale.FRENCH
            val coach = RuleCoach(language = language, plan = PlanType.ULTRA)
            val freeQuestion = preparedCoachQuestionsFor(language, PlanType.FREE).first()
            val premiumQuestion = exclusiveQuestion(PlanType.PREMIUM, PlanType.FREE, language)
            val ultraQuestion = exclusiveQuestion(PlanType.ULTRA, PlanType.PREMIUM, language)
            for (question in listOf(freeQuestion, premiumQuestion, ultraQuestion)) {
                val reply = coach.replyForPreparedQuestion(question.id, context)
                assertFalse(reply.unrecognized)
                assertTrue(reply.text, reply.text.startsWith(question.answer))
            }
            val translated = requireNotNull(preparedCoachQuestionById(ultraQuestion.id, alternate, PlanType.ULTRA))
            assertTrue(coach.reply(translated.question, context).text.startsWith(ultraQuestion.answer))
        }
    }

    @Test fun seriousDistressStillPrecedesLibraryAccessOnEveryPlanAtZeroPoints() {
        for (language in AppLocale.entries) {
            val question = exclusiveQuestion(PlanType.ULTRA, PlanType.PREMIUM, language)
            val distress = if (language == AppLocale.FRENCH) "Je veux me faire du mal" else "I want to hurt myself"
            for (plan in PlanType.entries) {
                val reply = RuleCoach(language = language, plan = plan)
                    .reply("${question.question} $distress", context)
                assertTrue(reply.sensitive)
                assertFalse(reply.unrecognized)
                assertNull(reply.topicId)
                assertTrue(reply.text, reply.text.contains("3114"))
            }
        }
    }

    @Test fun applicationHelpAndRepeatedFreeTextQuestionsRemainAvailableOnEveryPlan() {
        for (language in AppLocale.entries) for (plan in PlanType.entries) {
            val coach = RuleCoach(language = language, plan = plan)
            val help = coach.replyForTopic("cancel_subscription", context)
            assertEquals("cancel_subscription", help.topicId)
            assertFalse(help.unrecognized)
            assertTrue(help.text, help.text.contains("Google Play"))
            repeat(25) {
                val reply = coach.reply(if (language == AppLocale.FRENCH) "sommeil" else "sleep", context)
                assertEquals("sleep", reply.topicId)
                assertFalse(reply.unrecognized || reply.sensitive)
            }
        }
    }
}
