package com.maree.app

import java.math.BigDecimal

const val POINT_UNITS_PER_POINT = 4
const val CUSTOM_MIN_DAYS = 2
const val CUSTOM_MAX_DAYS = 365
const val MAX_ACTIVE_CUSTOM_CHALLENGES = 10

enum class BillingPeriod {
    MONTHLY, ANNUAL;

    val label: String get() = when (this) {
        MONTHLY -> tr("Mensuel", "Monthly")
        ANNUAL -> tr("Annuel", "Annual")
    }
    val priceSuffix: String get() = when (this) {
        MONTHLY -> tr("/ mois", "/ month")
        ANNUAL -> tr("/ an", "/ year")
    }
}

/** null représente l’absence de plafond commercial sur les applications compatibles. */
enum class PlanType(val maxTrackedApps: Int?, val monthlyPriceEuros: Int) {
    FREE(10, 0), PREMIUM(50, 10), ULTRA(null, 20);

    val canCreateCustomChallenges: Boolean get() = this != FREE
    /** Size of the prepared library, not a monthly quota or a usage counter. */
    val coachQuestionCount: Int get() = preparedCoachQuestionCount(this)
    /** Prix prévus, sans facturation active : l’année coûte douze mensualités. */
    val annualPriceEuros: Int get() = monthlyPriceEuros * 12
    fun priceEuros(period: BillingPeriod): Int = when (period) {
        BillingPeriod.MONTHLY -> monthlyPriceEuros
        BillingPeriod.ANNUAL -> annualPriceEuros
    }
    val displayName: String get() = when (this) {
        FREE -> tr("Gratuit", "Free")
        PREMIUM -> "Premium"
        ULTRA -> "Ultra"
    }
}

/** Un solde provient de quarts entiers ; le séparateur suit la langue sans zéros ajoutés. */
fun formatPoints(points: Double): String {
    val value = BigDecimal.valueOf(points).stripTrailingZeros().toPlainString()
    return if (AppLanguage.current == AppLocale.FRENCH) value.replace('.', ',') else value
}

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
