package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

/**
 * Placement modifier spreading the position over the positions of a cuboid around it, of size [xzSize] horizontally and
 * [ySize] vertically, with its edges and its interior toggled by [includeEdges] and [includeInterior].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Java_Edition_26.3-snapshot-3
 *
 * @property includeEdges Whether the positions on the edges of the cuboid are kept, `true` when left out.
 * @property includeInterior Whether the positions inside the cuboid are kept, `true` when left out.
 * @property xzSize Horizontal size of the cuboid, `1` to `16`, a constant is written as a bare integer.
 * @property ySize Vertical size of the cuboid, `1` to `16`, a constant is written as a bare integer.
 */
@Serializable
data class Cuboid(
	var includeEdges: Boolean? = null,
	var includeInterior: Boolean? = null,
	var xzSize: IntProvider,
	var ySize: IntProvider,
) : PlacementModifier(), IntProviderScope

/**
 * Appends a `cuboid` placement modifier spreading the position over a cuboid of [xzSize] by [ySize] around it.
 *
 * ```kotlin
 * placedFeature("my_feature", Features.ACACIA) {
 *     cuboid(uniform(2, 6), constant(3), includeInterior = false)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Java_Edition_26.3-snapshot-3
 */
fun PlacedFeature.cuboid(
	xzSize: IntProvider,
	ySize: IntProvider,
	includeEdges: Boolean? = null,
	includeInterior: Boolean? = null,
) {
	placementModifiers += Cuboid(includeEdges, includeInterior, xzSize, ySize)
}

/**
 * Appends a `cuboid` placement modifier spreading the position over a cuboid of exactly [xzSize] by [ySize] around it.
 *
 * ```kotlin
 * placedFeature("my_feature", Features.ACACIA) {
 *     cuboid(4, 2)
 * }
 * ```
 */
fun PlacedFeature.cuboid(
	xzSize: Int,
	ySize: Int,
	includeEdges: Boolean? = null,
	includeInterior: Boolean? = null,
) = cuboid(constant(xzSize), constant(ySize), includeEdges, includeInterior)
