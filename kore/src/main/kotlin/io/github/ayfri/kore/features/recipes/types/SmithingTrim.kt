package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.generated.arguments.types.TrimPatternArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SmithingTrim(
	var template: InlinableList<ItemOrTagArgument> = emptyList(),
	var base: InlinableList<ItemOrTagArgument> = emptyList(),
	var addition: InlinableList<ItemOrTagArgument> = emptyList(),
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
		pattern = TrimPatternArgument("")
	).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

fun SmithingTrim.template(vararg items: ItemOrTagArgument) = apply { template = items.toList() }
fun SmithingTrim.template(tag: ItemOrTagArgument) = apply { template = listOf(tag) }

fun SmithingTrim.base(vararg items: ItemOrTagArgument) = apply { base = items.toList() }
fun SmithingTrim.base(tag: ItemOrTagArgument) = apply { base = listOf(tag) }

fun SmithingTrim.addition(vararg items: ItemOrTagArgument) = apply { addition = items.toList() }
fun SmithingTrim.addition(tag: ItemOrTagArgument) = apply { addition = listOf(tag) }
