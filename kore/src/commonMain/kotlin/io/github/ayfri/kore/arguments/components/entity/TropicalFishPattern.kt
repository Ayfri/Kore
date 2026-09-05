package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.TropicalFishPatterns
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:tropical_fish/pattern` entity component, which sets the body pattern of a tropical fish.
 *
 * Exposed on tropical fish spawn eggs/buckets (and the entity itself) since snapshot 25w04a. Serializes as the pattern id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#tropical_fish/pattern
 */
@Serializable(with = TropicalFishPattern.Companion.TropicalFishPatternSerializer::class)
data class TropicalFishPattern(
	var pattern: TropicalFishPatterns
) : Component() {
	companion object {
		data object TropicalFishPatternSerializer : InlineAutoSerializer<TropicalFishPattern, TropicalFishPatterns>(
			serializer<TropicalFishPatterns>(),
			TropicalFishPattern::pattern,
			::TropicalFishPattern
		)
	}
}

/** Sets the body pattern of a tropical fish. */
fun ComponentsScope.tropicalFishPattern(pattern: TropicalFishPatterns) {
	this[EntityItemComponentTypes.TROPICAL_FISH_PATTERN] = TropicalFishPattern(pattern)
}
