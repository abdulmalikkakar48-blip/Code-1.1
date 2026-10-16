package com.maree.app

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

class LocalDataExportTest {
    @Test
    fun exportsStoredUnicodeProgressQuartersAndLimitsWithoutAdvancingRewardsOrCycles() {
        val (store, prefs) = fixture()
        val before = prefs.all
        val json = JSONObject(LocalDataExport.json(store, AppLocale.FRENCH, ThemeMode.SYSTEM, true, NOW + DAY))

        assertEquals(before, prefs.all)
        assertEquals("selkie-local-data", json.getString("format"))
        assertEquals(5, json.getInt("schemaVersion"))
        assertEquals(Instant.ofEpochMilli(NOW + DAY).toString(), json.getString("exportedAt"))
        val preferences = json.getJSONObject("preferences")
        assertEquals("fr", preferences.getString("language"))
        assertEquals("system", preferences.getString("appearance"))
        assertEquals("dark", preferences.getString("effectiveAppearance"))

        val stored = json.getJSONObject("storedData")
        assertEquals(0.25, stored.getJSONObject("points").getDouble("value"), 0.0)
        assertEquals(1, stored.getJSONObject("points").getInt("quarterUnits"))
        assertEquals(4, stored.getJSONObject("points").getInt("unitsPerPoint"))
        assertEquals(100, stored.getJSONObject("points").getInt("maximumValue"))
        assertTrue(stored.isNull("honorDice"))
        assertEquals("PREMIUM", stored.getJSONObject("membership").getString("plan"))
        assertEquals("local_demo", stored.getJSONObject("membership").getString("selectionMode"))

        val app = stored.getJSONObject("tracking").getJSONArray("trackedApps").getJSONObject(0)
        assertEquals("com.example.reader", app.getString("packageName"))
        assertEquals(30, app.getInt("limitMinutes"))
        assertEquals(90_000L, app.getLong("baselineUsageMillis"))
        assertEquals(NOW + DAY, app.getLong("lockedUntilEpochMillis"))
        assertEquals("20270115", app.getString("warningLocalDay"))
        assertEquals(NOW - 30_000, app.getLong("lastPenaltyAtEpochMillis"))

        val standard = stored.getJSONObject("standardChallenges").getJSONArray("challenges").getJSONObject(0)
        assertEquals(CHALLENGE_REQUIRED_DAYS, standard.getInt("requiredDays"))
        assertEquals(1, standard.getInt("completedDays"))
        assertEquals(day(NOW), standard.getJSONArray("validatedDates").getString(0))
        val custom = stored.getJSONArray("customChallenges").getJSONObject(0)
        assertEquals("Déconnecter 🐚", custom.getString("title"))
        assertEquals(3, custom.getJSONArray("steps").length())
        assertEquals("Écrire une idée", custom.getJSONArray("steps").getString(2))
        assertEquals(2, custom.getInt("requiredDays"))
        assertEquals(1, custom.getInt("completedDays"))
        assertEquals(day(NOW), custom.getJSONArray("validatedDates").getString(0))
        assertEquals(day(NOW + DAY), custom.getJSONObject("dailyChecklist").getString("localDate"))
        assertEquals("[0,2]", custom.getJSONObject("dailyChecklist").getJSONArray("checkedStepIndexes").toString())
        assertFalse(custom.getBoolean("completed"))
        assertFalse(custom.getBoolean("rewardClaimed"))
    }

    @Test
    fun whitelistRejectsSyntheticSecretsAndCoachTextInjectedIntoBothPrefsAndNestedStoredObjects() {
        val (store, prefs) = fixture()
        prefs.edit().putString("accessToken", "synthetic-private-token")
            .putString("accountEmail", "fixture@example.invalid")
            .putString("offlineCoachHistory", "Question privée qui ne doit pas sortir")
            .putString("futurePrivateSetting", "future-private-value").apply()
        val apps = JSONArray(prefs.getString("apps", "[]"))
        apps.getJSONObject(0).put("session", "nested-private-session")
        prefs.edit().putString("apps", apps.toString()).apply()
        val customs = JSONArray(prefs.getString("customChallenges", "[]"))
        customs.getJSONObject(0).put("coachHistory", "nested-private-coach-text")
        prefs.edit().putString("customChallenges", customs.toString()).apply()

        val exported = LocalDataExport.json(store, now = NOW)

        for (privateValue in listOf("synthetic-private-token", "fixture@example.invalid",
                "Question privée qui ne doit pas sortir", "future-private-value",
                "nested-private-session", "nested-private-coach-text")) {
            assertFalse(exported.contains(privateValue))
        }
        val data = JSONObject(exported).getJSONObject("storedData")
        assertEquals(setOf("points", "honorDice", "membership", "tracking", "standardChallenges", "customChallenges", "calendarTasks", "coloringPages", "dailyHistory", "limitSchedules", "coachFavorites", "guidedPrograms"),
            data.keys().asSequence().toSet())
        assertEquals("Déconnecter 🐚", data.getJSONArray("customChallenges").getJSONObject(0).getString("title"))
    }

