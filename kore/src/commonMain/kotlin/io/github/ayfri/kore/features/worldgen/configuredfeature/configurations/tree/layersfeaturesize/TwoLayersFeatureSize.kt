package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize

import kotlinx.serialization.Serializable

/**
 * Splits the trunk into a lower and an upper layer, each needing its own free width. Vanilla uses it for every tree
 * except the dark oak and pale oak trees.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property minClippedHeight Minimum free height required for the tree to generate when lower than the trunk height, `0`
 * to `80`. `null` leaves it out, so the trunk height is used.
 * @property limit Height under which [lowerSize] applies, `0` to `81`. `null` leaves it out, so the game uses `1`.
 * @property lowerSize Free width needed under [limit], `0` to `16`. `null` leaves it out, so the game uses `0`.
 * @property upperSize Free width needed from [limit] up, `0` to `16`, `2` for vanilla spruces. `null` leaves it out, so
 * the game uses `1`.
 */
@Serializable
data class TwoLayersFeatureSize(
	override var minClippedHeight: Int? = null,
	var limit: Int? = null,
	var lowerSize: Int? = null,
	var upperSize: Int? = null,
) : LayersFeatureSize()

/**
 * Creates a [TwoLayersFeatureSize], then applies [block].
 *
 * ```kotlin
 * tree("spruce") {
 *     minimumSize = twoLayersFeatureSize(limit = 2, upperSize = 2)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun twoLayersFeatureSize(
	minClippedHeight: Int? = null,
	limit: Int? = null,
	lowerSize: Int? = null,
	upperSize: Int? = null,
	block: TwoLayersFeatureSize.() -> Unit = {},
) = TwoLayersFeatureSize(minClippedHeight, limit, lowerSize, upperSize).apply(block)
