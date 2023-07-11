package features.recipes.types

import arguments.Argument
import features.recipes.RecipeFile
import features.recipes.RecipeTypes
import features.recipes.Recipes
import features.recipes.data.Ingredient
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SmithingTrim(
	var template: Ingredient,
	var base: Ingredient,
	var addition: Ingredient,
) : Recipe() {
	@Transient
	@Deprecated("SmithingTrim does not have a group", level = DeprecationLevel.HIDDEN)
	override var group: String? = null
		set(_) = Unit

	override val type = RecipeTypes.SMITHING_TRANSFORM
}

fun Recipes.smithingTrim(name: String, block: SmithingTrim.() -> Unit): Argument.Recipe {
	dp.recipes.add(
		RecipeFile(
			name,
			SmithingTrim(template = Ingredient(), base = Ingredient(), addition = Ingredient()).apply(block)
		)
	)
	return Argument.Recipe(name, dp.name)
}

fun SmithingTrim.template(block: Ingredient.() -> Unit) {
	template = Ingredient().apply(block)
}

fun SmithingTrim.base(block: Ingredient.() -> Unit) {
	base = Ingredient().apply(block)
}

fun SmithingTrim.base(item: Argument.Item? = null, tag: Argument.ItemTag? = null) {
	base = Ingredient(item, tag)
}