    @Test
    fun confirmedHonorRollIsIncludedInSchemaFiveWithoutExportingInjectedMetadataOrAdvancingRewards() {
        val (_, prefs) = fixture()
        prefs.edit().putInt("pointUnits", 400).putLong("periodStart", NOW).apply()
        val store = Store(prefs) { 5 }
        val result = store.rollHonorDie(UUID.randomUUID().toString(), NOW)!!
        val receipt = result.toJson().put("privateMetadata", "synthetic-private-receipt-value")
        prefs.edit().putString(HONOR_DICE_KEY, receipt.toString()).apply()
        val before = prefs.all

        val json = JSONObject(LocalDataExport.json(store, now = NOW + DAY))

        assertEquals(5, json.getInt("schemaVersion"))
        val stored = json.getJSONObject("storedData")
        assertEquals(95.0, stored.getJSONObject("points").getDouble("value"), 0.0)
        assertEquals(100, stored.getJSONObject("points").getInt("maximumValue"))
        assertEquals(result.toJson().toString(), stored.getJSONObject("honorDice").toString())
        assertFalse(json.toString().contains("synthetic-private-receipt-value"))
        assertEquals(before, prefs.all)
    }

    @Test
    fun documentWriterUsesUtf8AndClosesTheChosenOutput() {
        val (store, _) = fixture()
        val stream = object : ByteArrayOutputStream() {
            var closed = false
            override fun close() { closed = true; super.close() }
        }
        val export = LocalDataExport.json(store, now = NOW)

        LocalDataExport.write(stream, export)

        assertTrue(stream.closed)
        assertEquals(export, String(stream.toByteArray(), Charsets.UTF_8))
        assertEquals("Déconnecter 🐚", JSONObject(String(stream.toByteArray(), Charsets.UTF_8))
            .getJSONObject("storedData").getJSONArray("customChallenges").getJSONObject(0).getString("title"))
    }

    private fun fixture(): Pair<Store, ExportPreferences> {
        val prefs = ExportPreferences()
        prefs.edit().putInt("pointUnits", 1).putLong("periodStart", NOW)
            .putLong("challengeStart", NOW).putString("day", day(NOW).replace("-", "")).apply()
        val store = Store(prefs)
        store.setDemoPlan(PlanType.PREMIUM)
        assertNull(store.addApp("com.example.reader", 30, 90_000))
        store.tick(UsageReading(mapOf("com.example.reader" to 1_890_000L), null), NOW)
        val apps = JSONArray(prefs.getString("apps", "[]"))
        apps.getJSONObject(0).put("warn", "20270115").put("penalty", NOW - 30_000)
        prefs.edit().putString("apps", apps.toString()).apply()
        assertTrue(store.completeChallenge(0, NOW))
        assertNull(store.createCustomChallenge("Déconnecter 🐚", listOf("Lire sans écran", "Marcher dehors", "Écrire une idée"), 2, NOW))
        val id = JSONArray(prefs.getString("customChallenges", "[]")).getJSONObject(0).getString("id")
        repeat(3) { assertNull(store.setCustomChallengeStep(id, it, true, NOW)) }
        assertNull(store.validateCustomChallengeToday(id, NOW))
        assertNull(store.setCustomChallengeStep(id, 0, true, NOW + DAY))
        assertNull(store.setCustomChallengeStep(id, 2, true, NOW + DAY))
        // Une lecture d'écran déclencherait des bonus : l'export doit rester purement consultatif.
        prefs.edit().putLong("periodStart", NOW - 3 * DAY).putBoolean("periodBreach", false).apply()
        return store to prefs
    }

    private fun day(now: Long): String = Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toLocalDate().toString()

    companion object {
        private const val NOW = 1_800_000_000_000L
        private const val DAY = 86_400_000L
    }
}

private class ExportPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any>()
    override fun getAll(): MutableMap<String, *> = values.toMutableMap()
    override fun getString(key: String, defValue: String?): String? = values[key] as? String ?: defValue
    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? =
        (values[key] as? Set<String>)?.toMutableSet() ?: defValues
    override fun getInt(key: String, defValue: Int): Int = values[key] as? Int ?: defValue
    override fun getLong(key: String, defValue: Long): Long = values[key] as? Long ?: defValue
    override fun getFloat(key: String, defValue: Float): Float = values[key] as? Float ?: defValue
    override fun getBoolean(key: String, defValue: Boolean): Boolean = values[key] as? Boolean ?: defValue
    override fun contains(key: String): Boolean = values.containsKey(key)
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit
    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
        private val pending = mutableMapOf<String, Any?>()
        private var clearValues = false
        override fun putString(key: String, value: String?) = apply { pending[key] = value }
        override fun putStringSet(key: String, values: MutableSet<String>?) = apply { pending[key] = values?.toSet() }
        override fun putInt(key: String, value: Int) = apply { pending[key] = value }
        override fun putLong(key: String, value: Long) = apply { pending[key] = value }
        override fun putFloat(key: String, value: Float) = apply { pending[key] = value }
        override fun putBoolean(key: String, value: Boolean) = apply { pending[key] = value }
        override fun remove(key: String) = apply { pending[key] = null }
        override fun clear() = apply { clearValues = true }
        override fun commit(): Boolean {
            if (clearValues) values.clear()
            pending.forEach { (key, value) -> if (value == null) values.remove(key) else values[key] = value }
            return true
        }
        override fun apply() { commit() }
    }
}
