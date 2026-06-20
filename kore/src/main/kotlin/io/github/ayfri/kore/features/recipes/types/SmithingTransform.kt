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

/**
 * Smithing table recipe that upgrades a base item using a template and an addition material.
 *
 * Components from the base item are copied to the result (e.g. enchantments are preserved).
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#smithing_transform
 */
@Serializable
data class SmithingTransform(
	/** The smithing template item(s) required in the template slot. */
	var template: InlinableList<ItemOrTagArgument> = emptyList(),
	/** The item(s) to be upgraded. */
	var base: InlinableList<ItemOrTagArgument> = emptyList(),
	/** The upgrade material(s) consumed alongside the base item. */
	var addition: InlinableList<ItemOrTagArgument> = emptyList(),
	override var result: CraftingResult,
	var showNotification: Boolean? = null,
) : Recipe(), ResultedRecipe {
	@Transient
	@Deprecated("SmithingTransform does not have a group", level = DeprecationLevel.HIDDEN)
	override var group: String? = null
		set(_) = Unit

	override val type = RecipeTypes.SMITHING_TRANSFORM
}

/**
 * Adds a `smithing_transform` recipe to the data pack.
 *
 * Use [SmithingTransform.template], [SmithingTransform.base], and [SmithingTransform.addition] inside the block.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#smithing_transform
 */
fun Recipes.smithingTransform(name: String, block: SmithingTransform.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, SmithingTransform(result = CraftingResult("")).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the template slot to one or more specific items. */
fun SmithingTransform.template(vararg items: ItemArgument) = apply { template = items.toList() }

/** Sets the template slot to an item tag. */
fun SmithingTransform.template(tag: ItemTagArgument) = apply { template = listOf(tag) }

/** Sets the base (item to upgrade) to one or more specific items. */
fun SmithingTransform.base(vararg items: ItemOrTagArgument) = apply { base = items.toList() }

/** Sets the base (item to upgrade) to an item tag. */
fun SmithingTransform.base(tag: ItemTagArgument) = apply { base = listOf(tag) }

/** Sets the addition (upgrade material) to one or more specific items. */
fun SmithingTransform.addition(vararg items: ItemOrTagArgument) = apply { addition = items.toList() }

/** Sets the addition (upgrade material) to an item tag. */
fun SmithingTransform.addition(tag: ItemTagArgument) = apply { addition = listOf(tag) }
