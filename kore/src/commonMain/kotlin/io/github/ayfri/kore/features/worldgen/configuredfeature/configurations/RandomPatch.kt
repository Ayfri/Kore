package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Tries to place [feature] several times around the origin, at random offsets, which is how the vanilla flower,
 * grass and mushroom patches are generated.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#random_patch
 *
 * @property feature The placed feature attempted at each try.
 * @property tries How many placements are attempted, `128` by default.
 * @property xzSpread Maximum horizontal offset of an attempt from the origin, `7` by default.
 * @property ySpread Maximum vertical offset of an attempt from the origin, `3` by default.
 */
@Serializable
data class RandomPatch(
	var feature: PlacedFeatureArgument,
	var tries: Int? = null,
	var xzSpread: Int? = null,
	var ySpread: Int? = null,
) : FeatureConfig()

/**
 * Creates a `random_patch` configured feature attempting [feature] [tries] times around the origin.
 *
 * ```kotlin
 * configuredFeatures {
 *     randomPatch("dandelion_patch", PlacedFeatures.FLOWER_DEFAULT) {
 *         tries = 64
 *         xzSpread = 5
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#random_patch
 */
fun ConfiguredFeatures.randomPatch(
	fileName: String,
	feature: PlacedFeatureArgument,
	block: RandomPatch.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, RandomPatch(feature).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
