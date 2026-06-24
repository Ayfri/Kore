package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:shulker/color` entity component, which sets the color of a shulker.
 *
 * Exposed on shulker spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the dye color string directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#shulker/color
 */
@Serializable(with = ShulkerColor.Companion.ShulkerColorSerializer::class)
data class ShulkerColor(
	var color: DyeColors
) : Component() {
	companion object {
		data object ShulkerColorSerializer : InlineAutoSerializer<ShulkerColor>(ShulkerColor::class)
	}
}

/** Sets the color of a shulker. */
fun ComponentsScope.shulkerColor(color: DyeColors) {
	this[EntityItemComponentTypes.SHULKER_COLOR] = ShulkerColor(color)
}
