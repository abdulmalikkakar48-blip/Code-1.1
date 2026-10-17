package com.maree.app

import org.junit.Assert.*
import org.junit.Test

class IllustrationCategoryTest {
    @Test fun categoriesKeepAllExistingDrawingsAndPartitionTheExpandedGallery() {
        val flowers = illustrationPagesForCategory(IllustrationCategory.FLOWERS)
        assertEquals(listOf("rose_garden", "wild_bouquet", "spring_tulips", "botanical_leaves",
            "sunflower_bouquet", "water_lilies", "iris_garden", "magnolia_branch"),
            flowers.map { it.id })
        assertEquals(listOf("waterfall_valley", "alpine_lake", "coastal_garden",
            "forest_path", "desert_dunes", "terraced_garden", "night_lake"),
            illustrationPagesForCategory(IllustrationCategory.LANDSCAPES).map { it.id })
        assertEquals(listOf("patterned_tree", "garden_mandala", "woven_leaves", "seashell_spiral", "patterned_mountains"),
            illustrationPagesForCategory(IllustrationCategory.PATTERNS).map { it.id })
        val categorized = IllustrationCategory.entries.flatMap { illustrationPagesForCategory(it) }
        assertEquals(20, categorized.size)
        assertEquals(illustrationPages.toSet(), categorized.toSet())
        assertEquals(illustrationPages, illustrationPagesForCategory(null))
    }

    @Test fun changingFilterOrRestoringASelectionCannotLeaveAnInvisibleDrawingSelected() {
        val filters = listOf(null) + IllustrationCategory.entries
        filters.forEach { category ->
            val visible = illustrationPagesForCategory(category)
            illustrationPages.forEach { previous ->
                val selected = selectedIllustrationPage(visible, previous.id)
                assertTrue("${previous.id} / $category must resolve to a visible page", selected in visible)
                if (previous in visible) assertSame(previous, selected)
                else assertSame(visible.first(), selected)
            }
        }
    }

    @Test fun anUnknownRestoredPageFallsBackInsideItsChosenCategory() {
        val visible = illustrationPagesForCategory(IllustrationCategory.LANDSCAPES)
        assertEquals("waterfall_valley", selectedIllustrationPage(visible, "removed_or_invalid_id").id)
    }
}
