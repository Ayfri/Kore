package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.heightproviders.ConstantHeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import kotlinx.serialization.Serializable

/**
 * Moves the position to the Y level drawn from [height], keeping its X and Z.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#height_range
 *
 * @property height The height provider drawing the Y level.
 */
@Serializable
data class HeightRange(
	var height: HeightProvider = ConstantHeightProvider(Absolute(0)),
) : PlacementModifier()

/**
 * Adds a `height_range` placement modifier drawing the Y level from [height].
 *
 * ```kotlin
 * placedFeature("my_ore", ConfiguredFeatures.ORE_DIAMOND) {
 *     heightRange(uniformHeightProvider(aboveBottom(0), absolute(16)))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#height_range
 */
fun PlacedFeature.heightRange(height: HeightProvider = ConstantHeightProvider(Absolute(0))) {
	placementModifiers += HeightRange(height)
}
