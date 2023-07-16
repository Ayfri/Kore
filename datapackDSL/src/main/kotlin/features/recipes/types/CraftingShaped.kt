package features.recipes.types

import arguments.types.resources.ItemArgument
import arguments.types.resources.RecipeArgument
import arguments.types.resources.item
import arguments.types.resources.tagged.ItemTagArgument
import features.recipes.RecipeFile
import features.recipes.RecipeTypes
import features.recipes.Recipes
import features.recipes.data.CraftingResult
import features.recipes.data.Ingredient
import kotlinx.serialization.Serializable
import serializers.InlinableList

@Serializable
data class CraftingShaped(
	override var group: String? = null,
	val pattern: MutableList<String> = mutableListOf(),
	val key: MutableMap<String, InlinableList<Ingredient>> = mutableMapOf(),
	override var result: CraftingResult,
) : Recipe(), CraftingRecipe {
	override val type = RecipeTypes.CRAFTING_SHAPED
}

fun Recipes.craftingShaped(name: String, block: CraftingShaped.() -> Unit): RecipeArgument {
	dp.recipes.add(RecipeFile(name, CraftingShaped(result = CraftingResult(item = item(""))).apply(block)))
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
fun CraftingShaped.patternLine(line: String) = pattern.add(line)
fun CraftingShaped.pattern(vararg lines: String) = pattern.addAll(lines.toList())
