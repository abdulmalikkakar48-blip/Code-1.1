package com.maree.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Stable destinations keep the menu selection when the language changes. */
internal enum class SettingsPage(private val frenchTitle: String, private val englishTitle: String) {
    LANGUAGES("Langue", "Language"),
    APPEARANCE("Apparence", "Appearance"),
    ACCESS("Accès Android", "Android access"),
    SUBSCRIPTIONS("Abonnements", "Subscriptions"),
    RULES("Règles de Selkie", "Selkie rules"),
    PRIVACY("Confidentialité et données", "Privacy and data");

    val title: String get() = tr(frenchTitle, englishTitle)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelkieMenuSheet(
    selectedPage: SettingsPage?,
    onSelect: (SettingsPage) -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.88f),
        drawerContainerColor = MareePalette.Surface,
        drawerContentColor = MareePalette.Ink) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item(key = "menu_header") {
                Row(Modifier.fillMaxWidth().padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BrandIcon()
                    Text("Selkie", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    TextButton(onClick = onClose,
                        modifier = Modifier.heightIn(min = 48.dp)) {
                        Text(tr("Fermer", "Close"))
                    }
                }
            }
            item(key = "menu_divider") {
                HorizontalDivider(Modifier.padding(vertical = 12.dp), color = MareePalette.Outline)
            }
            item(key = "settings_label") { MenuGroupLabel(tr("Réglages", "Settings")) }
            items(listOf(SettingsPage.LANGUAGES, SettingsPage.APPEARANCE, SettingsPage.ACCESS),
                key = { it.name }) { page ->
                MenuDestination(page, selectedPage == page, onSelect)
            }
            item(key = "plans_label") { MenuGroupLabel(tr("Offres", "Plans")) }
            item(key = SettingsPage.SUBSCRIPTIONS.name) {
                MenuDestination(SettingsPage.SUBSCRIPTIONS,
                    selectedPage == SettingsPage.SUBSCRIPTIONS, onSelect)
            }
            item(key = "information_label") { MenuGroupLabel(tr("Informations", "Information")) }
            items(listOf(SettingsPage.RULES, SettingsPage.PRIVACY), key = { it.name }) { page ->
                MenuDestination(page, selectedPage == page, onSelect)
            }
            item(key = "menu_version") {
                Text("Selkie · ${BuildConfig.VERSION_NAME}", color = MareePalette.Muted,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 20.dp))
            }
        }
    }
}

@Composable
private fun MenuGroupLabel(label: String) {
    Text(label, color = MareePalette.Muted, style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp).semantics { heading() })
}

@Composable
private fun MenuDestination(page: SettingsPage, selected: Boolean,
    onSelect: (SettingsPage) -> Unit) {
    NavigationDrawerItem(
        label = { Text(page.title, modifier = Modifier.padding(vertical = 4.dp)) },
        selected = selected,
        onClick = { onSelect(page) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MareePalette.Subtle,
            unselectedContainerColor = MareePalette.Surface,
            selectedTextColor = MareePalette.Teal,
            unselectedTextColor = MareePalette.Ink
        )
    )
}

@Composable
internal fun SettingsPageContent(
    page: SettingsPage,
    snapshot: MareeSnapshot,
    onSetup: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onNotifications: () -> Unit,
    onPlan: (PlanType) -> Unit,
    monitoring: Boolean,
    notificationsGranted: Boolean
) {
    // Privacy remains the existing screen, with its policy and data-deletion action.
    if (page == SettingsPage.PRIVACY) return
    key(page) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item(key = "page_heading") {
                Text(page.title, color = MareePalette.Ink,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.semantics { heading() })
            }
            when (page) {
                SettingsPage.LANGUAGES -> {
                    item {
                        Text(tr("Choisis la langue des écrans, du coach et de l’aide.",
                            "Choose the language for screens, the coach and help."),
                            color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
                    }
                    item { LanguageSelector() }
                }
                SettingsPage.APPEARANCE -> item { AppearanceSelector() }
                SettingsPage.ACCESS -> {
                    item {
                        AppCardSurface {
                            SectionLabel(tr("Le suivi", "Tracking"),
                                if (monitoring) tr("Actif", "Active") else tr("En pause", "Paused"))
                            Text(if (monitoring)
                                tr("Les limites de tes applications sont surveillées.",
                                    "Your app limits are being monitored.")
                                else tr("Aucune application n’est surveillée ou bloquée.",
                                    "No apps are being monitored or blocked."),
                                color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
                            PrimaryAction(
                                label = if (monitoring) tr("Gérer les autorisations", "Manage permissions")
                                    else tr("Activer le suivi", "Turn on tracking"),
                                onClick = if (monitoring) onSetup else onStart,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (monitoring) TextButton(onClick = onStop,
                                modifier = Modifier.heightIn(min = 48.dp)) {
                                Text(tr("Mettre le suivi en pause", "Pause tracking"))
                            }
                            if (!notificationsGranted) {
                                HorizontalDivider(color = MareePalette.Outline)
                                TextButton(onClick = onNotifications,
                                    modifier = Modifier.heightIn(min = 48.dp)) {
                                    Text(tr("Activer les alertes facultatives", "Turn on optional alerts"))
                                }
                            }
                        }
                    }
                    item {
                        AppCardSurface {
                            SectionLabel(tr("À quoi servent les accès ?", "What is the access for?"))
                            MenuBody(tr("Les données d’utilisation servent à mesurer les durées des applications suivies. L’affichage par-dessus les autres applications permet de montrer l’écran de pause quand une limite est atteinte.",
                                "Usage access measures time in tracked apps. Displaying over other apps lets Selkie show the pause screen when a limit is reached."))
                            MenuBody(tr("Ces deux accès sont nécessaires pour le suivi. Les notifications restent facultatives. Tu peux utiliser les défis et le coach avec le suivi en pause.",
                                "These two access permissions are needed for tracking. Notifications are optional. You can use challenges and the coach while tracking is paused."))
                        }
                    }
                }
                SettingsPage.SUBSCRIPTIONS -> {
                    item { MembershipSection(snapshot.plan, onPlan) }
                    items(helpEntriesFor(AppLanguage.current).filter { it.section == SelkieHelpSection.ACCOUNT },
                        key = { it.topic.id }) { entry -> HelpRuleCard(entry) }
                }
                SettingsPage.RULES -> {
                    item {
                        Text(tr("Les règles des limites, des points et des défis s’appliquent à toutes les offres.",
                            "The rules for limits, points and challenges apply to every plan."),
                            color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
                    }
                    items(helpEntriesFor(AppLanguage.current).filter { it.topic.id in ruleTopicIds },
                        key = { it.topic.id }) { entry -> HelpRuleCard(entry) }
                }
                SettingsPage.PRIVACY -> Unit
            }
        }
    }
}

private val ruleTopicIds = setOf(
    "limits", "reset", "red_screen", "usage", "points", "earn_points",
    "lose_points", "zero_points", "discipline_bonus", "challenges", "challenge_cycle",
    "custom_challenges", "challenge_photos"
)

@Composable
private fun HelpRuleCard(entry: SelkieHelpEntry) {
    AppCardSurface {
        Text(entry.topic.label, color = MareePalette.Ink,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() })
        entry.topic.replies.firstOrNull()?.let { MenuBody(it) }
    }
}

@Composable
private fun MenuBody(text: String) {
    Text(text, color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
}
