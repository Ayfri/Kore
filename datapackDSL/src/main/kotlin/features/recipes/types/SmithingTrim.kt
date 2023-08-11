package features.recipes.types

import arguments.types.resources.ItemArgument
import arguments.types.resources.RecipeArgument
import arguments.types.resources.tagged.ItemTagArgument
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

fun Recipes.smithingTrim(name: String, block: SmithingTrim.() -> Unit): RecipeArgument {
	dp.recipes += RecipeFile(
		name,
		SmithingTrim(template = Ingredient(), base = Ingredient(), addition = Ingredient()).apply(block)
	)
	return RecipeArgument(name, dp.name)
}

fun SmithingTrim.template(block: Ingredient.() -> Unit) {
	template = Ingredient().apply(block)
}

fun SmithingTrim.base(block: Ingredient.() -> Unit) {
	base = Ingredient().apply(block)
}

fun SmithingTrim.base(item: ItemArgument? = null, tag: ItemTagArgument? = null) {
	base = Ingredient(item, tag)
}
