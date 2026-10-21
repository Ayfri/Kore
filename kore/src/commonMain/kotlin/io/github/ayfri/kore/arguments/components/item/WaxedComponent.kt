package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:waxed` item component, a marker that keeps the sign it is placed as waxed, so it can't be edited.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#waxed
 */
@Serializable
data object WaxedComponent : Component()

/** Marks the item as waxed. */
fun ComponentsScope.waxed() = apply { this[ItemComponentTypes.WAXED] = WaxedComponent }
