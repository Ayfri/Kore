package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.BannerPatternOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:provides_banner_patterns` item component, which allows the item to be placed in the pattern slot of a loom
 * and provide the specified banner pattern(s).
 *
 * Serializes as the banner pattern ID/tag (or list) directly (inlined), not as an object.
 *
 * @property patterns Banner pattern(s) this item provides. Accepts a tag, a single ID, or a list of IDs/tags.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#provides_banner_patterns
 */
@Serializable(with = ProvidesBannerPatterns.Companion.ProvidesBannerPatternsSerializer::class)
data class ProvidesBannerPatterns(var patterns: InlinableList<BannerPatternOrTagArgument>) : Component() {
	companion object {
		data object ProvidesBannerPatternsSerializer : InlineAutoSerializer<ProvidesBannerPatterns>(ProvidesBannerPatterns::class)
	}
}

/**
 * Adds the `minecraft:provides_banner_patterns` component, allowing this item to provide the given banner [patterns] in a loom.
 *
 * @param patterns Banner pattern(s) the item provides.
 */
fun ComponentsScope.providesBannerPatterns(patterns: InlinableList<BannerPatternOrTagArgument>) = apply {
	this[ItemComponentTypes.PROVIDES_BANNER_PATTERNS] = ProvidesBannerPatterns(patterns)
}

/** Adds the `minecraft:provides_banner_patterns` component using vararg [patterns]. */
fun ComponentsScope.providesBannerPatterns(vararg patterns: BannerPatternOrTagArgument) =
	providesBannerPatterns(patterns.toList())
