package features.recipes.types

import arguments.Argument
import arguments.item
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
	var result: Argument.Item,
) : Recipe() {
	@Transient
	@Deprecated("SmithingTransform does not have a group", level = DeprecationLevel.HIDDEN)
	override var group: String? = null
		set(_) = Unit

	override val type = RecipeTypes.SMITHING_TRANSFORM
}

fun Recipes.smithingTransform(name: String, block: SmithingTransform.() -> Unit): Argument.Recipe {
	dp.recipes.add(
		RecipeFile(
			name,
			SmithingTransform(template = Ingredient(), base = Ingredient(), addition = Ingredient(), result = item("")).apply(block)
		)
	)
	return Argument.Recipe(name, dp.name)
}

fun SmithingTransform.template(block: Ingredient.() -> Unit) {
	template = Ingredient().apply(block)
}

fun SmithingTransform.base(block: Ingredient.() -> Unit) {
	base = Ingredient().apply(block)
}

fun SmithingTransform.base(item: Argument.Item? = null, tag: Argument.ItemTag? = null) {
	base = Ingredient(item, tag)
}

fun SmithingTransform.addition(block: Ingredient.() -> Unit) {
	addition = Ingredient().apply(block)
}

fun SmithingTransform.addition(item: Argument.Item? = null, tag: Argument.ItemTag? = null) {
	addition = Ingredient(item, tag)
}
