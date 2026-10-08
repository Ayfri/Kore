package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:pot_decorations` item component, the item stack shown on each face of a decorated pot.
 *
 * A face without an item shows bricks, an item's `provides_pottery_pattern` component picks the pattern of its face.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#pot_decorations
 */
@Serializable
data class PotDecorationsComponent(
	var back: ItemStack? = null,
	var front: ItemStack? = null,
	var left: ItemStack? = null,
	var right: ItemStack? = null,
) : Component()

/** Defines the item shown on each face of a decorated pot, bricks for a missing face, in the game's former list order. */
fun ComponentsScope.potDecorations(
	back: ItemArgument? = null,
	left: ItemArgument? = null,
	right: ItemArgument? = null,
	front: ItemArgument? = null,
) = apply {
	this[ItemComponentTypes.POT_DECORATIONS] = PotDecorationsComponent(
		back?.let(::ItemStack),
		front?.let(::ItemStack),
		left?.let(::ItemStack),
		right?.let(::ItemStack),
	)
}

/** Defines the item stack shown on each face of a decorated pot, configured in [block]. */
fun ComponentsScope.potDecorations(block: PotDecorationsComponent.() -> Unit) = apply {
	this[ItemComponentTypes.POT_DECORATIONS] = PotDecorationsComponent().apply(block)
}
