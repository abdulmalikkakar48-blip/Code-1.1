package com.maree.app

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
internal fun HomeWidgetSection(plan: PlanType, onOffers: () -> Unit) {
    val context = LocalContext.current
    var instruction by remember { mutableStateOf(false) }
    AppCardSurface {
        SectionLabel(tr("Selkie sur ton accueil", "Selkie on your home screen"))
        Text(tr("Retrouve tes points et un défi sans ouvrir tous les écrans. Le widget affiche ta progression sur l’écran d’accueil de ton téléphone.",
            "See your points and a challenge without opening every screen. The widget displays your progress on your phone's home screen."), color = MareePalette.Muted)
        if (plan == PlanType.FREE) {
            PrimaryAction(tr("Découvrir le widget Premium", "Explore the Premium widget"), onClick = onOffers)
        } else {
            PrimaryAction(tr("Ajouter le widget", "Add widget"), onClick = {
                val manager = AppWidgetManager.getInstance(context)
                instruction = try {
                    !manager.isRequestPinAppWidgetSupported ||
                        !manager.requestPinAppWidget(ComponentName(context, SelkieWidgetProvider::class.java), null, null)
                } catch (_: Exception) { true }
            })
        }
        if (instruction) Text(tr("Maintiens une zone vide de l’écran d’accueil → Widgets → Selkie, puis place le widget.",
            "Long-press an empty area of your home screen → Widgets → Selkie, then place the widget."), color = MareePalette.Muted)
        Text(tr("Le contenu peut avoir jusqu’à une minute de décalage lorsque Selkie est active. Il ne valide aucun défi et ne dépense aucun point.",
            "Content may lag by up to a minute while Selkie is active. It never checks in a challenge or spends points."), color = MareePalette.Muted)
    }
}
