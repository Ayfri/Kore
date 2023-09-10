package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.Ingredient
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
