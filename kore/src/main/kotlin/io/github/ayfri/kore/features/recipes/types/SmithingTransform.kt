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
import kotlinx.serialization.Transient

@Serializable
data class SmithingTransform(
	var template: InlinableList<ItemOrTagArgument> = emptyList(),
	var base: InlinableList<ItemOrTagArgument> = emptyList(),
	var addition: InlinableList<ItemOrTagArgument> = emptyList(),
	override var result: CraftingResult,
) : Recipe(), ResultedRecipe {
	@Transient
	@Deprecated("SmithingTransform does not have a group", level = DeprecationLevel.HIDDEN)
	override var group: String? = null
		set(_) = Unit

	override val type = RecipeTypes.SMITHING_TRANSFORM
}

fun Recipes.smithingTransform(name: String, block: SmithingTransform.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, SmithingTransform(result = CraftingResult("")).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

fun SmithingTransform.template(vararg items: ItemArgument) = apply { template = items.toList() }
fun SmithingTransform.template(tag: ItemTagArgument) = apply { template = listOf(tag) }

fun SmithingTransform.base(vararg items: ItemOrTagArgument) = apply { base = items.toList() }
fun SmithingTransform.base(tag: ItemTagArgument) = apply { base = listOf(tag) }

fun SmithingTransform.addition(vararg items: ItemOrTagArgument) = apply { addition = items.toList() }
fun SmithingTransform.addition(tag: ItemTagArgument) = apply { addition = listOf(tag) }
