package com.maree.app

import android.content.SharedPreferences
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class HonorDiceTest {
    @Test
    fun migrationsCapBothLegacyFormatsAndPreserveLowerBalancesAndOtherProgress() {
        for (legacy in listOf(false, true)) {
            val prefs = preferences()
            prefs.edit().putString("apps", "[]").putString("checked", "[0,1]")
                .putInt("completedCount", 12).putString("plan", "ULTRA").apply()
            if (legacy) prefs.edit().remove("pointUnits").putInt("points", 160).apply()
            else prefs.edit().putInt("pointUnits", 640).apply()
            val store = Store(prefs)
            assertEquals(400, store.pointUnits())
            assertEquals(400, prefs.getInt("pointUnits", -1))
            assertEquals("[]", prefs.getString("apps", null))
            assertEquals("[0,1]", prefs.getString("checked", null))
            assertEquals(12, prefs.getInt("completedCount", 0))
            assertEquals(PlanType.ULTRA, store.planType())
            assertFalse(prefs.contains("points"))
        }
        for (units in listOf(0, 1, 179, 399, 400)) {
            val prefs = preferences(units)
            assertEquals(units, Store(prefs).pointUnits())
            assertEquals(units, prefs.getInt("pointUnits", -1))
        }
    }

    @Test
    fun dailyAndChallengeRewardsReachButNeverExceedOneHundredAndAreNotDeferred() {
        val prefs = preferences(399)
        val store = Store(prefs)
        prefs.edit().putLong("periodStart", NOW - DAY).apply()
        assertEquals(400, store.readSnapshot(UsageReading(emptyMap(), null), NOW).pointUnits)
        prefs.edit().putInt("pointUnits", 399).putInt("completedCount", 4).apply()
        repeat(CHALLENGE_REQUIRED_DAYS) { day -> assertTrue(store.completeChallenge(0, NOW + day * DAY)) }
        assertEquals(400, store.pointUnits())
        assertEquals(5, prefs.getInt("completedCount", 0))
        prefs.edit().putInt("pointUnits", 350).apply()
        assertFalse(store.completeChallenge(0, NOW + 5 * DAY))
        assertEquals(350, store.pointUnits())
    }

    @Test
    fun customQuarterRewardsCapPreciselyAndCompletionAtCapDoesNotBankAReward() {
        for (startingUnits in listOf(399, 400)) {
            val prefs = preferences(startingUnits)
            val store = Store(prefs)
            store.setDemoPlan(PlanType.PREMIUM)
            assertNull(store.createCustomChallenge("Pause", listOf("Lire", "Marcher", "Écrire"), 2, NOW))
            val id = store.readSnapshot(UsageReading(emptyMap(), null), NOW).customChallenges.single().id
            repeat(2) { day ->
                repeat(3) { step -> assertNull(store.setCustomChallengeStep(id, step, true, NOW + day * DAY)) }
                assertNull(store.validateCustomChallengeToday(id, NOW + day * DAY))
            }
            assertEquals(400, store.pointUnits())
            prefs.edit().putInt("pointUnits", 350).apply()
            assertNotNull(store.validateCustomChallengeToday(id, NOW + 2 * DAY))
            assertEquals(350, store.pointUnits())
        }
    }

    @Test
    fun everyFaceDeductsItsNumberOfPointsForEveryPlanWithoutChangingLocksOrLimits() {
        for (plan in PlanType.entries) for (face in 1..6) {
            val prefs = preferences()
            val store = Store(prefs) { face }
            store.setDemoPlan(plan)
            assertNull(store.addApp("example.tracked", 5, 0))
            store.tick(UsageReading(mapOf("example.tracked" to 300_000L), null), NOW)
            val apps = prefs.getString("apps", null)
            val result = store.rollHonorDie(token(), NOW)!!
            assertEquals(face, result.face)
            assertEquals(face * 4, result.spentUnits)
            assertEquals(400 - face * 4, result.balanceAfterUnits)
            assertEquals(result.balanceAfterUnits, store.pointUnits())
            assertEquals(apps, prefs.getString("apps", null))
            assertEquals(plan, store.planType())
            assertEquals(0, prefs.getInt("completedCount", 0))
        }
    }

    @Test
    fun lowFractionalBalancesStopAtZeroAndZeroBalanceNeverRolls() {
        val prefs = preferences(1)
        var calls = 0
        val store = Store(prefs) { calls++; 6 }
        val result = store.rollHonorDie(token(), NOW)!!
        assertEquals(1, result.spentUnits)
        assertEquals(0, result.balanceAfterUnits)
        assertNull(store.rollHonorDie(token(), NOW))
        assertEquals(1, calls)
        assertEquals(0, store.pointUnits())
    }

    @Test
    fun retryUsesTheLastReceiptAndNeverRollsAgainOrRestoresAHistoricBalance() {
        val prefs = preferences()
        var calls = 0
        val store = Store(prefs) { calls++; 6 }
        val operation = token()
        val original = store.rollHonorDie(operation, NOW)!!
        prefs.edit().putInt("pointUnits", 350).apply()
        assertEquals(original, store.rollHonorDie(operation, NOW + DAY))
        assertEquals(350, store.pointUnits())
        val recreated = Store(prefs) { error("A retry must not roll again") }
        assertEquals(original, recreated.rollHonorDie(operation, NOW + DAY))
        assertEquals(350, recreated.pointUnits())
        assertEquals(1, calls)
        assertEquals(original, recreated.lastHonorDiceResult())
    }

    @Test
    fun commitFailureAfterAndroidMemoryMutationCanBeRetriedAfterRotationWithoutDoubleLoss() {
        val prefs = FaultingPreferences(preferences())
        val operation = token()
        val store = Store(prefs) { 6 }
        prefs.fail = true
        prefs.mutateOnFailure = true
        assertNull(store.rollHonorDie(operation, NOW))
        assertEquals(376, store.pointUnits())
        assertNotNull(store.lastHonorDiceResult())
        prefs.fail = false
        val recreated = Store(prefs) { error("The receipt must survive Activity recreation") }
        val result = recreated.rollHonorDie(operation, NOW)!!
        assertEquals(24, result.spentUnits)
        assertEquals(376, recreated.pointUnits())
        assertEquals(result, recreated.rollHonorDie(operation, NOW))
        assertEquals(376, recreated.pointUnits())
    }

    @Test
    fun failureWithoutMemoryMutationLeavesTheBalanceUntouchedUntilSuccessfulRetry() {
        val prefs = FaultingPreferences(preferences())
        val store = Store(prefs) { 4 }
        val operation = token()
        prefs.fail = true
        assertNull(store.rollHonorDie(operation, NOW))
        assertEquals(400, store.pointUnits())
        assertNull(store.lastHonorDiceResult())
        prefs.fail = false
        assertEquals(16, store.rollHonorDie(operation, NOW)!!.spentUnits)
        assertEquals(384, store.pointUnits())
    }

    @Test
    fun newRollSettlesDueDailyBonusBeforeLossWhileReceiptRetryNeverAdvancesPeriods() {
        val prefs = preferences(200)
        prefs.edit().putLong("periodStart", NOW - DAY).apply()
        val store = Store(prefs) { 6 }
        val operation = token()
        val original = store.rollHonorDie(operation, NOW)!!
        assertEquals(216, store.pointUnits()) // 50 + 10 - 6 points.
        assertEquals(216, store.readSnapshot(UsageReading(emptyMap(), null), NOW).pointUnits)
        assertEquals(original, store.rollHonorDie(operation, NOW + DAY))
        assertEquals(216, store.pointUnits())
        assertEquals(232, store.rollHonorDie(token(), NOW + DAY)!!.balanceAfterUnits)
    }

    @Test
    fun erasureRejectsStaleRollAndReceiptRetriesWithoutRecreatingPersonalData() {
        val prefs = preferences()
        val store = Store(prefs) { 3 }
        val operation = token()
        assertNotNull(store.rollHonorDie(operation, NOW))
        assertTrue(store.clearPersonalData())
        assertNull(store.rollHonorDie(operation, NOW))
        assertNull(store.rollHonorDie(token(), NOW))
        assertNull(store.lastHonorDiceResult())
        assertTrue(prefs.all.isEmpty())
        val restarted = Store(prefs) { 2 }
        assertNotNull(restarted.rollHonorDie(token(), NOW))
        assertTrue(restarted.pointUnits() in 0..400)
    }

    @Test
    fun invalidTokensAndMalformedReceiptsAreRejectedAndExportOmitsUnknownFields() {
        val prefs = preferences()
        val store = Store(prefs) { 2 }
        assertNull(store.rollHonorDie("not-a-uuid", NOW))
        assertEquals(400, store.pointUnits())
        val result = store.rollHonorDie(token(), NOW)!!
        val receipt = result.toJson().put("privateUnexpectedField", "must not export")
        prefs.edit().putString(HONOR_DICE_KEY, receipt.toString()).apply()
        val exported = store.exportLocalState(NOW).getJSONObject("honorDice")
        assertEquals(4, exported.length())
        assertFalse(exported.has("privateUnexpectedField"))
        assertEquals(100, store.exportLocalState(NOW).getJSONObject("points").getInt("maximumValue"))
        for (bad in listOf("{}", receipt.put("face", 7).toString(),
            JSONObject().put("operationId", result.operationId).put("face", 1)
                .put("spentQuarterUnits", 1).put("balanceAfterQuarterUnits", 399).toString())) {
            prefs.edit().putString(HONOR_DICE_KEY, bad).apply()
            assertNull(store.lastHonorDiceResult())
            assertTrue(store.exportLocalState(NOW).isNull("honorDice"))
        }
    }

    private fun preferences(units: Int = 400) = TestFeaturePreferences().apply {
        edit().putInt("pointUnits", units).putLong("periodStart", NOW)
            .putLong("challengeStart", NOW).putString("day", "20270115").apply()
    }

    private fun token(): String = UUID.randomUUID().toString()

    companion object {
        private const val NOW = 1_800_000_000_000L
        private const val DAY = 86_400_000L
    }
}

/** Models both test-store failure and Android's memory-before-disk commit semantics. */
private class FaultingPreferences(private val delegate: SharedPreferences) : SharedPreferences by delegate {
    var fail = false
    var mutateOnFailure = false
    override fun edit(): SharedPreferences.Editor {
        val editor = delegate.edit()
        return object : SharedPreferences.Editor by editor {
            override fun putString(key: String, value: String?) = apply { editor.putString(key, value) }
            override fun putStringSet(key: String, values: MutableSet<String>?) = apply { editor.putStringSet(key, values) }
            override fun putInt(key: String, value: Int) = apply { editor.putInt(key, value) }
            override fun putLong(key: String, value: Long) = apply { editor.putLong(key, value) }
            override fun putFloat(key: String, value: Float) = apply { editor.putFloat(key, value) }
            override fun putBoolean(key: String, value: Boolean) = apply { editor.putBoolean(key, value) }
            override fun remove(key: String) = apply { editor.remove(key) }
            override fun clear() = apply { editor.clear() }
            override fun commit(): Boolean {
                if (!fail) return editor.commit()
                if (mutateOnFailure) editor.commit()
                return false
            }
            override fun apply() { commit() }
        }
    }
}
