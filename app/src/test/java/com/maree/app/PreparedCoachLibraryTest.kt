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

    @Test fun tierCountsAreDerivedFromConcreteScenariosAndKeepEveryOriginalId() {
        assertEquals(1152, preparedCoachQuestionCount())
        assertEquals(23040, preparedCoachQuestionCount(PlanType.PREMIUM))
        assertEquals(50688, preparedCoachQuestionCount(PlanType.ULTRA))
        assertEquals(43, coachLibraryScenarios.size)
        assertEquals(43, coachLibraryScenarios.map { it.id }.toSet().size)
        assertEquals(19, coachLibraryScenarios.count { it.minimumPlan == PlanType.PREMIUM })
        assertEquals(24, coachLibraryScenarios.count { it.minimumPlan == PlanType.ULTRA })
        for (language in AppLocale.entries) {
            val original = preparedCoachQuestionsFor(language)
            for (plan in PlanType.entries) {
                val questions = preparedCoachQuestionsFor(language, plan)
                assertEquals(preparedCoachQuestionCount(plan), questions.size)
                for (index in original.indices) assertEquals(original[index], questions[index])
                assertSame(questions, preparedCoachQuestionsFor(language, plan))
                assertEquals(preparedCoachSectionsFor(language), preparedCoachSectionsFor(language, plan))
            }
        }
    }

    @Test fun everyUltraQuestionAndAnswerIsDistinctAndTranslatedWithTheSameStableIdentity() {
        val french = preparedCoachQuestionsFor(AppLocale.FRENCH, PlanType.ULTRA)
        val english = preparedCoachQuestionsFor(AppLocale.ENGLISH, PlanType.ULTRA)
        for (language in AppLocale.entries) {
            val questions = preparedCoachQuestionsFor(language, PlanType.ULTRA)
            val ids = HashSet<String>()
            val texts = HashSet<String>()
            val answers = HashSet<String>()
            val sections = HashMap<String, Int>()
            for (index in questions.indices) {
                val entry = questions[index]
                assertTrue("Duplicate ID: ${entry.id}", ids.add(entry.id))
                assertTrue("Duplicate question: ${entry.id}", texts.add(normalizeForTest(entry.question)))
                assertTrue("Duplicate answer: ${entry.id}", answers.add(entry.answer))
                assertTrue(entry.answer.contains("\n1.") && entry.answer.contains("\n2.") &&
                    entry.answer.contains("\n3."))
                assertTrue(entry.answer.length >= 160)
                sections[entry.sectionId] = (sections[entry.sectionId] ?: 0) + 1
                val translated = if (language == AppLocale.FRENCH) english[index] else french[index]
                assertEquals(entry.id, translated.id)
                assertEquals(entry.topicId, translated.topicId)
                assertEquals(entry.sectionId, translated.sectionId)
                assertNotEquals(entry.question, translated.question)
                assertNotEquals(entry.answer, translated.answer)
            }
            assertEquals(50688, ids.size)
            assertEquals(8, sections.size)
            assertTrue(sections.values.all { it == 6336 })
            assertTrue(preparedCoachAnswerCacheStats(language).cachedAnswers <= 256)
        }
    }

    @Test fun lookupAndSearchRejectScenariosOutsideTheSelectedPlan() {
        val intentIds = preparedCoachQuestionsFor(AppLocale.FRENCH).take(24)
            .map { it.id.substringAfter('.') }
        for (scenario in coachLibraryScenarios) {
            val minimumPlan = scenario.minimumPlan
            // Check every coaching decision, in both source and answer languages.
            for (intent in intentIds) {
                val id = "${coachLibraryProfiles.first().id}~${scenario.id}.$intent"
                val french = preparedCoachQuestionById(id, AppLocale.FRENCH, minimumPlan)!!
                val english = preparedCoachQuestionById(id, AppLocale.ENGLISH, minimumPlan)!!
                for (plan in PlanType.entries) for (language in AppLocale.entries) {
                    val expected = if (language == AppLocale.FRENCH) french else english
                    if (plan.ordinal >= minimumPlan.ordinal) {
                        assertEquals(expected, preparedCoachQuestionById(id, language, plan))
                        assertEquals(expected, findPreparedCoachQuestion(french.question, language, plan))
                        assertEquals(expected, findPreparedCoachQuestion(english.question, language, plan))
                    } else {
                        assertNull(preparedCoachQuestionById(id, language, plan))
                        assertNull(findPreparedCoachQuestion(french.question, language, plan))
                        assertNull(findPreparedCoachQuestion(english.question, language, plan))
                    }
                }
            }
            for (plan in PlanType.entries) for (language in AppLocale.entries) {
                val results = searchPreparedCoachQuestions(scenario.questionContext.forLanguage(language),
                    language, limit = Int.MAX_VALUE, plan = plan)
                val ownResults = results.filter { it.id.contains("~${scenario.id}.") }
                if (plan.ordinal < minimumPlan.ordinal) assertTrue(ownResults.isEmpty())
                else assertEquals(1152, ownResults.size)
            }
        }
        for (plan in PlanType.entries) {
            assertNull(preparedCoachQuestionById("tiktok_short_videos~unknown.next_step", AppLocale.FRENCH, plan))
            assertNull(preparedCoachQuestionById("tiktok_short_videos~shared_space.unknown", AppLocale.FRENCH, plan))
        }
    }

    @Test fun libraryAndSearchGenerateAnswersOnlyForRowsActuallyRequested() {
        for (language in AppLocale.entries) {
            val before = preparedCoachAnswerCacheStats(language).generatedAnswers
            val library = preparedCoachQuestionsFor(language, PlanType.ULTRA)
            val all = searchPreparedCoachQuestions("", language, limit = Int.MAX_VALUE, plan = PlanType.ULTRA)
            val section = searchPreparedCoachQuestions("", language, "relationships", Int.MAX_VALUE, PlanType.ULTRA)
            val matching = searchPreparedCoachQuestions("tiktoc", language, limit = Int.MAX_VALUE, plan = PlanType.ULTRA)
            assertEquals(50688, library.size)
            assertEquals(50688, all.size)
            assertEquals(6336, section.size)
            assertEquals(1056, matching.size)
            assertEquals(before, preparedCoachAnswerCacheStats(language).generatedAnswers)
            val page = (120 until 140).map { matching[it] }
            assertEquals(20, page.size)
            assertTrue(page.all { it.question.contains("TikTok", ignoreCase = true) })
            val generated = preparedCoachAnswerCacheStats(language).generatedAnswers - before
            assertTrue("A page must generate at most its 20 answers, not the whole result list", generated <= 20)
            assertEquals(before + generated, preparedCoachAnswerCacheStats(language).generatedAnswers)
            // Metadata is reused by another query; reading result counts still does not build answers.
            assertFalse(searchPreparedCoachQuestions("notifi", language, limit = Int.MAX_VALUE,
                plan = PlanType.ULTRA).isEmpty())
            assertEquals(before + generated, preparedCoachAnswerCacheStats(language).generatedAnswers)
        }
    }

    @Test fun constraintsChangeTheActionAndGoalRatherThanReusingAnIncompatibleBaseExercise() {
        val settings = coachLibraryScenarios.first { it.id == "unchanged_settings" }
        val noEquipment = coachLibraryScenarios.first { it.id == "no_equipment" }
        val technicalProfiles = setOf("youtube_autoplay", "repeated_push_alerts", "home_screen_shortcuts",
            "grayscale_visual_pull", "group_chat_noise", "unused_app_reinstallation")
        for (base in coachLibraryProfiles) for (language in AppLocale.entries) {
            val adapted = adaptedCoachLibraryProfile(base, settings)
            assertFalse(adapted.goal.forLanguage(language).contains(base.goal.forLanguage(language)))
            assertTrue(adapted.goal.forLanguage(language).contains(if (language == AppLocale.ENGLISH)
                "all settings remain unchanged" else "tous les réglages restent inchangés"))
            if (base.id in technicalProfiles)
                assertFalse(adapted.smallAction.forLanguage(language).contains(base.smallAction.forLanguage(language)))
        }
        val paper = coachLibraryProfiles.first { it.id == "paper_reading" }
        for (language in AppLocale.entries) {
            val adapted = adaptedCoachLibraryProfile(paper, noEquipment)
            assertFalse(adapted.smallAction.forLanguage(language).contains(paper.smallAction.forLanguage(language)))
            assertTrue(adapted.smallAction.forLanguage(language).contains(if (language == AppLocale.ENGLISH)
                "no book or purchase is required" else "aucun livre ni achat n'est requis"))
            val plan = PlanType.ULTRA
            fun answer(scenario: String, intent: String) = preparedCoachQuestionById(
                "${paper.id}~$scenario.$intent", language, plan)!!.answer
            assertTrue(answer("no_equipment", "tomorrow_plan").contains(if (language == AppLocale.ENGLISH)
                "without needing a written note" else "sans exiger de note écrite"))
            assertTrue(answer("low_energy", "prepare_space").contains(if (language == AppLocale.ENGLISH)
                "without requiring purchases, preparation" else "sans exiger d'achat, de préparation"))
            assertTrue(answer("unchanged_settings", "avoid_perfection").contains(if (language == AppLocale.ENGLISH)
                "rather than the phone's configuration" else "plutôt que la configuration du téléphone"))
            assertTrue(answer("essential_contact", "need_phone").contains(if (language == AppLocale.ENGLISH)
                "without inserting a delay" else "sans ajouter de délai"))
            assertTrue(answer("limited_time", "two_minutes").contains(if (language == AppLocale.ENGLISH)
                "at most two minutes" else "deux minutes au maximum"))
        }
    }

    @Test fun expandedTiersDoNotInterceptMedicalDistressOrGeneralKnowledgeMessages() {
        for (plan in PlanType.entries) for (language in AppLocale.entries) for (message in listOf(
            "Quelle est la capitale du Pérou ?", "What is the capital of Peru?",
            "Quel médicament prendre pour une insomnie ?", "Which medicine should I take for insomnia?",
            "TikTok me rend triste et je veux me faire du mal", "I want to hurt myself"
        )) assertNull(message, findPreparedCoachQuestion(message, language, plan))
    }

    private fun normalizeForTest(text: String): String = Normalizer.normalize(
        text.lowercase(Locale.ROOT).replace("œ", "oe").replace("æ", "ae"), Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .replace(Regex("[^\\p{L}\\p{N}]+"), " ").trim().replace(Regex("\\s+"), " ")
}
