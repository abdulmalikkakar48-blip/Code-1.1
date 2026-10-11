package com.maree.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
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
    Column(Modifier.fillMaxWidth()) {
        if (days.first > 1) {
            Text("Journées 1 à ${days.first - 1} validées",
                modifier = Modifier.padding(bottom = 10.dp),
                color = MareePalette.Muted, fontSize = 12.sp)
        }
        days.forEach { day ->
            val done = day <= completed
            Row(Modifier.fillMaxWidth().semantics(mergeDescendants = true) {}
                .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(26.dp).background(
                    if (done) MareePalette.Teal else MareePalette.Subtle, CircleShape),
                    contentAlignment = Alignment.Center) {
                    Text(if (done) "✓" else day.toString(),
                        color = if (done) Color.White else MareePalette.Teal,
                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Text("Jour $day", modifier = Modifier.weight(1f),
                    color = MareePalette.Ink, fontSize = 14.sp,
                    fontWeight = FontWeight.Medium)
                Text(when {
                    done -> "Validé"
                    day == completed + 1 && !validatedToday -> "À valider"
                    else -> "À venir"
                }, color = if (done) MareePalette.Teal else MareePalette.Muted,
                    fontSize = 12.sp)
            }
            if (day != days.last) HorizontalDivider(color = MareePalette.Outline)
        }
        if (requiredDays > 7) {
            TextButton(onClick = { expanded = !expanded }, modifier = Modifier.align(Alignment.End)) {
                Text(if (expanded) "Réduire la liste" else "Voir les $requiredDays journées")
            }
        }
    }
}
