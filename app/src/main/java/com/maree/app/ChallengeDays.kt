package com.maree.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Les journées de pratique restent ordonnées et séparées, même pour une longue durée. */
@Composable
internal fun ChallengeDays(requiredDays: Int, completedDays: Int, validatedToday: Boolean) {
    var expanded by rememberSaveable(requiredDays) { mutableStateOf(false) }
    val completed = completedDays.coerceIn(0, requiredDays)
    val days = if (requiredDays <= 7 || expanded) 1..requiredDays
        else maxOf(1, completed - 1)..minOf(requiredDays, completed + 2)
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        days.forEach { day ->
            Row(Modifier.fillMaxWidth().padding(vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Jour $day", color = MareePalette.Ink, fontSize = 14.sp,
                    fontWeight = FontWeight.Medium)
                Text(when {
                    day <= completed -> "✓ Validé"
                    day == completed + 1 && !validatedToday -> "Prochaine journée"
                    else -> "À venir"
                }, color = if (day <= completed) MareePalette.Teal else MareePalette.Muted,
                    fontSize = 13.sp)
            }
            if (day != days.last) HorizontalDivider(color = MareePalette.Outline)
        }
        if (requiredDays > 7) {
            TextButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Réduire la liste" else "Voir les $requiredDays journées")
            }
        }
    }
}
