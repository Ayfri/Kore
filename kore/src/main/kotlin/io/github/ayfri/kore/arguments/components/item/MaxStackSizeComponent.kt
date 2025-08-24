package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MaxStackSizeComponent.Companion.MaxStackSizeComponentSerializer::class)
data class MaxStackSizeComponent(var maxStackSize: Int) : Component() {
	companion object {
		data object MaxStackSizeComponentSerializer : InlineAutoSerializer<MaxStackSizeComponent>(MaxStackSizeComponent::class)
	}
}

fun ComponentsScope.maxStackSize(maxStackSize: Int) = apply {
	this[ItemComponentTypes.MAX_STACK_SIZE] = MaxStackSizeComponent(maxStackSize)
}
