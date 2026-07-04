package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:glider` item component, which enables elytra-like gliding when equipped in the chest slot.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#glider
 */
@Serializable
data object GliderComponent : Component()

/** Enables elytra-like gliding when equipped in the chest slot. */
fun ComponentsScope.glider() = apply {
	this[ItemComponentTypes.GLIDER] = GliderComponent
}
