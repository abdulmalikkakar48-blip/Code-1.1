package com.maree.app

import org.junit.Assert.*
import org.junit.Test

class RecipeCatalogTest {
    @Test fun fiftyBilingualRecipesHaveQuantitiesMethodsAndUsefulCategories() {
        assertEquals(50, recipes.size); assertEquals(50, recipes.map { it.id }.toSet().size)
        assertEquals(listOf(10, 15, 15, 10), RecipeCategory.entries.map { c -> recipes.count { it.category == c } })
        recipes.forEach { recipe ->
            assertTrue(recipe.minutes in 5..300 && recipe.servings in 1..8)
            assertTrue(recipe.titleFr.isNotBlank() && recipe.titleEn.isNotBlank())
            assertEquals(recipe.ingredientsFr.size, recipe.ingredientsEn.size)
            assertEquals(recipe.stepsFr.size, recipe.stepsEn.size)
            assertTrue(recipe.stepsFr.size >= 3 && recipe.stepsFr.all { it.length > 20 })
            assertTrue(recipe.ingredientsFr.all { it.any(Char::isDigit) })
            assertTrue(recipe.ingredientsEn.all { it.any(Char::isDigit) })
            val text = (recipe.ingredientsFr + recipe.ingredientsEn).joinToString(" ").lowercase()
            assertFalse(recipe.id, Regex("\\b(vin|wine|beer|bière|rhum|rum|liqueur|porc|pork|bacon|lard|jambon|ham|gélatine|gelatin|vodka)\\b").containsMatchIn(text))
        }
    }
    @Test fun searchSupportsAccentsIngredientsAndCategoryWithoutChangingTheCatalog() {
        assertTrue(searchRecipes("creme", null).any { it.id == "potato_gratin" })
        assertTrue(searchRecipes("oeufs", null).any { it.id == "shakshuka" })
        assertTrue(searchRecipes("chickpea", RecipeCategory.MAINS).any { it.id == "chickpea_curry" })
        assertTrue(searchRecipes("chickpea", RecipeCategory.DRINKS).isEmpty())
        assertTrue(searchRecipes("zzzz-no-ingredient", null).isEmpty())
        assertEquals(50, searchRecipes("", null).size)
    }
}
