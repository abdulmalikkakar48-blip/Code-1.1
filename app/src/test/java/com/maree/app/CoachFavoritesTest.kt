package com.maree.app

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CoachFavoritesTest {
    private val language = AppLocale.ENGLISH
    private val context = OfflineCoachContext(8, false, emptyList())
    private val sleep get() = coachTopicsFor(language).first { it.id == "sleep" }

    @Test fun selectedFavoritesPersistAndReplaceOnlyTheOldestBeyondFifty() {
        val prefs = FavoritePreferences()
        val store = CoachFavoritesStore(prefs)
        val questions = preparedCoachQuestionsFor(language).take(55)
        questions.forEach { assertNotNull(store.savePrepared(it.id, language)) }

        val reloaded = CoachFavoritesStore(prefs).entries()
        assertEquals(50, reloaded.size)
        assertEquals(questions.drop(5).map { it.id }, reloaded.map { it.preparedId })
        assertTrue(reloaded.all { it.reply == null && it.minimumPlan == PlanType.FREE })
    }

    @Test fun duplicatesAreStableAndRemovalIsDurable() {
        val prefs = FavoritePreferences()
        val store = CoachFavoritesStore(prefs)
        val id = store.saveAdvice(sleep.id, sleep.label, sleep.replies.first(), language)
        assertNotNull(id)
        assertEquals(id, store.saveAdvice(sleep.id, sleep.label, sleep.replies.first(), language))
        assertEquals(1, store.entries().size)
        assertTrue(store.remove(requireNotNull(id)))
        assertTrue(CoachFavoritesStore(prefs).entries().isEmpty())
        assertTrue(store.remove(id))
    }

    @Test fun preparedAndAdviceFavoritesHaveDistinctLanguageIdentity() {
        val store = CoachFavoritesStore(FavoritePreferences())
        val question = preparedCoachQuestionsFor(language).first()
        val english = store.savePrepared(question.id, language)
        val french = store.savePrepared(question.id, AppLocale.FRENCH)
        assertFalse(english == french)
        val adviceId = store.saveAdvice(sleep.id, sleep.label, sleep.replies.first(), language)
        val advice = store.entries().first { it.id == adviceId }
        assertEquals(language, advice.language)
        assertEquals(sleep.label, advice.label)
        assertEquals(sleep.replies.first(), advice.reply)
        assertEquals(3, CoachFavoritesStore(FavoritePreferences().also {
            it.edit().putString(CoachFavoritesStore.KEY, store.exportJson().toString()).commit()
        }).entries().size)
    }

    @Test fun downgradeKeepsPreparedReferencesAndLocksTheirAnswers() {
        val store = CoachFavoritesStore(FavoritePreferences())
        for (minimum in PlanType.entries) {
            val question = questionFor(minimum)
            assertNotNull(store.savePrepared(question.id, language))
            val favorite = store.entries().last()
            assertEquals(minimum, favorite.minimumPlan)
            for (plan in PlanType.entries) {
                assertEquals(plan.ordinal < minimum.ordinal, favorite.isLocked(plan))
                val resolved = favorite.preparedQuestion(AppLocale.FRENCH, plan)
                if (plan.ordinal < minimum.ordinal) assertNull(resolved)
                else assertEquals(preparedCoachQuestionById(question.id, AppLocale.FRENCH, plan)?.question,
                    requireNotNull(resolved).question)
            }
            val exported = store.exportJson().getJSONObject(store.entries().lastIndex)
            assertFalse(exported.has("reply"))
            assertFalse(exported.toString().contains(question.answer))
        }
        assertEquals(3, store.entries().size)
    }

    @Test fun typedCompletePaidQuestionCannotBeReclassifiedAsAFreeTopic() {
        val store = CoachFavoritesStore(FavoritePreferences())
        val question = questionFor(PlanType.ULTRA)
        val typed = requireNotNull(findPreparedCoachQuestion(question.question, language, PlanType.ULTRA))
        val reply = RuleCoach(language = language, plan = PlanType.ULTRA).reply(question.question, context)
        val topic = coachTopicsFor(language).first { it.id == reply.topicId }

        assertNull(store.saveAdvice(topic.id, topic.label, reply.text, language))
        assertNotNull(store.savePrepared(typed.id, language))
        assertTrue(store.entries().single().isLocked(PlanType.FREE))
        assertNull(store.entries().single().preparedQuestion(language, PlanType.FREE))
    }

    @Test fun unrecognisedSensitiveAndArbitraryTextAreRefusedEvenWithAKnownTopic() {
        val store = CoachFavoritesStore(FavoritePreferences())
        val coach = RuleCoach(language = language)
        val unknown = coach.reply("xyzzy plover quux", context)
        val sensitive = coach.reply("I want to hurt myself", context)
        assertTrue(unknown.unrecognized)
        assertTrue(sensitive.sensitive)
        for (reply in listOf(unknown.text, sensitive.text, "person@example.invalid", "my token is private",
                sleep.replies.first() + "\n\nperson@example.invalid", "x".repeat(4_001))) {
            assertNull(store.saveAdvice(sleep.id, sleep.label, reply, language))
        }
        assertNull(store.saveAdvice("missing", sleep.label, sleep.replies.first(), language))
        assertNull(store.saveAdvice(sleep.id, "Raw user question", sleep.replies.first(), language))
        assertNull(store.savePrepared("missing.question", language))
        assertTrue(store.entries().isEmpty())
    }

    @Test fun recognizedAdviceOmitsRawInputAndExportIgnoresUnrelatedPrivatePreferences() {
        val prefs = FavoritePreferences()
        prefs.edit().putString("accountEmail", "person@example.invalid")
            .putString("accessToken", "private-token-in-unrelated-preference").commit()
        val store = CoachFavoritesStore(prefs)
        val raw = "I cannot sleep; insomnia keeps me awake; unique violet zebra phrase"
        val reply = RuleCoach(language = language).reply(raw, context)
        assertEquals(sleep.id, reply.topicId)
        assertNotNull(store.saveAdvice(sleep.id, sleep.label, reply.text, language))

        val json = store.exportJson().toString()
        assertFalse(json.contains(raw))
        assertFalse(json.contains("person@example.invalid"))
        assertFalse(json.contains("private-token-in-unrelated-preference"))
        assertEquals(sleep.label, store.entries().single().label)
    }

    @Test fun safeEngineContextIsAcceptedButPrivateAppLabelsAreRefused() {
        val store = CoachFavoritesStore(FavoritePreferences())
        val coach = RuleCoach(language = language)
        val topic = coachTopicsFor(language).first { it.id == "tiktok" }
        val safe = coach.replyForTopic(topic.id, OfflineCoachContext(0, true,
            listOf(OfflineCoachApp("TikTok", 60_000))))
        assertNotNull(store.saveAdvice(topic.id, topic.label, safe.text, language))
        val privateReply = coach.replyForTopic(topic.id, OfflineCoachContext(0, true,
            listOf(OfflineCoachApp("TikTok person@example.invalid", 60_000))))
        assertNull(store.saveAdvice(topic.id, topic.label, privateReply.text, language))
        val points = coachTopicsFor(language).first { it.id == "points" }
        val balance = coach.replyForTopic(points.id, context)
        assertNotNull(store.saveAdvice(points.id, points.label, balance.text, language))
    }

    @Test fun corruptRecordsAreDiscardedAndSavedTierCannotForgeEntitlement() {
        val prefs = FavoritePreferences()
        val store = CoachFavoritesStore(prefs)
        val question = questionFor(PlanType.ULTRA)
        assertNotNull(store.savePrepared(question.id, language))
        val valid = store.exportJson().getJSONObject(0)
        valid.put("minimumPlan", "FREE").put("reply", "injected-private-token")
            .put("label", "injected@example.invalid").put("unknownQuestion", "private raw text")
        val invalid = JSONObject().put("kind", "advice").put("id", "forged")
            .put("language", "en").put("topicId", sleep.id).put("label", sleep.label)
            .put("reply", "injected private answer")
        prefs.edit().putString(CoachFavoritesStore.KEY,
            JSONArray().put(JSONObject.NULL).put(invalid).put(valid).put(valid).toString()).commit()

        val entry = store.entries().single()
        assertEquals(PlanType.ULTRA, entry.minimumPlan)
        assertTrue(entry.isLocked(PlanType.FREE))
        assertNull(entry.reply)
        val exported = store.exportJson().toString()
        assertFalse(exported.contains("injected"))
        assertFalse(exported.contains("private raw text"))
        assertFalse(exported.contains("unknownQuestion"))
        prefs.edit().putString(CoachFavoritesStore.KEY, "{broken").commit()
        assertTrue(store.entries().isEmpty())
        assertNotNull(store.savePrepared(question.id, language))
        prefs.edit().putString(CoachFavoritesStore.KEY, "x".repeat(230_001)).commit()
        assertTrue(store.entries().isEmpty())
    }

    @Test fun failedCommitsNeverReportSaveOrDeletionSuccess() {
        val prefs = FavoritePreferences()
        val store = CoachFavoritesStore(prefs)
        val question = preparedCoachQuestionsFor(language).first()
        prefs.failCommit = true
        assertNull(store.savePrepared(question.id, language))
        assertNull(store.saveAdvice(sleep.id, sleep.label, sleep.replies.first(), language))
        assertTrue(store.entries().isEmpty())
        prefs.failCommit = false
        val id = requireNotNull(store.savePrepared(question.id, language))
        prefs.failCommit = true
        assertNull(store.savePrepared(question.id, language))
        assertFalse(store.remove(id))
        assertEquals(id, store.entries().single().id)
        prefs.failCommit = false
        assertTrue(store.remove(id))
    }

    private fun questionFor(plan: PlanType): PreparedCoachQuestion {
        if (plan == PlanType.FREE) return preparedCoachQuestionsFor(language).first()
        val base = coachLibraryProfiles.first().id
        val scenario = coachLibraryScenarios.first { it.minimumPlan == plan }.id
        return requireNotNull(preparedCoachQuestionById("$base~$scenario.notice_trigger", language, plan))
    }
}

