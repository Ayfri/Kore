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

/**
 * Smithing table recipe that applies a cosmetic trim pattern to armour.
 *
 * Does not change the item's function or components; only the visual trim is applied.
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#smithing_trim
 */
@Serializable
data class SmithingTrim(
	/** The smithing template item(s) required in the template slot. */
	var template: InlinableList<ItemOrTagArgument> = emptyList(),
	/** The armour piece(s) to be trimmed. */
	var base: InlinableList<ItemOrTagArgument> = emptyList(),
	/** The trim material(s) that determine the colour of the trim. */
	var addition: InlinableList<ItemOrTagArgument> = emptyList(),
	/** The trim pattern applied to the armour. */
	var pattern: TrimPatternArgument,
	var showNotification: Boolean? = null,
) : Recipe() {
	@Transient
	@Deprecated("SmithingTrim does not have a group", level = DeprecationLevel.HIDDEN)
	override var group: String? = null
		set(_) = Unit

	override val type = RecipeTypes.SMITHING_TRANSFORM
}

/**
 * Adds a `smithing_trim` recipe to the data pack.
 *
 * Use [SmithingTrim.template], [SmithingTrim.base], [SmithingTrim.addition], and [SmithingTrim.pattern] inside the block.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#smithing_trim
 */
fun Recipes.smithingTrim(name: String, block: SmithingTrim.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, SmithingTrim(
		pattern = TrimPatternArgument("")
	).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the template slot to one or more specific items. */
fun SmithingTrim.template(vararg items: ItemOrTagArgument) = apply { template = items.toList() }

/** Sets the template slot to an item tag. */
fun SmithingTrim.template(tag: ItemOrTagArgument) = apply { template = listOf(tag) }

/** Sets the base (armour to trim) to one or more specific items. */
fun SmithingTrim.base(vararg items: ItemOrTagArgument) = apply { base = items.toList() }

/** Sets the base (armour to trim) to an item tag. */
fun SmithingTrim.base(tag: ItemOrTagArgument) = apply { base = listOf(tag) }

/** Sets the addition (trim material) to one or more specific items. */
fun SmithingTrim.addition(vararg items: ItemOrTagArgument) = apply { addition = items.toList() }

/** Sets the addition (trim material) to an item tag. */
fun SmithingTrim.addition(tag: ItemOrTagArgument) = apply { addition = listOf(tag) }
