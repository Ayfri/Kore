package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.DecoratedPotPatternArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:provides_pottery_pattern` item component, the pattern this item shows on the face of a decorated pot.
 *
 * Serializes as the decorated pot pattern id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#provides_pottery_pattern
 */
@Serializable(with = ProvidesPotteryPattern.Companion.ProvidesPotteryPatternSerializer::class)
data class ProvidesPotteryPattern(var pattern: DecoratedPotPatternArgument) : Component() {
	companion object {
		data object ProvidesPotteryPatternSerializer : InlineAutoSerializer<ProvidesPotteryPattern, DecoratedPotPatternArgument>(
			serializer<DecoratedPotPatternArgument>(),
			ProvidesPotteryPattern::pattern,
			::ProvidesPotteryPattern
		)
	}
}

/** Shows [pattern] on the face of a decorated pot crafted with this item. */
fun ComponentsScope.providesPotteryPattern(pattern: DecoratedPotPatternArgument) = apply {
	this[ItemComponentTypes.PROVIDES_POTTERY_PATTERN] = ProvidesPotteryPattern(pattern)
}
