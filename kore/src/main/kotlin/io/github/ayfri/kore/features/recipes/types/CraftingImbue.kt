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

/**
 * Recipe that imbues items with properties from a source item (e.g. tipping arrows with lingering potions).
 *
 * The items in `material` are imbued with the effect of `source` and produce `result`.
 * Replaces the removed `minecraft:crafting_special_tippedarrow` special recipe.
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_imbue
 */
@Serializable
data class CraftingImbue(
	/** Optional recipe book category. */
	var category: RecipeCategory? = null,
	/** Optional recipe book group name. */
	override var group: String? = null,
	/** The item(s) to be imbued (e.g. arrows). */
	var material: InlinableList<ItemOrTagArgument>,
	/** The output item. */
	override var result: CraftingResult,
	/** Whether to show the recipe unlock notification. */
	var showNotification: Boolean? = null,
	/** The source item providing the effect (e.g. a lingering potion). */
	var source: InlinableList<ItemOrTagArgument>,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_IMBUE
}

/**
 * Adds a `crafting_imbue` recipe to the data pack.
 *
 * Use [CraftingImbue.material] and [CraftingImbue.source] inside the block to set the ingredients.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_imbue
 */
fun Recipes.craftingImbue(name: String, block: CraftingImbue.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingImbue(
			material = listOf(),
			result = CraftingResult(id = ""),
			source = listOf(),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the material (items to be imbued) to one or more specific items. */
fun CraftingImbue.material(vararg items: ItemArgument) = apply { material = items.toList() }

/** Sets the material (items to be imbued) to an item tag. */
fun CraftingImbue.material(tag: ItemTagArgument) = apply { material = listOf(tag) }

/** Sets the source (effect provider) to one or more specific items. */
fun CraftingImbue.source(vararg items: ItemArgument) = apply { source = items.toList() }

/** Sets the source (effect provider) to an item tag. */
fun CraftingImbue.source(tag: ItemTagArgument) = apply { source = listOf(tag) }
