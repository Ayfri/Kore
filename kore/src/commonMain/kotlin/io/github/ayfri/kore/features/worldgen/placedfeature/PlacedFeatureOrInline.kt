package io.github.ayfri.kore.features.worldgen.placedfeature

import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.PlacementModifier
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.PlacementModifiersScope
import io.github.ayfri.kore.generated.arguments.worldgen.PlacedFeatureOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.FeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A placed feature written inline: the configured feature [feature] with its placement modifiers, in the same shape as
 * a placed feature file.
 *
 * Only references a configured feature by ID. Kore can't write the configured feature inline yet, because the configured
 * feature builders always register a file.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 *
 * @property feature The configured feature to place.
 * @property placementModifiers The placement modifiers applied before placing [feature], in application order.
 */
@Serializable
data class InlinePlacedFeature(
	var feature: FeatureArgument,
	@SerialName("placement")
	override var placementModifiers: List<PlacementModifier> = emptyList(),
) : PlacementModifiersScope

/**
 * This class represents a placed feature file ID, a placed feature tag, or a placed feature written inline.
 * Needed because the `overlay`, `sequence` and `simple_random_selector` features accept all of them.
 *
 * A tag is only valid as the whole `features` field, never as an entry of a list. Decoding never falls back to reading an
 * object as a [feature], so an inline placed feature that can't be decoded throws instead of losing its placement.
 *
 * @property feature The placed feature or placed feature tag. It can be null if the instance holds an inline placed feature.
 * @property inline The inline placed feature. It can be null if the instance holds a placed feature or tag.
 */
@KeepGeneratedSerializer
@Serializable(with = PlacedFeatureOrInline.Companion.PlacedFeatureOrInlineSerializer::class)
data class PlacedFeatureOrInline(
	var feature: PlacedFeatureOrTagArgument? = null,
	var inline: InlinePlacedFeature? = null,
) {
	companion object {
		data object PlacedFeatureOrInlineSerializer :
			EitherInlineSerializer<PlacedFeatureOrInline>(generatedSerializer(), "feature", "inline", objectFallback = false)
	}
}

/**
 * Builder scope declaring the placed features of an `overlay`, `sequence` or `simple_random_selector` feature, by ID
 * with [feature] or inline with [placed].
 *
 * @property features The placed features appended so far, in declaration order.
 */
class PlacedFeaturesScope {
	val features = mutableListOf<PlacedFeatureOrInline>()
}

/** Collects the placed features appended in [block] into a list. */
internal fun buildPlacedFeatures(block: PlacedFeaturesScope.() -> Unit) = PlacedFeaturesScope().apply(block).features

/**
 * Appends the placed feature [id]. A tag isn't possible here because the game only reads a placed feature tag as the whole
 * field, never as an entry of a list.
 *
 * ```kotlin
 * feature(PlacedFeatures.BASALT_PILLAR)  // "minecraft:basalt_pillar"
 * ```
 */
fun PlacedFeaturesScope.feature(id: PlacedFeatureArgument) {
	features += PlacedFeatureOrInline(feature = id)
}

/**
 * Appends a placed feature written inline: the configured feature [feature] with the placement modifiers declared in
 * [block].
 *
 * ```kotlin
 * placed(Features.BASALT_PILLAR) { offset(y = constant(-2)) }  // {"feature": "minecraft:basalt_pillar", "placement": [...]}
 * ```
 */
fun PlacedFeaturesScope.placed(feature: FeatureArgument, block: PlacementModifiersScope.() -> Unit = {}) {
	features += PlacedFeatureOrInline(inline = InlinePlacedFeature(feature).apply(block))
}
