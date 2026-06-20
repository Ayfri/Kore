package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:dye` item component, which sets the dye color of an item.
 *
 * Serializes as the dye color string directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#dye
 */
@Serializable(with = DyeComponent.Companion.DyeComponentSerializer::class)
data class DyeComponent(var value: DyeColors) : Component() {
	companion object {
		data object DyeComponentSerializer : InlineAutoSerializer<DyeComponent>(DyeComponent::class)
	}
}

/** Sets the dye color of an item using a `DyeColors` value. */
fun ComponentsScope.dye(color: DyeColors) = apply { this[ItemComponentTypes.DYE] = DyeComponent(color) }
