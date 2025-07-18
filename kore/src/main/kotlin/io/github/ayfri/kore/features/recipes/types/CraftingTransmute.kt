package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.ItemStackBuilder
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.Ingredient
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.Serializable

@Serializable(with = CraftingTransmuteResult.Companion.CraftingTransmuteResultSerializer::class)
data class CraftingTransmuteResult(
	var id: String,
	var count: Short? = null,
	var components: ComponentsPatch? = null,
) {
	fun toItemStack() = ItemStack(id, count, components?.toComponents())

	companion object {
		data object CraftingTransmuteResultSerializer : SinglePropertySimplifierSerializer<CraftingTransmuteResult, String>(
			kClass = CraftingTransmuteResult::class,
			property = CraftingTransmuteResult::id
		)
	}
}

@Serializable
data class CraftingTransmute(
	override var group: String? = null,
	var category: CraftingTransmuteCategory? = null,
	var input: Ingredient,
	var material: Ingredient,
	var result: CraftingTransmuteResult,
) : Recipe() {
	override val type = RecipeTypes.CRAFTING_TRANSMUTE
}

fun Recipes.craftingTransmute(name: String, block: CraftingTransmute.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingTransmute(
			input = Ingredient(),
			material = Ingredient(),
			result = CraftingTransmuteResult(id = ""),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

fun CraftingTransmute.input(block: Ingredient.() -> Unit) = Ingredient().apply(block).also { input = it }

fun CraftingTransmute.material(block: Ingredient.() -> Unit) = Ingredient().apply(block).also { material = it }

fun CraftingTransmute.result(block: ItemStackBuilder.() -> Unit) {
	result = itemStack(block = block).let {
		CraftingTransmuteResult(
			id = it.id,
			count = it.count,
			components = it.components?.toPatch()
		)
	}
}

fun CraftingTransmute.result(id: ItemArgument, count: Short? = null, block: (ComponentsPatch.() -> Unit)? = null) {
	result = CraftingTransmuteResult(
		id = id.asId(),
		count = count,
		components = block?.let { ComponentsPatch().apply(it) }
	)
}
