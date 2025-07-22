package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TropicalFishPatternColor.Companion.TropicalFishPatternColorSerializer::class)
data class TropicalFishPatternColor(
	var color: DyeColors
) : Component() {
	companion object {
		data object TropicalFishPatternColorSerializer : InlineAutoSerializer<TropicalFishPatternColor>(TropicalFishPatternColor::class)
	}
}

fun ComponentsScope.tropicalFishPatternColor(color: DyeColors) {
	this[EntityItemComponentTypes.TROPICAL_FISH_PATTERN_COLOR] = TropicalFishPatternColor(color)
}
