package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.TropicalFishPatterns
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TropicalFishPattern.Companion.TropicalFishPatternSerializer::class)
data class TropicalFishPattern(
	var pattern: TropicalFishPatterns
) : Component() {
	companion object {
		data object TropicalFishPatternSerializer : InlineAutoSerializer<TropicalFishPattern>(TropicalFishPattern::class)
	}
}

fun ComponentsScope.tropicalFishPattern(pattern: TropicalFishPatterns) {
	this[EntityItemComponentTypes.TROPICAL_FISH_PATTERN] = TropicalFishPattern(pattern)
}
