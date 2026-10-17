package com.maree.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Navigation only: opening the guide never changes tracking, points or the selected plan. */
internal enum class GuideDestination {
    HOME, DAILY, CALENDAR, REVIEW, READING, DICE, COLORING, TOOLS, CHALLENGES, COACH,
    ACCESS, SUBSCRIPTIONS, APPEARANCE, LANGUAGES, PRIVACY
}

/** A reusable, scroll-container-neutral guide for the home screen and the information menu. */
@Composable
internal fun FeatureGuideSection(onOpen: (GuideDestination) -> Unit) {
    var showSettings by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ScreenHeading(
            tr("BIENVENUE DANS SELKIE", "WELCOME TO SELKIE"),
            tr("Selkie, pas à pas.", "Selkie, step by step."),
            tr("Moins de temps dans les applications, plus de place pour tes journées. Les quatre onglets du bas ont chacun leur rôle.",
                "Less time in apps, more room for your day. Each of the four bottom tabs has its own purpose.")
        )

        AppCardSurface {
            GuideSectionHeading(
                "1", tr("Accueil", "Home"),
                tr("Choisir les applications à limiter.", "Choose which apps to limit.")
            )
            GuideBody(tr("Active le suivi, choisis une application et fixe une durée. Par exemple : une limite de 30 minutes pour ton application de vidéos.",
                "Turn on tracking, choose an app and set a time limit. For example: a 30-minute limit for your video app."))
            GuideBody(tr("Quand la limite est atteinte avec le suivi actif, Selkie verrouille l’application pendant 24 heures. Les accès Android permettent de mesurer le temps et d’afficher l’écran de pause.",
                "When the limit is reached while tracking is active, Selkie locks the app for 24 hours. Android access lets it measure time and show the pause screen."))
            PrimaryAction(tr("Voir mes applications", "See my apps"),
                onClick = { onOpen(GuideDestination.HOME) }, modifier = Modifier.fillMaxWidth())
        }

        AppCardSurface {
            GuideSectionHeading(
                "2", tr("Quotidien", "Daily"),
                tr("Organiser ta journée et voir ta progression.", "Plan your day and see your progress.")
            )
            GuideBody(tr("Retrouve ton résumé du jour, puis choisis la rubrique qui t’intéresse.",
                "See your daily summary, then choose the section you need."))
            PrimaryAction(tr("Voir mon quotidien", "Open Daily"),
                onClick = { onOpen(GuideDestination.DAILY) }, modifier = Modifier.fillMaxWidth())
            HorizontalDivider(color = MareePalette.Outline)
            GuideLink(
                tr("Calendrier", "Calendar"),
                tr("Gratuit · Choisis une date, écris tes tâches et coche ce qui est fait.",
                    "Free · Choose a date, write your tasks and check off what is done."),
                { onOpen(GuideDestination.CALENDAR) }
            )
            GuideLink(
                tr("Bilan de la semaine", "Weekly review"),
                tr("Résumé gratuit · Détail par application en Premium et Ultra. Le bilan utilise les périodes réellement observées.",
                    "Free summary · App details in Premium and Ultra. The review uses periods that were actually observed."),
                { onOpen(GuideDestination.REVIEW) }
            )
            GuideLink(
                tr("Lecture", "Reading"),
                tr("Gratuit · Lis et enregistre des extraits sélectionnés pour réfléchir et apprendre.",
                    "Free · Read and save selected excerpts to reflect and learn."),
                { onOpen(GuideDestination.READING) }
            )
            GuideLink(tr("Dé Selkie", "Selkie dice"),
                tr("Gratuit · Activité, application ou semaine : confirme un objectif au calendrier. Le mode Points, facultatif, retire de 1 à 6 points après confirmation du lancer.",
                    "Free · Activity, app or week: confirm a calendar goal. The optional Points mode deducts 1 to 6 points after you confirm the roll."),
                { onOpen(GuideDestination.DICE) })
            GuideLink(tr("Coloriage", "Coloring"),
                tr("Gratuit · Illustrations : huit dessins à remplir par zones, classés en Fleurs, Paysages et Motifs, avec zoom et repères numérotés facultatifs. À carreaux : les ${coloringTemplates.size} pages précédentes. Tes couleurs restent sur ce téléphone.",
                    "Free · Illustrations: eight drawings to fill by region, grouped into Flowers, Landscapes and Patterns, with zoom and optional numbered guides. Grid pages: the ${coloringTemplates.size} existing pages. Your colors stay on this phone."),
                { onOpen(GuideDestination.COLORING) })
            GuideLink(
                tr("Outils", "Tools"),
                tr("Premium et Ultra : plages horaires et widget. Ultra : programmes guidés de 7, 14 ou 30 jours.",
                    "Premium and Ultra: time slots and a widget. Ultra: guided programs lasting 7, 14 or 30 days."),
                { onOpen(GuideDestination.TOOLS) }
            )
        }

        AppCardSurface {
            GuideSectionHeading(
                "3", tr("Défis", "Challenges"),
                tr("Remplacer un peu d’écran par une action concrète.", "Replace some screen time with a real activity.")
            )
            GuideBody(tr("Choisis un défi, réalise ses étapes et valide ta journée. Chaque défi se poursuit sur plusieurs jours. Par exemple : lire quelques pages chaque jour.",
                "Choose a challenge, complete its steps and check in for the day. Each challenge continues over several days. For example: read a few pages each day."))
            GuideBody(tr("Les défis proposés sont gratuits. Premium et Ultra permettent aussi de créer tes propres défis avec trois étapes et une durée d’au moins deux jours.",
                "The suggested challenges are free. Premium and Ultra also let you create your own challenges with three steps and a duration of at least two days."))
            PrimaryAction(tr("Choisir un défi", "Choose a challenge"),
                onClick = { onOpen(GuideDestination.CHALLENGES) }, modifier = Modifier.fillMaxWidth())
        }

        AppCardSurface {
            GuideSectionHeading(
                "4", tr("Coach", "Coach"),
                tr("Trouver un conseil ou comprendre Selkie.", "Find a tip or understand Selkie.")
            )
            GuideBody(tr("Écris une question sur tes habitudes ou choisis une question proposée. Le coach utilise des réponses préparées, disponibles hors ligne.",
                "Write a question about your habits or choose a suggested question. The coach uses prepared responses, available offline."))
            GuideBody(tr("Par exemple : « Comment moins regarder mon téléphone le soir ? » Ta question et la réponse restent dans deux espaces séparés. Enregistre tes conseils préférés dans les favoris gratuits.",
                "For example: “How can I use my phone less in the evening?” Your question and its answer appear in separate areas. Save your favorite tips with the free favorites feature."))
            GuideBody(tr("La bibliothèque de questions est plus grande en Premium et en Ultra. Ce nombre décrit la bibliothèque accessible, pas un quota de messages.",
                "Premium and Ultra offer a larger question library. This number describes the accessible library, not a message quota."))
            PrimaryAction(tr("Ouvrir le coach", "Open the coach"),
                onClick = { onOpen(GuideDestination.COACH) }, modifier = Modifier.fillMaxWidth())
        }

        AppCardSurface(containerColor = MareePalette.Subtle) {
            SectionLabel(tr("Les points, simplement", "Points, simply explained"))
            GuideBody(tr("Tu commences avec 100 points et ton solde reste entre 0 et 100. Les bonus et les défis permettent d’en regagner jusqu’à ce plafond. Ce n’est pas de l’argent.",
                "You start with 100 points and your balance stays between 0 and 100. Bonuses and challenges let you earn points back up to that cap. They are not money."))
            GuideBody(tr("À 0 point, ajouter une application ou modifier une limite reste bloqué dans toutes les offres. Premium et Ultra ne contournent pas cette règle.",
                "At 0 points, adding an app or changing a limit is blocked on every plan. Premium and Ultra follow the same rule."))
        }

        AppCardSurface {
            TextButton(onClick = { showSettings = !showSettings },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(tr("Tes réglages et informations", "Your settings and information"),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f).semantics { heading() })
                    Text(if (showSettings) tr("Masquer", "Hide") else tr("Voir", "Show"),
                        style = MaterialTheme.typography.labelMedium)
                }
            }
            GuideBody(tr("Le menu en haut de l’application rassemble la langue, l’apparence, les accès Android, les offres et tes données.",
                "The menu at the top of the app brings together language, appearance, Android access, plans and your data."))
            if (showSettings) {
                GuideLink(tr("Langue", "Language"),
                    tr("Choisir français ou anglais.", "Choose French or English."),
                    { onOpen(GuideDestination.LANGUAGES) })
                GuideLink(tr("Apparence", "Appearance"),
                    tr("Choisir le mode clair, nuit ou celui du téléphone.",
                        "Choose light, night or your phone’s setting."),
                    { onOpen(GuideDestination.APPEARANCE) })
                GuideLink(tr("Accès Android", "Android access"),
                    tr("Comprendre les accès et activer ou mettre en pause le suivi.",
                        "Understand access and turn tracking on or pause it."),
                    { onOpen(GuideDestination.ACCESS) })
                GuideLink(tr("Abonnements", "Subscriptions"),
                    tr("Comparer Gratuit, Premium et Ultra.", "Compare Free, Premium and Ultra."),
                    { onOpen(GuideDestination.SUBSCRIPTIONS) })
                GuideLink(tr("Confidentialité et données", "Privacy and data"),
                    tr("Lire la politique, exporter tes données ou les effacer.",
                        "Read the policy, export your data or delete it."),
                    { onOpen(GuideDestination.PRIVACY) })
            }
            if (BuildConfig.DEBUG) {
                HorizontalDivider(color = MareePalette.Outline)
                Text(tr("Version de test : le choix Premium ou Ultra permet d’essayer les fonctions. Aucun paiement n’est prélevé.",
                    "Test version: choosing Premium or Ultra lets you try the features. No payment is charged."),
                    color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun GuideSectionHeading(number: String, title: String, purpose: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(12.dp),
            color = MareePalette.Subtle, contentColor = MareePalette.Teal) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(number, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, color = MareePalette.Ink, style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() })
            Text(purpose, color = MareePalette.Teal, style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun GuideBody(text: String) {
    Text(text, color = MareePalette.Muted, style = MaterialTheme.typography.bodyMedium)
}

/** One full-width target per destination stays readable on narrow screens and with large text. */
@Composable
private fun GuideLink(title: String, description: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, MareePalette.ControlOutline),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, color = MareePalette.Teal, style = MaterialTheme.typography.labelLarge)
            Text(description, color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        }
    }
}
