package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.generated.BannerPatterns
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable
data class BannerPatternEntry(
	var pattern: BannerPatterns,
	var color: FormattingColor,
)

@Serializable(with = BannerPatternsComponent.Companion.BannerPatternComponentSerializer::class)
data class BannerPatternsComponent(var list: List<BannerPatternEntry>) : Component() {
	companion object {
		object BannerPatternComponentSerializer : InlineSerializer<BannerPatternsComponent, List<BannerPatternEntry>>(
			ListSerializer(BannerPatternEntry.serializer()),
			BannerPatternsComponent::list
		)
	}
}

fun Components.bannerPatterns(patterns: List<BannerPatternEntry>) = apply {
	components["banner_patterns"] = BannerPatternsComponent(patterns)
}

fun Components.bannerPatterns(vararg patterns: BannerPatternEntry) = apply {
	components["banner_patterns"] = BannerPatternsComponent(patterns.toList())
}

fun Components.bannerPatterns(block: BannerPatternsComponent.() -> Unit) = apply {
	components["banner_patterns"] = BannerPatternsComponent(mutableListOf()).apply(block)
}

fun BannerPatternsComponent.pattern(pattern: BannerPatterns, color: FormattingColor) = apply {
	list += BannerPatternEntry(pattern, color)
}
