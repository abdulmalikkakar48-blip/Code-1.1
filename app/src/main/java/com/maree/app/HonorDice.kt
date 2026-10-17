package com.maree.app

import org.json.JSONObject
import java.util.UUID

internal const val HONOR_DICE_KEY = "honorDice_v1"
internal const val HONOR_DICE_POINTS_PER_FACE = 10

/** One bounded receipt, not a roll history. Units represent exact quarter points. */
internal data class HonorDiceResult(
    val operationId: String,
    val face: Int,
    val spentUnits: Int,
    val balanceAfterUnits: Int,
    // Older receipts had no policy field and deducted one point per pip. Retaining
    // that policy keeps their pending attempt idempotent when the app is updated.
    val pointsPerFace: Int = HONOR_DICE_POINTS_PER_FACE
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("operationId", operationId)
        put("face", face)
        put("spentQuarterUnits", spentUnits)
        put("balanceAfterQuarterUnits", balanceAfterUnits)
        if (pointsPerFace == HONOR_DICE_POINTS_PER_FACE) put("pointsPerFace", pointsPerFace)
    }
}

internal fun isHonorDiceOperationId(value: String): Boolean = try {
    value.length == 36 && UUID.fromString(value).toString() == value
} catch (_: IllegalArgumentException) { false }

/** Parse only the fixed, typed receipt fields; unknown fields never reach exports. */
internal fun readHonorDiceResult(raw: String?): HonorDiceResult? = try {
    if (raw == null || raw.length > 512) null else {
        val value = JSONObject(raw)
        val operation = value.opt("operationId") as? String
        val face = value.opt("face") as? Int
        val spent = value.opt("spentQuarterUnits") as? Int
        val balance = value.opt("balanceAfterQuarterUnits") as? Int
        val pointsPerFace = if (value.has("pointsPerFace")) {
            (value.opt("pointsPerFace") as? Int)?.takeIf { it == HONOR_DICE_POINTS_PER_FACE }
        } else 1
        if (operation == null || !isHonorDiceOperationId(operation) || face == null || face !in 1..6 ||
            pointsPerFace == null || spent == null ||
            spent !in 1..(face * pointsPerFace * POINT_UNITS_PER_POINT) || balance == null ||
            balance !in 0..MAX_POINT_UNITS || spent + balance > MAX_POINT_UNITS ||
            (spent < face * pointsPerFace * POINT_UNITS_PER_POINT && balance != 0)) null
        else HonorDiceResult(operation, face, spent, balance, pointsPerFace)
    }
} catch (_: Exception) { null }
