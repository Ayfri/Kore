package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.heightproviders.ConstantHeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchor
import kotlinx.serialization.Serializable

/**
 * Moves the position to the Y level drawn from [height], keeping its X and Z.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#height_range
 *
 * @property height The height provider drawing the Y level.
 */
@Serializable
data class HeightRange(
	var height: HeightProvider,
) : PlacementModifier()

/**
 * Adds a `height_range` placement modifier drawing the Y level from [height].
 *
 * ```kotlin
 * placedFeature("my_ore", Features.ORE_DIAMOND) {
 *     heightRange(uniformHeightProvider(aboveBottom(0), absolute(16)))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#height_range
 */
fun PlacementModifiersScope.heightRange(height: HeightProvider) {
	placementModifiers += HeightRange(height)
}

/**
 * Adds a `height_range` placement modifier moving the position to the fixed Y level of [height].
 *
 * ```kotlin
 * placedFeature("my_ore", Features.ORE_DIAMOND) {
 *     heightRange(absolute(32))
 * }
 * ```
 */
fun PlacementModifiersScope.heightRange(height: VerticalAnchor) = heightRange(ConstantHeightProvider(height))
