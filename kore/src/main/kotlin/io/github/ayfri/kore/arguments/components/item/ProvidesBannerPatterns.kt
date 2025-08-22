package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.tagged.BannerPatternTagArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ProvidesBannerPatterns.Companion.ProvidesBannerPatternsSerializer::class)
data class ProvidesBannerPatterns(var patterns: BannerPatternTagArgument) : Component() {
	companion object {
		data object ProvidesBannerPatternsSerializer : InlineAutoSerializer<ProvidesBannerPatterns>(ProvidesBannerPatterns::class)
	}
}

fun ComponentsScope.providesBannerPatterns(patterns: BannerPatternTagArgument) = apply {
	this[ItemComponentTypes.PROVIDES_BANNER_PATTERNS] = ProvidesBannerPatterns(patterns)
}
