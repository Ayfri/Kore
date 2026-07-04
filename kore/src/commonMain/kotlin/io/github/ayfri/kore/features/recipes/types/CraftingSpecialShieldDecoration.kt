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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Recipe for applying a banner pattern to a shield.
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_shielddecoration
 */
@Serializable
@SerialName("crafting_special_shielddecoration")
data class CraftingSpecialShieldDecoration(
	/** The banner ingredient to copy the pattern from. */
	var banner: InlinableList<ItemOrTagArgument>,
	override var result: CraftingResult,
	/** The shield to decorate. */
	var target: InlinableList<ItemOrTagArgument>,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_SPECIAL("shielddecoration")
}

/**
 * Adds a `crafting_special_shielddecoration` recipe to the data pack.
 *
 * Use [CraftingSpecialShieldDecoration.banner] and [CraftingSpecialShieldDecoration.target] inside the block.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_shielddecoration
 */
fun Recipes.craftingSpecialShieldDecoration(
	name: String,
	block: CraftingSpecialShieldDecoration.() -> Unit
): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingSpecialShieldDecoration(
			banner = listOf(),
			result = CraftingResult(id = ""),
			target = listOf(),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the banner ingredient to one or more specific items. */
fun CraftingSpecialShieldDecoration.banner(vararg items: ItemArgument) = apply { banner = items.toList() }

/** Sets the banner ingredient to an item tag. */
fun CraftingSpecialShieldDecoration.banner(tag: ItemTagArgument) = apply { banner = listOf(tag) }

/** Sets the shield target to one or more specific items. */
fun CraftingSpecialShieldDecoration.target(vararg items: ItemArgument) = apply { target = items.toList() }

/** Sets the shield target to an item tag. */
fun CraftingSpecialShieldDecoration.target(tag: ItemTagArgument) = apply { target = listOf(tag) }
