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

@Serializable
data class CraftingTransmute(
	override var group: String? = null,
	var category: CraftingTransmuteCategory? = null,
	var input: InlinableList<ItemOrTagArgument>,
	var material: InlinableList<ItemOrTagArgument>,
	override var result: CraftingResult,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_TRANSMUTE
}

fun Recipes.craftingTransmute(name: String, block: CraftingTransmute.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingTransmute(
			input = listOf(),
			material = listOf(),
			result = CraftingResult(id = ""),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

fun CraftingTransmute.input(vararg items: ItemArgument) = apply { input = items.toList() }
fun CraftingTransmute.input(tag: ItemTagArgument) = apply { input = listOf(tag) }

fun CraftingTransmute.material(vararg items: ItemArgument) = apply { material = items.toList() }
fun CraftingTransmute.material(tag: ItemTagArgument) = apply { material = listOf(tag) }
