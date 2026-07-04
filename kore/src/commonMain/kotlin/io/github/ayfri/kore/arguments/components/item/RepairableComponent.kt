package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:repairable` item component, which defines which items can repair this item in an anvil.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#repairable
 */
@Serializable
data class RepairableComponent(
	var items: InlinableList<ItemOrTagArgument> = listOf(),
) : Component()

/** Defines which items can repair this item in an anvil. */
fun ComponentsScope.repairable(vararg items: ItemOrTagArgument) = apply {
	this[ItemComponentTypes.REPAIRABLE] = RepairableComponent(items.toList())
}
