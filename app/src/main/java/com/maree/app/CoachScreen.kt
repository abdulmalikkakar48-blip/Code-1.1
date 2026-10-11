package com.maree.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val coachSituations = listOf(
    "Je scrolle sans réfléchir" to "Pose ton téléphone dix minutes hors de portée. Avant de le reprendre, choisis une seule chose à y faire. Une fois cette tâche terminée, referme l’application.",
    "Je dépasse mes limites" to "Choisis une seule application pour commencer et une limite réaliste. Quand l’écran de pause apparaît, prépare une alternative courte : boire de l’eau, marcher ou lire une page.",
    "J’ai du mal à tenir un défi" to "Réduis l’effort de chaque étape et choisis un moment précis de la journée. Les jours peuvent être espacés : une journée manquée ne supprime pas ta progression."
)

/** Conseils fixes hors ligne ; aucune réponse n'est présentée comme produite par une IA. */
@Composable
internal fun CoachTab() {
    var selected by rememberSaveable { mutableIntStateOf(-1) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("Un coup de pouce.", fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = MareePalette.Ink)
        Text("Choisis ce qui te ressemble aujourd’hui.", color = MareePalette.Muted,
            fontSize = 15.sp, lineHeight = 22.sp)
        Card(colors = CardDefaults.cardColors(containerColor = MareePalette.Subtle),
            shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Coach IA · à venir", color = MareePalette.Teal,
                    fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                Text("Le dialogue avec l’IA n’est pas encore activé. Les conseils ci-dessous sont écrits à l’avance et fonctionnent sans Internet.",
                    color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
            }
        }
        Text("CONSEILS HORS LIGNE", color = MareePalette.Teal,
            fontWeight = FontWeight.SemiBold, fontSize = 11.sp, letterSpacing = 1.sp)
        coachSituations.forEachIndexed { index, situation ->
            OutlinedButton(onClick = { selected = index }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (selected == index) MareePalette.Teal else MareePalette.Outline)) {
                Text(situation.first, modifier = Modifier.padding(vertical = 5.dp), fontSize = 14.sp)
            }
        }
        if (selected in coachSituations.indices) {
            Card(colors = CardDefaults.cardColors(containerColor = MareePalette.Surface),
                shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, MareePalette.Outline)) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("À essayer maintenant", fontWeight = FontWeight.Bold,
                        color = MareePalette.Ink, fontSize = 19.sp)
                    Text(coachSituations[selected].second, color = MareePalette.Muted,
                        fontSize = 16.sp, lineHeight = 25.sp)
                    Text("Conseil local · aucun message envoyé", color = MareePalette.Teal, fontSize = 12.sp)
                }
            }
        }
    }
}
