package com.maree.app

import java.text.Normalizer
import java.util.Locale
import org.junit.Assert.*
import org.junit.Test

class PreparedCoachLibraryTest {
    @Test fun offers1152DistinctQuestionsAndAnswersInEachLanguage() {
        for (language in AppLocale.entries) {
            val questions = preparedCoachQuestionsFor(language)
            assertEquals(1152, questions.size)
            assertEquals(1152, questions.map { it.id }.distinct().size)
            assertEquals(1152, questions.map { normalizeForTest(it.question) }.distinct().size)
            assertEquals(1152, questions.map { it.answer }.distinct().size)
            assertTrue(questions.all { it.question.isNotBlank() && it.answer.length >= 160 })
        }
    }

    @Test fun uses48ConcreteProfilesAnd24DifferentDecisionsRatherThanAliases() {
        assertEquals(48, coachLibraryProfiles.size)
        assertEquals(48, coachLibraryProfiles.map { it.id }.distinct().size)
        val questions = preparedCoachQuestionsFor(AppLocale.FRENCH)
        val byProfile = questions.groupBy { it.id.substringBefore('.') }
        val byDecision = questions.groupBy { it.id.substringAfter('.') }
        assertEquals(48, byProfile.size)
        assertTrue(byProfile.values.all { it.size == 24 })
        assertEquals(24, byDecision.size)
        assertTrue(byDecision.values.all { it.size == 48 })
        for (profile in coachLibraryProfiles) {
            val ownQuestions = byProfile.getValue(profile.id)
            assertTrue(ownQuestions.all { it.topicId == profile.parentTopicId })
            assertTrue(ownQuestions.all { it.question.contains(profile.label.fr) })
            assertTrue(ownQuestions.all { it.answer.contains(profile.smallAction.fr) })
            assertTrue(ownQuestions.any { it.answer.contains(profile.alternative.fr) })
            assertTrue(ownQuestions.any { it.answer.contains(profile.barrier.fr) })
            assertTrue(ownQuestions.any { it.answer.contains(profile.goal.fr) })
        }
    }

    @Test fun hasEightBalancedSectionsWithTranslatedLabels() {
        for (language in AppLocale.entries) {
            val sections = preparedCoachSectionsFor(language)
            assertEquals(8, sections.size)
            assertEquals(8, sections.map { it.id }.distinct().size)
            assertTrue(sections.all { it.label.isNotBlank() })
            val grouped = preparedCoachQuestionsFor(language).groupBy { it.sectionId }
            assertEquals(sections.map { it.id }.toSet(), grouped.keys)
            assertTrue(grouped.values.all { it.size == 144 })
            assertTrue(grouped.values.all { entries ->
                entries.map { it.id.substringBefore('.') }.distinct().size == 6
            })
        }
        assertNotEquals(preparedCoachSectionsFor(AppLocale.FRENCH).map { it.label },
            preparedCoachSectionsFor(AppLocale.ENGLISH).map { it.label })
    }

    @Test fun preservesIdsAndContextAcrossLanguagesWithoutGlobalLocaleState() {
        val french = preparedCoachQuestionsFor(AppLocale.FRENCH)
        val english = preparedCoachQuestionsFor(AppLocale.ENGLISH)
        assertEquals(french.map { it.id }, english.map { it.id })
        assertEquals(french.map { it.topicId }, english.map { it.topicId })
        assertEquals(french.map { it.sectionId }, english.map { it.sectionId })
        for ((fr, en) in french.zip(english)) {
            assertNotEquals(fr.question, en.question)
            assertNotEquals(fr.answer, en.answer)
            assertEquals(fr, preparedCoachQuestionById(fr.id, AppLocale.FRENCH))
            assertEquals(en, preparedCoachQuestionById(en.id, AppLocale.ENGLISH))
        }
        // Explicit calls remain stable even when the caller alternates the requested language.
        assertSame(french, preparedCoachQuestionsFor(AppLocale.FRENCH))
        assertSame(english, preparedCoachQuestionsFor(AppLocale.ENGLISH))
    }

    @Test fun retrievesEveryCompleteQuestionInEitherLanguageAndAnswersInRequestedLanguage() {
        val french = preparedCoachQuestionsFor(AppLocale.FRENCH)
        val english = preparedCoachQuestionsFor(AppLocale.ENGLISH)
        for ((fr, en) in french.zip(english)) {
            assertEquals(fr, findPreparedCoachQuestion(fr.question, AppLocale.FRENCH))
            assertEquals(en, findPreparedCoachQuestion(en.question, AppLocale.ENGLISH))
            assertEquals(en, findPreparedCoachQuestion(fr.question, AppLocale.ENGLISH))
            assertEquals(fr, findPreparedCoachQuestion(en.question, AppLocale.FRENCH))
        }
    }

