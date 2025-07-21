package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable(with = MaxStackSizeComponent.Companion.MaxStackSizeComponentSerializer::class)
data class MaxStackSizeComponent(var maxStackSize: Int) : Component() {
	companion object {
		object MaxStackSizeComponentSerializer : InlineSerializer<MaxStackSizeComponent, Int>(
			Int.serializer(),
			MaxStackSizeComponent::maxStackSize
		)
	}
}

fun ComponentsScope.maxStackSize(maxStackSize: Int) = apply { this[ItemComponentTypes.MAX_STACK_SIZE] = MaxStackSizeComponent(maxStackSize) }
