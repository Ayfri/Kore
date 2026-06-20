package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Hardcoded crafting table recipe using built-in game logic that cannot be expressed as data.
 *
 * Pass one of the predefined constants (e.g. [CraftingSpecialBannerDuplicate]) or supply a raw [name].
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special
 */
@Serializable
data class CraftingSpecial(
	var name: String,
) : Recipe() {
	@Transient
	@Deprecated("CraftingSpecial does not have a group", level = DeprecationLevel.HIDDEN)
	override var group: String? = null
		set(_) = Unit

	override val type = RecipeTypes.CRAFTING_SPECIAL(name)
}

/** Repairs items by combining two damaged items of the same type. */
val CraftingSpecialRepairItem = CraftingSpecial("repairitem")

/** Recolours a shulker box with a dye. */
val CraftingSpecialShulkerBoxColoring = CraftingSpecial("shulkerboxcoloring")

/**
 * Adds a `crafting_special_<craftingTypeName>` recipe to the data pack using a raw type name string.
 *
 * Prefer the typed overload with a predefined constant where possible.
 * Produces `data/<namespace>/recipe/<name>.json`.
 */
fun Recipes.craftingSpecial(name: String, craftingTypeName: String, block: CraftingSpecial.() -> Unit) =
	dp.recipes.add(RecipeFile(name, CraftingSpecial(craftingTypeName).apply(block)))

/**
 * Adds a `crafting_special_*` recipe to the data pack from a predefined [CraftingSpecial] constant.
 *
 * Produces `data/<namespace>/recipe/<name>.json`.
 */
fun Recipes.craftingSpecial(name: String, craftingSpecial: CraftingSpecial) =
	dp.recipes.add(RecipeFile(name, craftingSpecial))
