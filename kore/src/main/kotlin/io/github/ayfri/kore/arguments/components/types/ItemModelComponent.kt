package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ItemModelComponent.Companion.ItemModelComponentSerializer::class)
data class ItemModelComponent(var model: ModelArgument) : Component() {
	companion object {
		object ItemModelComponentSerializer : InlineSerializer<ItemModelComponent, ModelArgument>(
			ModelArgument.serializer(),
			ItemModelComponent::model
		)
	}
}

fun ComponentsScope.itemModel(model: ModelArgument) = apply {
	this[ComponentTypes.ITEM_MODEL] = ItemModelComponent(model)
}

fun ComponentsScope.itemModel(model: String, namespace: String = "minecraft") = itemModel(ModelArgument(model, namespace))
fun ComponentsScope.itemModel(model: ItemArgument) = itemModel(ModelArgument(model.asId().substringAfter(":"), model.namespace))
