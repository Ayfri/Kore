package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:sulfur_cube_content` item component, which stores the item held inside a sulfur cube.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#sulfur_cube_content
 */
@Serializable
data class SulfurCubeContentComponent(
	var value: ItemStack,
) : Component()

/** Stores the item held inside a sulfur cube. */
fun ComponentsScope.sulfurCubeContent(value: ItemStack) = apply {
	this[ItemComponentTypes.SULFUR_CUBE_CONTENT] = SulfurCubeContentComponent(value)
}