    @Test fun normalizesAccentsCapitalizationWhitespacePunctuationAndKnownAppTypo() {
        val french = preparedCoachQuestionsFor(AppLocale.FRENCH).first()
        val equivalent = "  " + normalizeForTest(french.question).uppercase(Locale.ROOT)
            .replace(" ", "   ") + " !!! "
        assertEquals(french, findPreparedCoachQuestion(equivalent, AppLocale.FRENCH))
        val tiktok = preparedCoachQuestionsFor(AppLocale.FRENCH)
            .first { it.question.contains("TikTok", ignoreCase = true) }
        assertEquals(tiktok, findPreparedCoachQuestion(
            tiktok.question.replace("TikTok", "tiktoc", ignoreCase = true), AppLocale.FRENCH))
    }

    @Test fun completeQuestionLookupDoesNotHijackBroadUnknownOrMedicalQuestions() {
        for (language in AppLocale.entries) for (message in listOf(
            "", "TikTok", "mes points", "comment annuler mon abonnement", "Comment gagner des points ?",
            "Quelle est la capitale du Pérou ?", "What is the capital of Peru?",
            "Quel médicament prendre pour une insomnie ?", "Which medicine should I take for insomnia?",
            "TikTok me rend triste et je veux me faire du mal", "I want to hurt myself"
        )) assertNull(message, findPreparedCoachQuestion(message, language))
        assertNull(preparedCoachQuestionById("missing.profile", AppLocale.FRENCH))
    }

    @Test fun searchFindsNamedContextsPrefixesAndAppAliasesWithoutInventingAnswers() {
        for (language in AppLocale.entries) {
            val tiktok = searchPreparedCoachQuestions("tiktoc", language, limit = 2000)
            assertEquals(24, tiktok.size)
            assertTrue(tiktok.all { it.question.contains("TikTok", ignoreCase = true) })
            val notifications = searchPreparedCoachQuestions("notifi", language, limit = 2000)
            assertFalse(notifications.isEmpty())
            assertTrue(notifications.all { it.question.contains("notifi", ignoreCase = true) })
            assertTrue(searchPreparedCoachQuestions("xxzzyqv", language).isEmpty())
            assertTrue(searchPreparedCoachQuestions("capitale Pérou médicament", language).isEmpty())
        }
        val accented = searchPreparedCoachQuestions("REGULARITE", AppLocale.FRENCH, limit = 2000)
        assertEquals(144, accented.size)
        assertTrue(accented.all { it.sectionId == "consistency" })
    }

    @Test fun searchRespectsSectionAndLimitsAndReturnsAuthoredEntries() {
        val language = AppLocale.ENGLISH
        val all = preparedCoachQuestionsFor(language)
        assertEquals(all.take(20), searchPreparedCoachQuestions("", language, limit = 20))
        assertEquals(1152, searchPreparedCoachQuestions("", language, limit = Int.MAX_VALUE).size)
        assertTrue(searchPreparedCoachQuestions("", language, limit = 0).isEmpty())
        assertTrue(searchPreparedCoachQuestions("", language, limit = -1).isEmpty())
        assertTrue(searchPreparedCoachQuestions("", language, sectionId = "missing").isEmpty())
        val filtered = searchPreparedCoachQuestions("", language, sectionId = "moments", limit = 2000)
        assertEquals(144, filtered.size)
        assertTrue(filtered.all { it.sectionId == "moments" && it in all })
        assertTrue(searchPreparedCoachQuestions("tiktok", language, sectionId = "emotions").isEmpty())
        val exact = all.first { it.sectionId == "emotions" }
        assertEquals(exact, searchPreparedCoachQuestions(exact.question, language).first())
    }

    @Test fun actionsAreOnSeparateLinesAndWeeklyExerciseNeverPromisesAutomaticPoints() {
        for (language in AppLocale.entries) {
            val questions = preparedCoachQuestionsFor(language)
            assertTrue(questions.all { it.answer.contains("\n1.") && it.answer.contains("\n2.") &&
                it.answer.contains("\n3.") })
            val weekly = questions.filter { it.id.endsWith(".weekly_experiment") }
            assertEquals(48, weekly.size)
            assertTrue(weekly.all { it.answer.contains(if (language == AppLocale.ENGLISH)
                "does not automatically validate" else "ne valide pas automatiquement") })
            assertTrue(weekly.all { it.answer.contains("points") })
        }
    }

    private fun normalizeForTest(text: String): String = Normalizer.normalize(
        text.lowercase(Locale.ROOT).replace("œ", "oe").replace("æ", "ae"), Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .replace(Regex("[^\\p{L}\\p{N}]+"), " ").trim().replace(Regex("\\s+"), " ")
}
