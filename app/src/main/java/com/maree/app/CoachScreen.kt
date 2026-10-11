package com.maree.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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

private data class LocalCoachTip(val situation: String, val title: String, val steps: List<String>)

private val coachSituations = listOf(
    LocalCoachTip("Je scrolle sans réfléchir", "Reprends la main en trois gestes", listOf(
        "Pose ton téléphone dix minutes hors de portée.",
        "Avant de le reprendre, choisis une seule chose à y faire.",
        "Une fois cette tâche terminée, referme l’application."
    )),
    LocalCoachTip("Je dépasse mes limites", "Commence par une seule habitude", listOf(
        "Choisis une application et une limite réaliste pour commencer.",
        "Prépare une alternative courte : boire de l’eau, marcher ou lire une page.",
        "Quand l’écran de pause apparaît, essaie cette alternative."
    )),
    LocalCoachTip("J’ai du mal à tenir un défi", "Fais de la place pour un petit pas", listOf(
        "Choisis un défi adapté à ce que tu peux faire aujourd’hui.",
        "Prévois un moment précis pour le pratiquer.",
        "Les jours peuvent être espacés : une journée manquée ne supprime pas ta progression."
    ))
)

/** Conseils fixes hors ligne ; aucune réponse n'est présentée comme produite par une IA. */
@Composable
internal fun CoachTab() {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val advice = coachSituations[selected.coerceIn(coachSituations.indices)]
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ScreenHeading(
            eyebrow = "COACH",
            title = "Un coup de pouce.",
            description = "Des gestes simples pour retrouver ton attention."
        )
        AppCardSurface {
            StatusPill("Conseils hors ligne")
            Text("Comment te sens-tu aujourd’hui ?", color = MareePalette.Ink,
                fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 27.sp)
            coachSituations.forEachIndexed { index, situation ->
                val isSelected = selected == index
                OutlinedButton(
                    onClick = { selected = index },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) MareePalette.Subtle else MareePalette.Surface,
                        contentColor = if (isSelected) MareePalette.Teal else MareePalette.Ink
                    ),
                    border = BorderStroke(1.dp,
                        if (isSelected) MareePalette.Teal else MareePalette.Outline)
                ) {
                    Text(situation.situation, modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 15.sp, lineHeight = 22.sp)
                }
            }
        }
        AppCardSurface {
            SectionLabel("À essayer maintenant")
            Text(advice.title, color = MareePalette.Ink, fontWeight = FontWeight.Bold,
                fontSize = 22.sp, lineHeight = 29.sp)
            advice.steps.forEachIndexed { index, step ->
                if (index > 0) HorizontalDivider(color = MareePalette.Outline)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ÉTAPE ${index + 1}", color = MareePalette.Teal,
                        fontWeight = FontWeight.SemiBold, fontSize = 12.sp, letterSpacing = .6.sp)
                    Text(step, color = MareePalette.Muted, fontSize = 16.sp, lineHeight = 24.sp)
                }
            }
            Text("Conseil écrit à l’avance · aucun message envoyé",
                color = MareePalette.Muted, fontSize = 13.sp, lineHeight = 19.sp)
        }
        AppCardSurface {
            SectionLabel("Le dialogue IA")
            Text("Pas encore disponible", color = MareePalette.Ink,
                fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 24.sp)
            Text("Le coach IA n’est pas connecté. Pour le moment, cet espace propose uniquement les conseils locaux ci-dessus.",
                color = MareePalette.Muted, fontSize = 14.sp, lineHeight = 21.sp)
        }
        Spacer(Modifier.height(4.dp))
    }
}
