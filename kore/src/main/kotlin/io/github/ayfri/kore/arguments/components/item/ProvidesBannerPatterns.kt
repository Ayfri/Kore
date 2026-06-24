package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.tagged.BannerPatternTagArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:provides_banner_patterns` item component, which registers this item as a banner pattern source for the loom.
 *
 * Serializes as the banner pattern tag directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#provides_banner_patterns
 */
@Serializable(with = ProvidesBannerPatterns.Companion.ProvidesBannerPatternsSerializer::class)
data class ProvidesBannerPatterns(var patterns: BannerPatternTagArgument) : Component() {
	companion object {
		data object ProvidesBannerPatternsSerializer : InlineAutoSerializer<ProvidesBannerPatterns>(ProvidesBannerPatterns::class)
	}
}

/** Registers this item as a banner pattern source for the loom. */
fun ComponentsScope.providesBannerPatterns(patterns: BannerPatternTagArgument) = apply {
	this[ItemComponentTypes.PROVIDES_BANNER_PATTERNS] = ProvidesBannerPatterns(patterns)
}
