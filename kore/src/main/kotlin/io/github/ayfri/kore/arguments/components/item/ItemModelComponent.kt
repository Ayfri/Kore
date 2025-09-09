package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ItemModelComponent.Companion.ItemModelComponentSerializer::class)
data class ItemModelComponent(
	@Serializable(with = ResourceLocationArgument.Companion.ResourceLocationArgumentSimpleSerializer::class)
	var model: ModelArgument
) : Component() {
	companion object {
		data object ItemModelComponentSerializer : InlineAutoSerializer<ItemModelComponent>(ItemModelComponent::class)
	}
}

fun ComponentsScope.itemModel(model: ModelArgument) = apply {
	this[ItemComponentTypes.ITEM_MODEL] = ItemModelComponent(model)
}

fun ComponentsScope.itemModel(model: String, namespace: String = "minecraft") = itemModel(ModelArgument(model, namespace))
fun ComponentsScope.itemModel(model: ItemArgument) = itemModel(ModelArgument(model.asId().substringAfter(":"), model.namespace))
