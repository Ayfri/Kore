package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `underwater_magma` feature.
 *
 * Places a small patch of magma blocks, up to 3x3x3, on the floor under the water. Vanilla places its underwater magma
 * with it, in every Overworld biome.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#underwater_magma
 *
 * @property floorSearchRange Distance searched from the placement position to find the underwater floor, `0` to `512`.
 * @property placementRadiusAroundFloor Radius around the floor position in which magma can be placed, `0` to `64`.
 * @property placementProbabilityPerValidPosition Chance, `0` to `1`, that a valid position gets a magma block.
 */
@Serializable
data class UnderwaterMagma(
	var floorSearchRange: Int = 0,
	var placementRadiusAroundFloor: Int = 0,
	var placementProbabilityPerValidPosition: Double = 0.0,
) : FeatureConfig()

/**
 * Creates an `underwater_magma` configured feature, configured in [block].
 *
 * ```kotlin
 * underwaterMagma(
 *     "underwater_magma",
 *     floorSearchRange = 5,
 *     placementRadiusAroundFloor = 1,
 *     placementProbabilityPerValidPosition = 0.5,
 * )
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#underwater_magma
 */
fun ConfiguredFeatures.underwaterMagma(
	fileName: String,
	floorSearchRange: Int = 0,
	placementRadiusAroundFloor: Int = 0,
	placementProbabilityPerValidPosition: Double = 0.0,
	block: UnderwaterMagma.() -> Unit = {},
) = dp.configuredFeature(
	fileName,
	UnderwaterMagma(floorSearchRange, placementRadiusAroundFloor, placementProbabilityPerValidPosition).apply(block),
)