private class FavoritePreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any>()
    var failCommit = false
    override fun getAll(): MutableMap<String, *> = values.toMutableMap()
    override fun getString(key: String, defValue: String?): String? = values[key] as? String ?: defValue
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? = defValues
    override fun getInt(key: String, defValue: Int): Int = values[key] as? Int ?: defValue
    override fun getLong(key: String, defValue: Long): Long = values[key] as? Long ?: defValue
    override fun getFloat(key: String, defValue: Float): Float = values[key] as? Float ?: defValue
    override fun getBoolean(key: String, defValue: Boolean): Boolean = values[key] as? Boolean ?: defValue
    override fun contains(key: String): Boolean = values.containsKey(key)
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit
    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
        private val changes = mutableMapOf<String, Any?>()
        private var clear = false
        override fun putString(key: String, value: String?) = apply { changes[key] = value }
        override fun putStringSet(key: String, values: MutableSet<String>?) = apply { changes[key] = values }
        override fun putInt(key: String, value: Int) = apply { changes[key] = value }
        override fun putLong(key: String, value: Long) = apply { changes[key] = value }
        override fun putFloat(key: String, value: Float) = apply { changes[key] = value }
        override fun putBoolean(key: String, value: Boolean) = apply { changes[key] = value }
        override fun remove(key: String) = apply { changes[key] = null }
        override fun clear() = apply { clear = true }
        override fun commit(): Boolean {
            if (failCommit) return false
            if (clear) values.clear()
            changes.forEach { (key, value) -> if (value == null) values.remove(key) else values[key] = value }
            return true
        }
        override fun apply() { commit() }
    }
}
