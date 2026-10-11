package com.maree.app

import java.math.BigDecimal

const val POINT_UNITS_PER_POINT = 4
const val CUSTOM_MIN_DAYS = 2
const val CUSTOM_MAX_DAYS = 365
const val MAX_ACTIVE_CUSTOM_CHALLENGES = 10

/** null représente l’absence de plafond commercial sur les applications compatibles. */
enum class PlanType(val maxTrackedApps: Int?, val monthlyPriceEuros: Int) {
    FREE(10, 0), PREMIUM(50, 10), ULTRA(null, 20);

    val canCreateCustomChallenges: Boolean get() = this != FREE
    val displayName: String get() = when (this) {
        FREE -> "Gratuit"
        PREMIUM -> "Premium"
        ULTRA -> "Ultra"
    }
}

/** Un solde provient de quarts entiers ; l'affichage n'ajoute ni zéros ni séparateur anglais. */
fun formatPoints(points: Double): String =
    BigDecimal.valueOf(points).stripTrailingZeros().toPlainString().replace('.', ',')

fun formatPointUnits(pointUnits: Int): String = formatPoints(pointUnits / POINT_UNITS_PER_POINT.toDouble())

data class CustomChallenge(
    val id: String,
    val title: String,
    val steps: List<String>,
    val completedDays: Int,
    val requiredDays: Int,
    val todayCheckedSteps: Set<Int>,
    val validatedToday: Boolean,
    val canEditToday: Boolean,
    val canValidateToday: Boolean,
    val completed: Boolean,
    val rewardClaimed: Boolean
)
