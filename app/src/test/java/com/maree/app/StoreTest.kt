package com.maree.app

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class StoreTest {
    private lateinit var prefs: MemoryPreferences
    private lateinit var store: Store

    @Before
    fun setUp() {
        prefs = MemoryPreferences()
        prefs.edit().putInt("points", 100)
            .putLong("periodStart", NOW)
            .putLong("challengeStart", NOW)
            .putString("day", SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(Date(NOW)))
            .apply()
        store = Store(prefs)
        assertNull(store.addApp(PACKAGE, 5, 0))
    }

    @Test
    fun uiRefreshDoesNotConsumeTheWarning() {
        val usage = reading(4 * MINUTE)
        repeat(3) { store.readSnapshot(usage, NOW) }

        assertEquals(listOf(PACKAGE to 1), store.tick(usage, NOW).warnings)
        assertTrue(Store(prefs).tick(usage, NOW + 1).warnings.isEmpty())
    }

    @Test
    fun uiRefreshLeavesTheInitialLockForTheMonitor() {
        val usage = reading(5 * MINUTE)

        assertEquals(0L, store.readSnapshot(usage, NOW).apps.single().lockedUntil)
        val tick = store.tick(usage, NOW)
        assertEquals(setOf(PACKAGE), tick.justLocked)
        assertEquals(NOW + DAY, tick.snapshot.apps.single().lockedUntil)
        assertEquals(100.0, tick.snapshot.points, 0.0)
        assertTrue(Store(prefs).tick(usage, NOW + 1).justLocked.isEmpty())
    }

    @Test
    fun currentBreachDoesNotCancelACleanPreviousPeriodOrDisappear() {
        prefs.edit().putLong("periodStart", NOW - DAY).apply()

        val initialLock = store.tick(reading(5 * MINUTE), NOW)
        assertEquals(110.0, initialLock.snapshot.points, 0.0)
        assertTrue(prefs.getBoolean("periodBreach", false))

        val nextPeriod = store.tick(reading(0), NOW + DAY)
        assertEquals(110.0, nextPeriod.snapshot.points, 0.0)
        assertFalse(prefs.getBoolean("periodBreach", true))
    }

    @Test
    fun aPreviousBreachDoesNotCancelTheFollowingCleanPeriod() {
        prefs.edit().putLong("periodStart", NOW - DAY)
            .putBoolean("periodBreach", true).apply()

        assertEquals(100.0, store.tick(reading(0), NOW).snapshot.points, 0.0)
        assertEquals(110.0, store.tick(reading(0), NOW + DAY).snapshot.points, 0.0)
    }

    @Test
    fun snapshotCanAdvanceRewardsWithoutRepeatingThem() {
        prefs.edit().putLong("periodStart", NOW - DAY).apply()

        assertEquals(110.0, store.readSnapshot(reading(0), NOW).points, 0.0)
        assertEquals(110.0, store.tick(reading(0), NOW).snapshot.points, 0.0)
        assertEquals(110.0, Store(prefs).readSnapshot(reading(0), NOW).points, 0.0)
    }

    @Test
    fun reopeningPenaltyPreservesItsCooldownAndPointsFloor() {
        store.tick(reading(5 * MINUTE), NOW)
        prefs.edit().putInt("pointUnits", 12).apply()

        assertTrue(store.penalizeReopening(PACKAGE, NOW + 1))
        assertEquals(0.0, store.points(), 0.0)
        assertFalse(store.penalizeReopening(PACKAGE, NOW + 29_999))
        assertTrue(store.penalizeReopening(PACKAGE, NOW + 30_001))
        assertEquals(0.0, store.points(), 0.0)
    }

    @Test
    fun lockExpiresWithAFullNewAllowanceAndResetCostsTenPoints() {
        store.tick(reading(5 * MINUTE), NOW)

        val expired = store.tick(reading(2 * MINUTE), NOW + DAY)
        assertEquals(0L, expired.snapshot.apps.single().lockedUntil)
        assertEquals(0L, expired.snapshot.apps.single().usedMillis)

        store.tick(reading(7 * MINUTE), NOW + DAY + 1)
        assertNull(store.resetApp(PACKAGE, 7 * MINUTE))
        val reset = store.readSnapshot(reading(7 * MINUTE), NOW + DAY + 2)
        assertEquals(0L, reset.apps.single().lockedUntil)
        assertEquals(0L, reset.apps.single().usedMillis)
        assertEquals(90.0, reset.points, 0.0)
    }

    @Test
    fun challengesRewardOnlyAfterFiveWholeChallengesAndAlternateAfterTwentyOneDays() {
        repeat(CHALLENGE_REQUIRED_DAYS - 1) { day ->
            repeat(5) { index -> assertTrue(store.completeChallenge(index, NOW + day * DAY)) }
            assertEquals(100.0, store.points(), 0.0)
            assertEquals(0, prefs.getInt("completedCount", 0))
        }
        repeat(5) { index ->
            assertTrue(store.completeChallenge(index, NOW + (CHALLENGE_REQUIRED_DAYS - 1) * DAY))
            assertEquals(if (index == 4) 105.0 else 100.0, store.points(), 0.0)
            assertEquals(index + 1, prefs.getInt("completedCount", 0))
        }
        assertEquals(105.0, store.points(), 0.0)
        assertEquals(5, prefs.getInt("completedCount", 0))
        assertFalse(store.completeChallenge(0, NOW + (CHALLENGE_REQUIRED_DAYS - 1) * DAY))
        assertFalse(store.completeChallenge(10, NOW))

        val nextCycle = store.readSnapshot(reading(0), NOW + 21 * DAY)
        assertEquals(1, nextCycle.challengeSeries)
        assertTrue(nextCycle.checkedChallenges.isEmpty())
        assertTrue(nextCycle.challengeProgress.values.all { it.completedDays == 0 })
        assertFalse(prefs.contains("challengeDays"))
        assertEquals(5, nextCycle.totalCompletedChallenges)
        assertTrue(store.completeChallenge(10, NOW + 21 * DAY))
        assertFalse(store.completeChallenge(0, NOW + 21 * DAY))
    }

    @Test
    fun dailyValidationSurvivesRestartAndRejectsDoubleClicksAndEarlierDates() {
        assertTrue(store.completeChallenge(0, NOW))
        assertFalse(store.completeChallenge(0, NOW + 1))
        assertFalse(Store(prefs).completeChallenge(0, NOW + 2))

        val firstDay = store.readSnapshot(reading(0), NOW).challengeProgress.getValue(0)
        assertEquals(1, firstDay.completedDays)
        assertTrue(firstDay.validatedToday)
        assertFalse(firstDay.canValidateToday)
        assertTrue(store.completeChallenge(0, NOW + 3 * DAY))
        assertFalse(store.completeChallenge(0, NOW + DAY))

        val earlierDay = Store(prefs).readSnapshot(reading(0), NOW + DAY).challengeProgress.getValue(0)
        assertEquals(2, earlierDay.completedDays)
        assertFalse(earlierDay.validatedToday)
        assertFalse(earlierDay.canValidateToday)
    }

    @Test
    fun skippedDaysKeepProgressAndTheFifthDistinctDayCompletesTheChallengeOnce() {
        val offsets = listOf(0L, 3L, 5L, 8L, 10L)
        offsets.forEachIndexed { count, day ->
            assertTrue(store.completeChallenge(0, NOW + day * DAY))
            val snapshot = store.readSnapshot(reading(0), NOW + day * DAY)
            assertEquals(count + 1, snapshot.challengeProgress.getValue(0).completedDays)
            assertEquals(count == CHALLENGE_REQUIRED_DAYS - 1, 0 in snapshot.checkedChallenges)
            assertEquals(if (count == CHALLENGE_REQUIRED_DAYS - 1) 1 else 0,
                snapshot.totalCompletedChallenges)
        }
        assertFalse(Store(prefs).completeChallenge(0, NOW + 11 * DAY))
        assertEquals(1, prefs.getInt("completedCount", 0))
    }

    @Test
    fun rotationRemovesPartialProgressAndReactivatesAGroupAfterTwoCycles() {
        assertTrue(store.completeChallenge(0, NOW))
        assertTrue(store.completeChallenge(0, NOW + DAY))

        val secondGroup = store.readSnapshot(reading(0), NOW + 21 * DAY)
        assertEquals((10..19).toSet(), secondGroup.challengeProgress.keys)
        assertTrue(secondGroup.challengeProgress.values.all { it.completedDays == 0 && it.canValidateToday })
        assertFalse(prefs.contains("challengeDays"))
        assertTrue(store.completeChallenge(10, NOW + 21 * DAY))

        val originalGroup = store.readSnapshot(reading(0), NOW + 42 * DAY)
        assertEquals(0, originalGroup.challengeSeries)
        assertEquals(0, originalGroup.challengeProgress.getValue(0).completedDays)
        assertTrue(originalGroup.checkedChallenges.isEmpty())
        assertTrue(store.completeChallenge(0, NOW + 42 * DAY))
    }

    @Test
    fun fifthWholeChallengeRewardsEvenWhenEarlierCompletionsBelongToAPreviousSeries() {
        repeat(CHALLENGE_REQUIRED_DAYS) { day ->
            repeat(4) { index -> assertTrue(store.completeChallenge(index, NOW + day * DAY)) }
        }
        assertEquals(100.0, store.points(), 0.0)
        assertEquals(4, prefs.getInt("completedCount", 0))
        val nextCycle = store.readSnapshot(reading(0), NOW + 21 * DAY)
        val pointsAfterDisciplineRewards = nextCycle.points
        assertEquals(4, nextCycle.totalCompletedChallenges)

        repeat(CHALLENGE_REQUIRED_DAYS) { day ->
            assertTrue(store.completeChallenge(10, NOW + (21 + day) * DAY))
            assertEquals(pointsAfterDisciplineRewards + if (day == 4) 5 else 0, store.points(), 0.0)
        }
        assertEquals(5, prefs.getInt("completedCount", 0))
    }

    @Test
    fun legacyCompletedChallengesRemainCompleteWithoutRecreditingPointsOrCounts() {
        prefs.edit().putString("checked", "[0,1]").putInt("completedCount", 12)
            .putInt("points", 137).remove("pointUnits").apply()
        val upgraded = Store(prefs)
        val migrated = upgraded.readSnapshot(reading(0), NOW)

        assertEquals(setOf(0, 1), migrated.checkedChallenges)
        assertEquals(CHALLENGE_REQUIRED_DAYS, migrated.challengeProgress.getValue(0).completedDays)
        assertFalse(migrated.challengeProgress.getValue(0).canValidateToday)
        assertEquals(12, migrated.totalCompletedChallenges)
        assertEquals(137.0, migrated.points, 0.0)
        assertFalse(upgraded.completeChallenge(0, NOW))
        repeat(CHALLENGE_REQUIRED_DAYS) { day ->
            assertTrue(upgraded.completeChallenge(2, NOW + day * DAY))
            assertEquals(137.0, upgraded.points(), 0.0)
        }
        assertEquals(13, prefs.getInt("completedCount", 0))
        assertEquals(setOf(0, 1, 2), upgraded.readSnapshot(reading(0), NOW).checkedChallenges)
    }

    @Test
    fun localMidnightAllowsValidationWithoutWaitingTwentyFourHoursIncludingDst() {
        val previousZone = TimeZone.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"))
            fun time(value: String): Long = LocalDateTime.parse(value)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val beforeMidnight = time("2027-03-27T23:59:00")
            prefs.edit().putLong("challengeStart", beforeMidnight).putLong("periodStart", beforeMidnight).apply()

            assertTrue(store.completeChallenge(0, beforeMidnight))
            assertTrue(store.completeChallenge(0, time("2027-03-28T00:01:00")))
            assertFalse(Store(prefs).completeChallenge(0, time("2027-03-28T23:59:00")))
            assertTrue(store.completeChallenge(0, time("2027-03-29T00:01:00")))
            val progress = store.readSnapshot(reading(0), time("2027-03-29T00:02:00"))
                .challengeProgress.getValue(0)
            assertEquals(3, progress.completedDays)
            assertTrue(progress.validatedToday)
        } finally {
            TimeZone.setDefault(previousZone)
        }
    }

    @Test
    fun legacyIntegerPointsAndPremiumMigrateOnceWithoutGainsOnRestart() {
        val legacy = MemoryPreferences()
        legacy.edit().putInt("points", 137).putBoolean("premium", true).apply()

        val migrated = Store(legacy)
        assertEquals(548, migrated.pointUnits())
        assertEquals(137.0, migrated.points(), 0.0)
        assertEquals(PlanType.PREMIUM, migrated.planType())
        assertFalse(legacy.contains("points"))
        assertFalse(legacy.contains("premium"))
        legacy.edit().putInt("points", 999).apply()
        repeat(3) {
            assertEquals(548, Store(legacy).pointUnits())
            assertEquals(PlanType.PREMIUM, Store(legacy).planType())
        }
    }

    @Test
    fun fractionalPointFormattingUsesExactQuartersAndFrenchDecimalComma() {
        assertEquals("0", formatPointUnits(0))
        assertEquals("0,25", formatPointUnits(1))
        assertEquals("9,75", formatPointUnits(39))
        assertEquals("100", formatPointUnits(400))
        assertEquals("100,75", formatPoints(100.75))
        prefs.edit().putInt("pointUnits", 401).apply()
        assertEquals(100.25, Store(prefs).points(), 0.0)
        prefs.edit().putLong("periodStart", NOW - DAY).apply()
        assertEquals(441, store.tick(reading(0), NOW).snapshot.pointUnits)
        store.tick(reading(5 * MINUTE), NOW)
        assertTrue(store.penalizeReopening(PACKAGE, NOW + 1))
        assertEquals(421, store.pointUnits())
        assertNull(store.resetApp(PACKAGE, 5 * MINUTE))
        assertEquals(381, store.pointUnits())
    }

    @Test
    fun eachPlanEnforcesItsApplicationCapAndDowngradingPreservesExistingApps() {
        for (plan in PlanType.entries) {
            store.packages().forEach { store.removeApp(it) }
            store.setDemoPlan(plan)
            val appLimit = plan.maxTrackedApps
            repeat(appLimit ?: 150) { assertNull(store.addApp("example.app$it", 5, 0)) }
            if (appLimit != null) {
                assertTrue(store.addApp("example.extra", 5, 0) != null)
                assertEquals(appLimit, store.trackedCount())
            } else {
                assertNull(store.addApp("example.extra", 5, 0))
                assertEquals(151, store.trackedCount())
            }
            assertEquals(plan != PlanType.FREE, store.isPremium())
        }
        assertEquals(151, store.trackedCount())
        assertEquals(151, Store(prefs).trackedCount())
        assertEquals(PlanType.ULTRA, Store(prefs).planType())
        assertTrue(store.addApp("example.app0", 5, 0) != null)
        store.setDemoPlan(PlanType.PREMIUM)
        assertEquals(151, store.trackedCount())
        assertTrue(store.addApp("example.above-premium", 5, 0) != null)
        store.setDemoPlan(PlanType.FREE)
        assertEquals(151, store.trackedCount())
        assertTrue(store.addApp("example.above-free", 5, 0) != null)
        assertEquals(10, PlanType.FREE.maxTrackedApps)
        assertEquals(50, PlanType.PREMIUM.maxTrackedApps)
        assertNull(PlanType.ULTRA.maxTrackedApps)
        assertEquals(10, PlanType.PREMIUM.monthlyPriceEuros)
        assertEquals(20, PlanType.ULTRA.monthlyPriceEuros)
    }

    @Test
    fun zeroPointsBlocksAddingEditingAndResettingForEveryPlanButRemovalStaysFree() {
        for (plan in PlanType.entries) {
            store.setDemoPlan(plan)
            prefs.edit().putInt("pointUnits", 0).apply()
            val appsBefore = prefs.getString("apps", null)

            assertTrue(store.addApp("example.new", 5, 0) != null)
            assertTrue(store.editLimit(PACKAGE, 10) != null)
            assertTrue(store.resetApp(PACKAGE, 0) != null)
            assertEquals(appsBefore, prefs.getString("apps", null))
            assertEquals(0, store.pointUnits())
            store.removeApp(PACKAGE)
            assertTrue(store.packages().isEmpty())
            prefs.edit().putInt("pointUnits", 1).apply()
            assertNull(store.addApp(PACKAGE, 5, 0))
            assertNull(store.editLimit(PACKAGE, 10))
        }
    }

    @Test
    fun resettingRequiresTenWholePointsAndNeverMutatesOrPartlyChargesInsufficientBalances() {
        store.tick(reading(5 * MINUTE), NOW)
        store.setDemoPlan(PlanType.ULTRA)
        val lockedApps = prefs.getString("apps", null)
        for (units in listOf(0, 1, 39)) {
            prefs.edit().putInt("pointUnits", units).apply()
            assertTrue(store.resetApp(PACKAGE, 5 * MINUTE) != null)
            assertEquals(units, store.pointUnits())
            assertEquals(lockedApps, prefs.getString("apps", null))
        }
        prefs.edit().putInt("pointUnits", 41).apply()
        assertNull(store.resetApp(PACKAGE, 5 * MINUTE))
        assertEquals(1, store.pointUnits())
        assertEquals(0L, store.readSnapshot(reading(5 * MINUTE), NOW).apps.single().lockedUntil)
    }

    @Test
    fun customChallengesRequirePaidPlansThreeStepsAndAChosenDurationOfAtLeastTwoDays() {
        assertTrue(store.createCustomChallenge("Bouger", CUSTOM_STEPS, 2, NOW) != null)
        store.setDemoPlan(PlanType.PREMIUM)
        assertTrue(store.createCustomChallenge("Bouger", CUSTOM_STEPS.take(2), 2, NOW) != null)
        assertTrue(store.createCustomChallenge("Bouger", CUSTOM_STEPS + "Encore", 2, NOW) != null)
        assertTrue(store.createCustomChallenge("Bouger", listOf("Lire", "", "Marcher"), 2, NOW) != null)
        assertTrue(store.createCustomChallenge("", CUSTOM_STEPS, 2, NOW) != null)
        assertTrue(store.createCustomChallenge("Bouger", CUSTOM_STEPS, 1, NOW) != null)
        assertTrue(store.createCustomChallenge("Bouger", CUSTOM_STEPS, 366, NOW) != null)
        prefs.edit().putInt("pointUnits", 0).apply()
        assertNull(store.createCustomChallenge("Bouger", CUSTOM_STEPS, 7, NOW))
        val custom = store.readSnapshot(reading(0), NOW).customChallenges.single()
        assertEquals(7, custom.requiredDays)
        assertEquals(0, custom.completedDays)
        assertTrue(custom.canEditToday)
        assertFalse(custom.canValidateToday)
        assertFalse(custom.rewardClaimed)
        assertEquals(0, store.pointUnits())
        assertTrue(store.createCustomChallenge("  BOUGER ", CUSTOM_STEPS, 2, NOW) != null)
    }

    @Test
    fun dailyCustomStepsPersistResetTomorrowAndRequireAllThreeBeforeValidation() {
        store.setDemoPlan(PlanType.ULTRA)
        val id = createCustom("Bouger", 2)
        assertNull(store.setCustomChallengeStep(id, 0, true, NOW))
        assertNull(store.setCustomChallengeStep(id, 1, true, NOW))
        assertTrue(store.validateCustomChallengeToday(id, NOW) != null)
        assertEquals(setOf(0, 1), Store(prefs).readSnapshot(reading(0), NOW).customChallenges.single().todayCheckedSteps)
        assertNull(store.setCustomChallengeStep(id, 2, true, NOW))
        assertTrue(customAt(id, NOW).canValidateToday)
        assertNull(store.setCustomChallengeStep(id, 1, false, NOW))
        assertFalse(customAt(id, NOW).canValidateToday)
        assertNull(store.setCustomChallengeStep(id, 1, true, NOW))
        assertNull(store.validateCustomChallengeToday(id, NOW))
        assertTrue(Store(prefs).validateCustomChallengeToday(id, NOW + 1) != null)
        assertTrue(store.setCustomChallengeStep(id, 0, false, NOW) != null)
        assertEquals(1, customAt(id, NOW).completedDays)

        val tomorrow = customAt(id, NOW + DAY)
        assertTrue(tomorrow.todayCheckedSteps.isEmpty())
        assertEquals(1, tomorrow.completedDays)
        assertTrue(tomorrow.canEditToday)
        assertFalse(tomorrow.canValidateToday)
        assertNull(store.setCustomChallengeStep(id, 0, true, NOW + DAY))
        assertTrue(store.setCustomChallengeStep(id, 1, true, NOW) != null)
    }

    @Test
    fun customCompletionAwardsExactlyOneQuarterOnceAndNeverChangesTheStandardCounter() {
        store.setDemoPlan(PlanType.PREMIUM)
        val id = createCustom("Bouger", 2)
        finishCustomDay(id, NOW)
        assertEquals(400, store.pointUnits())
        finishCustomDay(id, NOW + 3 * DAY)
        assertEquals(401, store.pointUnits())
        assertEquals(100.25, Store(prefs).points(), 0.0)
        assertEquals(0, prefs.getInt("completedCount", 0))
        assertTrue(Store(prefs).validateCustomChallengeToday(id, NOW + 4 * DAY) != null)
        assertEquals(401, store.pointUnits())
        val completed = customAt(id, NOW + 3 * DAY)
        assertTrue(completed.completed)
        assertTrue(completed.rewardClaimed)
        assertFalse(completed.canEditToday)
        assertEquals(2, completed.completedDays)
    }

    @Test
    fun sevenDayCustomChallengesKeepTheirProgressAcrossStandardSeriesRotations() {
        store.setDemoPlan(PlanType.PREMIUM)
        assertNull(store.createCustomChallenge("Ma semaine", CUSTOM_STEPS, 7, NOW + 20 * DAY))
        val id = store.readSnapshot(reading(0), NOW + 20 * DAY).customChallenges.single().id
        repeat(6) { day -> finishCustomDay(id, NOW + (20 + day * 2) * DAY) }
        val partial = customAt(id, NOW + 30 * DAY)
        assertEquals(1, store.readSnapshot(reading(0), NOW + 30 * DAY).challengeSeries)
        assertEquals(6, partial.completedDays)
        assertFalse(partial.completed)
        val beforeReward = store.pointUnits()
        finishCustomDay(id, NOW + 32 * DAY)
        assertEquals(beforeReward + 1, store.pointUnits())
        val afterAnotherRotation = customAt(id, NOW + 42 * DAY)
        assertEquals(7, afterAnotherRotation.requiredDays)
        assertEquals(7, afterAnotherRotation.completedDays)
        assertTrue(afterAnotherRotation.rewardClaimed)
    }

    @Test
    fun deletingAndRecreatingACustomChallengeRequiresNewWorkAndNeverReversesItsOldReward() {
        store.setDemoPlan(PlanType.PREMIUM)
        val original = createCustom("Bouger", 2)
        finishCustomDay(original, NOW)
        finishCustomDay(original, NOW + DAY)
        val rewardedUnits = store.pointUnits()
        store.setDemoPlan(PlanType.FREE)
        assertNull(store.deleteCustomChallenge(original))
        assertEquals(rewardedUnits, store.pointUnits())

        store.setDemoPlan(PlanType.PREMIUM)
        val createdAt = NOW + 2 * DAY
        assertNull(store.createCustomChallenge("Bouger", CUSTOM_STEPS, 2, createdAt))
        assertEquals(rewardedUnits, store.pointUnits())
        val recreated = store.readSnapshot(reading(0), createdAt).customChallenges.single()
        assertTrue(original != recreated.id)
        assertEquals(0, recreated.completedDays)
        assertTrue(recreated.todayCheckedSteps.isEmpty())
        assertFalse(recreated.rewardClaimed)
        assertTrue(store.setCustomChallengeStep(recreated.id, 0, true, NOW + DAY) != null)
        finishCustomDay(recreated.id, createdAt)
        val beforeFinalDay = store.pointUnits()
        finishCustomDay(recreated.id, createdAt + DAY)
        assertEquals(beforeFinalDay + 1, store.pointUnits())
    }

    @Test
    fun customActiveCapDoesNotCountFinishedChallengesAndFreeDowngradePreservesData() {
        store.setDemoPlan(PlanType.PREMIUM)
        repeat(MAX_ACTIVE_CUSTOM_CHALLENGES) {
            assertNull(store.createCustomChallenge("Défi $it", CUSTOM_STEPS, 2, NOW))
        }
        assertTrue(store.createCustomChallenge("En trop", CUSTOM_STEPS, 2, NOW) != null)
        val id = store.readSnapshot(reading(0), NOW).customChallenges.first().id
        finishCustomDay(id, NOW)
        finishCustomDay(id, NOW + DAY)
        assertNull(store.createCustomChallenge("Nouveau", CUSTOM_STEPS, 2, NOW + DAY))
        store.setDemoPlan(PlanType.FREE)
        val customs = store.readSnapshot(reading(0), NOW + DAY).customChallenges
        assertEquals(MAX_ACTIVE_CUSTOM_CHALLENGES + 1, customs.size)
        assertTrue(customs.none { it.canEditToday })
        val active = customs.first { !it.completed }
        assertTrue(store.setCustomChallengeStep(active.id, 0, true, NOW + DAY) != null)
        assertTrue(store.validateCustomChallengeToday(active.id, NOW + DAY) != null)
        assertNull(store.deleteCustomChallenge(active.id))
    }

    @Test
    fun deletingPersonalDataRemovesLocksProgressAndAllStoredKeysBeforeRestart() {
        store.tick(reading(5 * MINUTE), NOW)
        assertTrue(store.penalizeReopening(PACKAGE, NOW + 1))
        repeat(CHALLENGE_REQUIRED_DAYS) { day -> assertTrue(store.completeChallenge(0, NOW + day * DAY)) }
        assertTrue(store.completeChallenge(1, NOW))
        // Même une clé ajoutée à l'avenir ne doit pas survivre à l'effacement complet.
        store.setDemoPlan(PlanType.ULTRA)
        assertNull(store.createCustomChallenge("Privé", CUSTOM_STEPS, 2, NOW))
        val customId = store.readSnapshot(reading(0), NOW).customChallenges.single().id
        assertNull(store.setCustomChallengeStep(customId, 0, true, NOW))
        prefs.edit().putString("futurePersonalSetting", "private").apply()
        assertTrue(prefs.contains("apps"))
        assertTrue(prefs.contains("checked"))
        assertTrue(prefs.contains("completedCount"))
        assertTrue(prefs.contains("challengeDays"))
        assertTrue(prefs.contains("customChallenges"))

        assertTrue(store.clearPersonalData())

        assertTrue(prefs.all.isEmpty())
        assertTrue(store.packages().isEmpty())
        assertEquals(100.0, store.points(), 0.0)
        assertFalse(store.isPremium())
        val restarted = Store(prefs)
        val snapshot = restarted.readSnapshot(UsageReading(emptyMap(), null))
        assertTrue(snapshot.apps.isEmpty())
        assertEquals(100.0, snapshot.points, 0.0)
        assertEquals(0, snapshot.challengeSeries)
        assertTrue(snapshot.checkedChallenges.isEmpty())
        assertEquals(0, snapshot.totalCompletedChallenges)
        assertEquals(PlanType.FREE, snapshot.plan)
        assertTrue(snapshot.customChallenges.isEmpty())
        assertTrue(snapshot.challengeProgress.values.all { it.completedDays == 0 })
        assertFalse(prefs.contains("challengeDays"))
        assertEquals(21, snapshot.daysUntilChallenges)
        assertFalse(prefs.contains("futurePersonalSetting"))
        assertTrue(prefs.contains("periodStart"))
        assertTrue(prefs.contains("challengeStart"))
    }

    private fun reading(millis: Long) = UsageReading(mapOf(PACKAGE to millis), PACKAGE)

    private fun createCustom(title: String, requiredDays: Int): String {
        assertNull(store.createCustomChallenge(title, CUSTOM_STEPS, requiredDays, NOW))
        return store.readSnapshot(reading(0), NOW).customChallenges.single { it.title == title }.id
    }

    private fun customAt(id: String, now: Long): CustomChallenge =
        store.readSnapshot(reading(0), now).customChallenges.single { it.id == id }

    private fun finishCustomDay(id: String, now: Long) {
        repeat(3) { assertNull(store.setCustomChallengeStep(id, it, true, now)) }
        assertNull(store.validateCustomChallengeToday(id, now))
    }

    companion object {
        private const val NOW = 1_800_000_000_000L
        private const val MINUTE = 60_000L
        private const val DAY = 24 * 60 * MINUTE
        private const val PACKAGE = "com.example.social"
        private val CUSTOM_STEPS = listOf("Marcher", "Lire", "S’étirer")
    }
}

/** Le contrat SharedPreferences suffit ici : aucun service Android ni émulateur n'est requis. */
private class MemoryPreferences : SharedPreferences {
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
            pending.forEach { (key, value) ->
                if (value == null) values.remove(key) else values[key] = value
            }
            return true
        }
        override fun apply() { commit() }
    }
}
