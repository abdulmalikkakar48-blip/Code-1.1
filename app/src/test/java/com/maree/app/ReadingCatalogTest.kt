package com.maree.app

import org.json.JSONArray
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.net.URI
import java.security.MessageDigest

class ReadingCatalogTest {
    private fun projectFile(path: String): File = File(path).takeIf { it.exists() } ?: File(if (path.startsWith("../")) path.removePrefix("../") else "app/$path")
    private fun asset(resource: ReadingResource, language: AppLocale): String =
        projectFile("src/main/assets/reading/${resource.assetName}-${language.tag}.txt").readText(Charsets.UTF_8)

    @Test fun catalogueHasUniqueStableEntriesAndBothReadableLanguageAssets() {
        assertEquals(16, readingResources.size)
        assertEquals(16, readingResources.map { it.id }.toSet().size)
        readingResources.forEach { resource ->
            assertTrue(resource.assetName.matches(Regex("[a-z0-9-]+")))
            assertTrue(resource.titleFr.isNotBlank() && resource.titleEn.isNotBlank())
            assertTrue(resource.editionNoteFr.isNotBlank() && resource.editionNoteEn.isNotBlank())
            AppLocale.entries.forEach { language -> assertTrue(asset(resource, language).length in 150..10_000) }
        }
    }
    @Test fun shippedPassagesMatchTheReviewedProvenanceHashes() {
        val manifest = JSONArray(projectFile("../docs/LECTURES_PROVENANCE.json").readText())
        assertEquals(readingResources.size, manifest.length())
        readingResources.forEach { resource ->
            val record = (0 until manifest.length()).map { manifest.getJSONObject(it) }.single { it.getString("id") == resource.id }
            assertEquals(resource.sourceUrl, record.getString("sourceUrl"))
            AppLocale.entries.forEach { language ->
                val digest = MessageDigest.getInstance("SHA-256").digest(asset(resource, language).toByteArray(Charsets.UTF_8))
                    .joinToString("") { "%02x".format(it.toInt() and 255) }
                assertEquals(record.getJSONObject("assets").getString(language.tag), digest)
            }
        }
    }
    @Test fun exportContainsChosenTextLanguageAndReferencesWithoutOtherReadings() {
        val chosen = readingResources.single { it.id == "ghazali-observation" }
        AppLocale.entries.forEach { language ->
            val body = asset(chosen, language)
            val output = readingExportContent(chosen, language, body)
            assertTrue(output.contains(body)); assertTrue(output.contains(chosen.sourceUrl))
            assertTrue(output.startsWith(if (language == AppLocale.ENGLISH) chosen.titleEn else chosen.titleFr))
            assertTrue(output.contains(if (language == AppLocale.ENGLISH) chosen.editionNoteEn else chosen.editionNoteFr))
            assertFalse(output.contains("Franklin")); assertFalse(output.contains("Smiles"))
        }
    }
    @Test fun allExternalSourcesUseVerifiedPublicHttpsHostsWithoutCredentials() {
        readingResources.forEach { resource ->
            val uri = URI(resource.sourceUrl)
            assertEquals("https", uri.scheme); assertEquals("raw.githubusercontent.com", uri.host)
            assertNull(uri.userInfo); assertNull(uri.query)
        }
    }
    @Test fun catalogueSearchIgnoresAccentsAndCaseAndRequiresAllWords() {
        assertTrue(filterReadingResources("MÉTHODE", null, AppLocale.FRENCH).any { it.id == "descartes-method" })
        assertEquals(listOf("descartes-method"), filterReadingResources("four rules", null, AppLocale.ENGLISH).map { it.id })
        assertTrue(filterReadingResources("four rules unrelatedzzz", null, AppLocale.ENGLISH).isEmpty())
        assertEquals(2, filterReadingResources("IBN TAYMIYYA", ReadingGenre.REFLECTION, AppLocale.FRENCH).size)
    }
    @Test fun genreFilterKeepsAllExistingResourcesAndRestrictsSearch() {
        val poems = filterReadingResources("", ReadingGenre.POETRY, AppLocale.FRENCH)
        assertTrue(poems.any { it.id == "fontaine-patience" }); assertTrue(poems.all { it.genre == ReadingGenre.POETRY })
        assertTrue(filterReadingResources("Ghazali", ReadingGenre.POETRY, AppLocale.FRENCH).isEmpty())
        assertEquals(readingResources.size, filterReadingResources("", null, AppLocale.ENGLISH).size)
        val originalIds = setOf("descartes-method", "bacon-studies", "franklin-habits", "ghazali-observation",
            "fontaine-patience", "smiles-method", "taymiyya-worship", "taymiyya-good-deeds")
        assertTrue(readingResources.map { it.id }.toSet().containsAll(originalIds))
    }

}
