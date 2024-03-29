package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable(with = CustomModelDataComponent.Companion.CustomModelDataComponentSerializer::class)
data class CustomModelDataComponent(var modelData: Int) : Component() {
	companion object {
		object CustomModelDataComponentSerializer : InlineSerializer<CustomModelDataComponent, Int>(
			Int.serializer(),
			CustomModelDataComponent::modelData
		)
	}
}

fun Components.customModelData(modelData: Int) = apply { this[ComponentTypes.CUSTOM_MODEL_DATA] = CustomModelDataComponent(modelData) }
