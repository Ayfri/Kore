package io.github.ayfri.kore.features.worldgen.verticalanchors

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * A single Y level, expressed either absolutely or relative to one of the dimension's build limits.
 *
 * Vertical anchors are serialized as a one-key object such as `{ "absolute": 64 }`, never with a `type` field, and
 * are used by the height providers, the `height_range` block predicate and the `y_above` / `vertical_gradient` material rule
 * conditions.
 *
 * The resolved Y is never clamped, so an anchor can point outside of `min_y` .. `min_y + height - 1`.
 *
 * Every builder is an extension on [VerticalAnchorScope], so [absolute], [aboveBottom], [belowTop] and [relativeToSeaLevel] only resolve
 * inside a block that actually accepts a vertical anchor, such as `cave("...") { }` or `materialRules { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/vertical_anchor
 */
@GeneratedSealedSerializer
@Serializable(with = VerticalAnchor.Companion.VerticalAnchorSerializer::class)
sealed class VerticalAnchor {
	companion object {
		data object VerticalAnchorSerializer :
			NamespacedPolymorphicSerializer<VerticalAnchor>(
				verticalAnchorSealedSerializer(),
				skipOutputName = true,
				decodeUntyped = true,
			)
	}
}

/**
 * A Y level given as an absolute coordinate, the one shown on the F3 screen.
 *
 * Serialized as `{ "absolute": <y> }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/vertical_anchor
 *
 * @property absolute The absolute Y coordinate.
 */
@Serializable
data class Absolute(var absolute: Int) : VerticalAnchor()

/**
 * A Y level given as an offset above the bottom of the dimension, so `0` is `min_y` itself.
 *
 * Serialized as `{ "above_bottom": <offset> }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/vertical_anchor
 *
 * @property aboveBottom The number of blocks above the bottom of the dimension.
 */
@Serializable
data class AboveBottom(var aboveBottom: Int) : VerticalAnchor()

/**
 * A Y level given as an offset below the top of the dimension, so `0` is the highest buildable block and larger
 * values move the anchor further down.
 *
 * Serialized as `{ "below_top": <offset> }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/vertical_anchor
 *
 * @property belowTop The number of blocks below the top of the dimension.
 */
@Serializable
data class BelowTop(var belowTop: Int) : VerticalAnchor()

/**
 * A Y level given as an offset from the sea level of the dimension, negative values being below it.
 *
 * Serialized as `{ "relative_to_sea_level": <offset> }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/vertical_anchor
 *
 * @property relativeToSeaLevel The number of blocks above the sea level, negative to go below it.
 */
@Serializable
data class RelativeToSeaLevel(var relativeToSeaLevel: Int) : VerticalAnchor()
