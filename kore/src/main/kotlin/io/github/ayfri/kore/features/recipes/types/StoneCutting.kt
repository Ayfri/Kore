package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.item
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.Ingredient
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class StoneCutting(
	override var group: String? = null,
	var ingredient: InlinableList<Ingredient> = emptyList(),
	var result: ItemArgument,
	var count: Int,
) : Recipe() {
	override val type = RecipeTypes.STONECUTTING
}

fun Recipes.stoneCutting(name: String, block: StoneCutting.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, StoneCutting(result = item(""), count = 1).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

fun StoneCutting.ingredient(block: Ingredient.() -> Unit) = Ingredient().apply(block).also { ingredient += it }
fun StoneCutting.ingredient(item: ItemArgument? = null, tag: ItemTagArgument? = null) = Ingredient(item, tag).also { ingredient += it }
