package io.github.ayfri.kore.features.recipes.data

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.Serializable

@Serializable(with = CraftingResult.Companion.CraftingTransmuteResultSerializer::class)
data class CraftingResult(
	var id: String,
	var count: Short? = null,
	var components: ComponentsPatch? = null,
) {
	fun toItemStack() = ItemStack(id, count, components?.toComponents())

	companion object {
		data object CraftingTransmuteResultSerializer : SinglePropertySimplifierSerializer<CraftingResult, String>(
			kClass = CraftingResult::class,
			property = CraftingResult::id
		)
	}
}

fun CraftingResult.item(item: ItemArgument) = apply {
	id = item.asId()
	components = item.components
}
fun CraftingResult.components(block: ComponentsPatch.() -> Unit) = apply { components = ComponentsPatch().apply(block)}
