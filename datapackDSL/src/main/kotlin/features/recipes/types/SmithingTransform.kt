package features.recipes.types

import arguments.types.resources.ItemArgument
import arguments.types.resources.RecipeArgument
import arguments.types.resources.item
import arguments.types.resources.tagged.ItemTagArgument
import features.recipes.RecipeFile
import features.recipes.RecipeTypes
import features.recipes.Recipes
import features.recipes.data.Ingredient
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SmithingTransform(
	var template: Ingredient,
	var base: Ingredient,
	var addition: Ingredient,
	var result: ItemArgument,
) : Recipe() {
	@Transient
	@Deprecated("SmithingTransform does not have a group", level = DeprecationLevel.HIDDEN)
	override var group: String? = null
		set(_) = Unit

	override val type = RecipeTypes.SMITHING_TRANSFORM
}

fun Recipes.smithingTransform(name: String, block: SmithingTransform.() -> Unit): RecipeArgument {
	dp.recipes.add(
		RecipeFile(
			name,
			SmithingTransform(template = Ingredient(), base = Ingredient(), addition = Ingredient(), result = item("")).apply(block)
		)
	)
	return RecipeArgument(name, dp.name)
}

fun SmithingTransform.template(block: Ingredient.() -> Unit) {
	template = Ingredient().apply(block)
}

fun SmithingTransform.base(block: Ingredient.() -> Unit) {
	base = Ingredient().apply(block)
}

fun SmithingTransform.base(item: ItemArgument? = null, tag: ItemTagArgument? = null) {
	base = Ingredient(item, tag)
}

fun SmithingTransform.addition(block: Ingredient.() -> Unit) {
	addition = Ingredient().apply(block)
}

fun SmithingTransform.addition(item: ItemArgument? = null, tag: ItemTagArgument? = null) {
	addition = Ingredient(item, tag)
}
