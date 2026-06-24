package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:tropical_fish/base_color` entity component, which sets the base body color of a tropical fish.
 *
 * Exposed on tropical fish spawn eggs/buckets (and the entity itself) since snapshot 25w04a. Serializes as the dye color string directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#tropical_fish/base_color
 */
@Serializable(with = TropicalFishBaseColor.Companion.TropicalFishBaseColorSerializer::class)
data class TropicalFishBaseColor(
	var color: DyeColors
) : Component() {
	companion object {
		data object TropicalFishBaseColorSerializer : InlineAutoSerializer<TropicalFishBaseColor>(TropicalFishBaseColor::class)
	}
}

/** Sets the base body color of a tropical fish. */
fun ComponentsScope.tropicalFishBaseColor(color: DyeColors) {
	this[EntityItemComponentTypes.TROPICAL_FISH_BASE_COLOR] = TropicalFishBaseColor(color)
}
