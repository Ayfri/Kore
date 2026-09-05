package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:item_model` item component, which overrides the item's model with a custom model resource location.
 *
 * Serializes as the model resource location directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#item_model
 */
@Serializable(with = ItemModelComponent.Companion.ItemModelComponentSerializer::class)
data class ItemModelComponent(
	@Serializable(with = ResourceLocationArgument.Companion.ResourceLocationArgumentSimpleSerializer::class)
	var model: ModelArgument
) : Component() {
	companion object {
		@Suppress("UNCHECKED_CAST")
		data object ItemModelComponentSerializer : InlineAutoSerializer<ItemModelComponent, ModelArgument>(
			ResourceLocationArgument.Companion.ResourceLocationArgumentSimpleSerializer as KSerializer<ModelArgument>,
			ItemModelComponent::model,
			::ItemModelComponent,
		)
	}
}

/** Overrides the item's model with a custom model resource location. */
fun ComponentsScope.itemModel(model: ModelArgument) = apply {
	this[ItemComponentTypes.ITEM_MODEL] = ItemModelComponent(model)
}

fun ComponentsScope.itemModel(model: String, namespace: String = "minecraft") = itemModel(ModelArgument(model, namespace))
fun ComponentsScope.itemModel(model: ItemArgument) = itemModel(ModelArgument(model.asId().substringAfter(":"), model.namespace))
