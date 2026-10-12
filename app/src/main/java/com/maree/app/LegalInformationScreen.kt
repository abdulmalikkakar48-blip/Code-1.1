package com.maree.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Readable offline pages, without a mandatory acceptance checkbox or a purchase action. */
@Composable
internal fun LegalInformationScreen(document: LegalDocument, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    key(document) {
        Scaffold(containerColor = MareePalette.Background) { inner ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(inner)
                    .statusBarsPadding().navigationBarsPadding(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item(key = "legal_header") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = onBack) { Text(tr("‹ Retour", "‹ Back")) }
                        ScreenHeading(
                            eyebrow = tr("INFORMATIONS", "INFORMATION"),
                            title = document.title,
                            description = LegalContent.introduction(document)
                        )
                        Text(
                            tr("Mis à jour le ${LegalContent.updatedOn}",
                                "Updated on ${LegalContent.updatedOn}"),
                            color = MareePalette.Muted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                items(LegalContent.sections(document), key = { it.title }) { section ->
                    AppCardSurface {
                        Text(section.title, color = MareePalette.Ink,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.semantics { heading() })
                        section.paragraphs.forEach { paragraph ->
                            Text(paragraph, color = MareePalette.Muted,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
