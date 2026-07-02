package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:max_stack_size` item component, which overrides how many items can stack in a single inventory slot (1-99).
 *
 * Serializes as the integer value directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#max_stack_size
 */
@Serializable(with = MaxStackSizeComponent.Companion.MaxStackSizeComponentSerializer::class)
data class MaxStackSizeComponent(var maxStackSize: Int) : Component() {
	companion object {
		data object MaxStackSizeComponentSerializer : InlineAutoSerializer<MaxStackSizeComponent, Int>(
			serializer<Int>(),
			MaxStackSizeComponent::maxStackSize,
			::MaxStackSizeComponent
		)
	}
}

/** Overrides how many items can stack in a single inventory slot (1-99). */
fun ComponentsScope.maxStackSize(maxStackSize: Int) = apply {
	this[ItemComponentTypes.MAX_STACK_SIZE] = MaxStackSizeComponent(maxStackSize)
}
