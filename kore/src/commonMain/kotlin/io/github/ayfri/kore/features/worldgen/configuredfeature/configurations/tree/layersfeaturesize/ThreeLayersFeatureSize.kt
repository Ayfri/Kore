package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize

import kotlinx.serialization.Serializable

/**
 * Splits the trunk into a lower, a middle and an upper layer, each needing its own free width. Vanilla uses it for the
 * dark oak and pale oak trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property minClippedHeight Minimum free height required for the tree to generate when lower than the trunk height, `0`
 * to `80`. `null` leaves it out, so the trunk height is used.
 * @property limit Height of the lower layer from the bottom of the trunk, `0` to `80`. `null` leaves it out, so the game
 * uses `1`.
 * @property upperLimit Height of the upper layer from the top of the trunk, `0` to `80`. `null` leaves it out, so the
 * game uses `1`.
 * @property lowerSize Free width needed in the lower layer, `0` to `16`. `null` leaves it out, so the game uses `0`.
 * @property middleSize Free width needed in the middle layer, `0` to `16`. `null` leaves it out, so the game uses `1`.
 * @property upperSize Free width needed in the upper layer, `0` to `16`, `2` for vanilla dark oaks. `null` leaves it out,
 * so the game uses `1`.
 */
@Serializable
data class ThreeLayersFeatureSize(
	override var minClippedHeight: Int? = null,
	var limit: Int? = null,
	var upperLimit: Int? = null,
	var lowerSize: Int? = null,
	var middleSize: Int? = null,
	var upperSize: Int? = null,
) : LayersFeatureSize()

/**
 * Creates a [ThreeLayersFeatureSize] configured in [block].
 *
 * ```kotlin
 * tree("dark_oak") {
 *     minimumSize = threeLayersFeatureSize { upperSize = 2 }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun threeLayersFeatureSize(block: ThreeLayersFeatureSize.() -> Unit = {}) = ThreeLayersFeatureSize().apply(block)
