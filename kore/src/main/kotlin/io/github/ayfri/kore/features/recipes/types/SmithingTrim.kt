package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.item
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.generated.arguments.types.TrimPatternArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SmithingTrim(
	var template: ItemArgument,
	var base: ItemArgument,
	var addition: ItemArgument,
	var pattern: TrimPatternArgument,
) : Recipe() {
	@Transient
	@Deprecated("SmithingTrim does not have a group", level = DeprecationLevel.HIDDEN)
	override var group: String? = null
		set(_) = Unit

	override val type = RecipeTypes.SMITHING_TRANSFORM
}

fun Recipes.smithingTrim(name: String, block: SmithingTrim.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, SmithingTrim(
		template = item(""),
		base = item(""),
		addition = item(""),
		pattern = TrimPatternArgument("")
	).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}
