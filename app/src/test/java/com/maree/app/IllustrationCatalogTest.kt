package com.maree.app

import org.junit.Assert.*
import org.junit.Test

class IllustrationCatalogTest {
    @Test fun stableUniquePageIdsPreserveSavedDrawingIdentityAndHaveDistinctCurvedArtwork() {
        assertEquals(listOf("rose_garden", "wild_bouquet", "spring_tulips", "botanical_leaves",
            "waterfall_valley", "alpine_lake", "coastal_garden", "patterned_tree"),
            illustrationPages.map { it.id })
        // Saved colors use region positions: keep all existing page sizes and order.
        assertEquals(listOf(48, 71, 47, 55), illustrationPages.take(4).map { it.regions.size })
        assertEquals(illustrationPages.size, illustrationPages.map { it.id }.toSet().size)
        assertEquals(illustrationPages.size, illustrationPages.map { page -> page.regions.map { it.pathData } }.toSet().size)
        illustrationPages.forEach { page ->
            assertTrue("${page.id} must offer useful detail without an excessive region count", page.regions.size in 30..250)
            assertEquals("${page.id}: duplicate region IDs would break editing controls",
                page.regions.size, page.regions.map { it.id }.toSet().size)
            assertTrue(page.regions.all { it.id.isNotBlank() })
        }
    }

    @Test fun numberedRegionsAreClosedPathsWithFiniteLabelsInsideTheDrawingViewport() {
        illustrationPages.forEach { page ->
            page.regions.forEach { region ->
                val label = "${page.id}/${region.id}"
                val path = region.pathData.trim()
                assertTrue("$label must begin with a move command", path.startsWith("M", ignoreCase = true))
                assertTrue("$label must be a closed area that can be filled", path.endsWith("Z", ignoreCase = true))
                assertTrue("$label must contain a drawn outline", path.any { it in "LlHhVvCcQqSsTtAa" })
                if (page.category == IllustrationCategory.FLOWERS) {
                    assertTrue("$label must preserve the existing curved floral outlines", path.any { it in "CcQqSsTtAa" })
                }
                assertTrue("$label has an unknown suggested palette color", region.colorNumber in 1..ILLUSTRATION_COLOR_COUNT)
                assertTrue("$label label X must fit the drawing", region.labelX.isFinite() && region.labelX in 0f..1_000f)
                assertTrue("$label label Y must fit the drawing", region.labelY.isFinite() && region.labelY in 0f..1_000f)
            }
        }
    }
}
