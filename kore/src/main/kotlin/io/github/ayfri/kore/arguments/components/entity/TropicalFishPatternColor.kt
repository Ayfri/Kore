package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:tropical_fish/pattern_color` entity component, which sets the pattern color of a tropical fish.
 *
 * Exposed on tropical fish spawn eggs/buckets (and the entity itself) since snapshot 25w04a. Serializes as the dye color string directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#tropical_fish/pattern_color
 */
@Serializable(with = TropicalFishPatternColor.Companion.TropicalFishPatternColorSerializer::class)
data class TropicalFishPatternColor(
	var color: DyeColors
) : Component() {
	companion object {
		data object TropicalFishPatternColorSerializer : InlineAutoSerializer<TropicalFishPatternColor, DyeColors>(
			serializer<DyeColors>(),
			TropicalFishPatternColor::color,
			::TropicalFishPatternColor
		)
	}
}

/** Sets the pattern color of a tropical fish. */
fun ComponentsScope.tropicalFishPatternColor(color: DyeColors) {
	this[EntityItemComponentTypes.TROPICAL_FISH_PATTERN_COLOR] = TropicalFishPatternColor(color)
}
