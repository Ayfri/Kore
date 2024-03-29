package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import io.github.ayfri.kore.arguments.types.resources.item
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.features.recipes.data.Ingredient
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class CraftingShaped(
	override var group: String? = null,
	var pattern: List<String> = emptyList(),
	var key: MutableMap<String, InlinableList<Ingredient>> = mutableMapOf(),
	override var result: CraftingResult,
) : Recipe(), CraftingRecipe {
	override val type = RecipeTypes.CRAFTING_SHAPED
}

fun Recipes.craftingShaped(name: String, block: CraftingShaped.() -> Unit): RecipeArgument {
	dp.recipes += RecipeFile(name, CraftingShaped(result = CraftingResult(id = item(""))).apply(block))
	return RecipeArgument(name, dp.name)
}

fun CraftingShaped.key(key: String, ingredients: List<Ingredient>) = this.key.put(key, ingredients.toMutableList())
fun CraftingShaped.key(key: String, block: Ingredient.() -> Unit) = this.key.put(key, mutableListOf(Ingredient().apply(block)))
fun CraftingShaped.key(key: String, item: ItemArgument? = null, tag: ItemTagArgument? = null) =
	this.key.put(key, mutableListOf(Ingredient(item, tag)))

class Keys(val key: MutableMap<String, List<Ingredient>>) {
	infix fun String.to(item: ItemArgument) = key.put(this, mutableListOf(Ingredient(item)))
	infix fun String.to(tag: ItemTagArgument) = key.put(this, mutableListOf(Ingredient(tag = tag)))
	infix fun String.to(ingredients: List<Ingredient>) = key.put(this, ingredients.toMutableList())
}

fun CraftingShaped.keys(block: Keys.() -> Unit) = Keys(key).apply(block)
fun CraftingShaped.patternLine(line: String) {
	pattern += line
}

fun CraftingShaped.pattern(vararg lines: String) {
	pattern += lines
}
