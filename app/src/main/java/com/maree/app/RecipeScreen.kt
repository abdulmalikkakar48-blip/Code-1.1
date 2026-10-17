package com.maree.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.Normalizer

internal fun searchRecipes(query: String, category: RecipeCategory?): List<Recipe> {
    fun normalized(value: String) = Normalizer.normalize(value.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "")
    val words = normalized(query.trim()).split(Regex("\\s+")).filter { it.isNotEmpty() }
    return recipes.filter { recipe ->
        (category == null || recipe.category == category) && words.all { word ->
            word in normalized(recipe.titleFr + " " + recipe.titleEn + " " + recipe.ingredientsFr.joinToString(" ") + " " + recipe.ingredientsEn.joinToString(" "))
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun RecipeSection() {
    var category by rememberSaveable { mutableStateOf<RecipeCategory?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    var offset by rememberSaveable { mutableIntStateOf(0) }
    var selectedId by rememberSaveable { mutableStateOf(recipes.first().id) }
    var detailRequest by remember { mutableIntStateOf(0) }
    val detail = remember { BringIntoViewRequester() }
    val matches = remember(query, category) { searchRecipes(query, category) }
    val lastPage = ((matches.size - 1).coerceAtLeast(0)) / 6
    val currentPage = offset.coerceIn(0, lastPage)
    val selected = matches.firstOrNull { it.id == selectedId } ?: matches.firstOrNull()
    LaunchedEffect(detailRequest) { if (detailRequest > 0) detail.bringIntoView() }
    AppCardSurface {
        SectionLabel(tr("Une pause en cuisine", "A cooking break"), tr("Gratuit", "Free"))
        Text(tr("50 recettes, des idées simples à préparer.", "50 recipes, simple ideas to make."), color = MareePalette.Ink, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(tr("Choisis une catégorie ou cherche un ingrédient. Les quantités, étapes et temps de repos sont indiqués dans chaque recette.", "Choose a category or search for an ingredient. Each recipe lists quantities, steps and resting times."), color = MareePalette.Muted)
        OutlinedTextField(value = query, onValueChange = { query = it.take(100); offset = 0 },
            label = { Text(tr("Chercher une recette ou un ingrédient", "Search recipes or ingredients")) }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = category == null, onClick = { category = null; offset = 0 }, label = { Text(tr("Toutes", "All")) })
            RecipeCategory.entries.forEach { item -> FilterChip(selected = category == item, onClick = { category = item; offset = 0 }, label = { Text(item.label()) }) }
        }
        Text(tr("${matches.size} recette(s)", "${matches.size} recipe(s)"), color = MareePalette.Muted)
        if (matches.isEmpty()) Text(tr("Aucune recette trouvée. Essaie un autre ingrédient ou une autre catégorie.", "No recipes found. Try another ingredient or category."), color = MareePalette.Ink)
        matches.drop(currentPage * 6).take(6).forEach { recipe ->
            OutlinedButton(onClick = { selectedId = recipe.id; detailRequest++ }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(recipe.name(), fontWeight = if (selected?.id == recipe.id) FontWeight.Bold else FontWeight.Medium)
                    Text(tr("${recipe.minutes} min · ${recipe.servings} portions", "${recipe.minutes} min · ${recipe.servings} servings"), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (matches.size > 6) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { offset = currentPage - 1 }, enabled = currentPage > 0) { Text(tr("Précédent", "Previous")) }
            Text("${currentPage + 1} / ${lastPage + 1}", color = MareePalette.Ink)
            TextButton(onClick = { offset = currentPage + 1 }, enabled = currentPage < lastPage) { Text(tr("Suivant", "Next")) }
        }
    }
    selected?.let { recipe ->
        AppCardSurface(Modifier.bringIntoViewRequester(detail)) {
            SectionLabel(recipe.category.label())
            Text(recipe.name(), color = MareePalette.Ink, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(tr("${recipe.servings} portions · environ ${recipe.minutes} min, repos compris", "${recipe.servings} servings · about ${recipe.minutes} min, including resting time"), color = MareePalette.Muted)
            Text(tr("Ingrédients", "Ingredients"), color = MareePalette.Ink, fontWeight = FontWeight.Bold)
            recipe.ingredients().forEach { Text("• $it", color = MareePalette.Ink) }
            HorizontalDivider(color = MareePalette.Outline)
            Text(tr("Préparation", "Method"), color = MareePalette.Ink, fontWeight = FontWeight.Bold)
            recipe.steps().forEachIndexed { index, step ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("${index + 1}.", color = MareePalette.Teal, fontWeight = FontWeight.Bold)
                    Text(step, color = MareePalette.Ink, modifier = Modifier.weight(1f))
                }
            }
            Text(tr("Allergènes : ${recipe.allergens()}", "Allergens: ${recipe.allergens()}"), color = MareePalette.Muted, style = MaterialTheme.typography.bodySmall)
        }
    }
}
