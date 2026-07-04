package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:use_remainder` item component, which replaces the item stack with the given item after it is fully consumed.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#use_remainder
 */
@Serializable
data class UseRemainderComponent(
	var item: ItemStack,
) : Component()

/** Replaces the item stack with the given item after it is fully consumed. */
fun ComponentsScope.useRemainder(item: ItemStack) = apply {
	this[ItemComponentTypes.USE_REMAINDER] = UseRemainderComponent(item)
}
