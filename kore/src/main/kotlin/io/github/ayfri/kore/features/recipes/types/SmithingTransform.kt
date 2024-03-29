package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import io.github.ayfri.kore.arguments.types.resources.item
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.features.recipes.data.Ingredient
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SmithingTransform(
	var template: Ingredient,
	var base: Ingredient,
	var addition: Ingredient,
	var result: CraftingResult,
) : Recipe() {
	@Transient
	@Deprecated("SmithingTransform does not have a group", level = DeprecationLevel.HIDDEN)
	override var group: String? = null
		set(_) = Unit

	override val type = RecipeTypes.SMITHING_TRANSFORM
}

fun Recipes.smithingTransform(name: String, block: SmithingTransform.() -> Unit): RecipeArgument {
	dp.recipes += RecipeFile(
		name,
		SmithingTransform(
			template = Ingredient(),
			base = Ingredient(),
			addition = Ingredient(),
			result = CraftingResult(id = item("")),
		).apply(block)
	)
	return RecipeArgument(name, dp.name)
}

fun SmithingTransform.template(block: Ingredient.() -> Unit) {
	template = Ingredient().apply(block)
}

fun SmithingTransform.template(item: ItemArgument? = null, tag: ItemTagArgument? = null) {
	template = Ingredient(item, tag)
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

fun SmithingTransform.result(block: CraftingResult.() -> Unit) {
	result = CraftingResult(id = item("")).apply(block)
}

fun SmithingTransform.result(item: ItemArgument, count: Int? = null, block: (Components.() -> Unit)? = null) {
	result = CraftingResult(item, count, block?.let { Components().apply(block) })
}
