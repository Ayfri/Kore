package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class CraftingShaped(
	override var group: String? = null,
	var pattern: List<String> = emptyList(),
	var key: MutableMap<String, InlinableList<ItemOrTagArgument>> = mutableMapOf(),
	override var result: CraftingResult,
) : Recipe(), CraftingRecipe {
	override val type = RecipeTypes.CRAFTING_SHAPED
}

fun Recipes.craftingShaped(name: String, block: CraftingShaped.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, CraftingShaped(result = CraftingResult("")).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

fun CraftingShaped.key(key: String, ingredients: List<ItemArgument>) = this.key.put(key, ingredients.toList())
fun CraftingShaped.key(key: String, vararg ingredients: ItemArgument) = this.key.put(key, ingredients.toList())
fun CraftingShaped.key(key: String, tag: ItemTagArgument) =
	this.key.put(key, listOf(tag))

data class Keys(val key: MutableMap<String, List<ItemOrTagArgument>>) {
	infix fun String.to(item: ItemArgument) = key.put(this, mutableListOf(item))
	infix fun String.to(tag: ItemTagArgument) = key.put(this, mutableListOf(tag))
	infix fun String.to(ingredients: List<ItemOrTagArgument>) = key.put(this, ingredients.toMutableList())
}

fun CraftingShaped.keys(block: Keys.() -> Unit) = Keys(key).apply(block)
fun CraftingShaped.patternLine(line: String) {
	pattern += line
}

fun CraftingShaped.pattern(vararg lines: String) {
	pattern += lines
}
