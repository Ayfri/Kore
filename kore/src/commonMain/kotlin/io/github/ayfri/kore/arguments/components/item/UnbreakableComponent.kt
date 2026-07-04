package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:unbreakable` item component, which prevents the item from taking durability damage.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#unbreakable
 */
@Serializable
data object UnbreakableComponent : Component()

/** Prevents the item from taking durability damage. */
fun ComponentsScope.unbreakable() = apply { this[ItemComponentTypes.UNBREAKABLE] = UnbreakableComponent }
