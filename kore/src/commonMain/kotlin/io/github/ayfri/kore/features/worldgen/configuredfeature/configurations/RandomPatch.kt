package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `random_patch` feature.
 *
 * Tries to place [feature] several times around the placement position, at random offsets. Vanilla doesn't use it, its
 * flower, grass and bush patches are `simple_block` features repeated by placement modifiers.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#random_patch
 *
 * @property feature The placed feature attempted at each try.
 * @property tries Number of attempts, `1` or more, `128` in vanilla when `null`.
 * @property xzSpread Maximum horizontal offset of an attempt, `0` or more, `7` in vanilla when `null`.
 * @property ySpread Maximum vertical offset of an attempt, `0` or more, `3` in vanilla when `null`.
 */
@Serializable
data class RandomPatch(
	var feature: PlacedFeatureArgument,
	var tries: Int? = null,
	var xzSpread: Int? = null,
	var ySpread: Int? = null,
) : FeatureConfig()

/**
 * Creates a `random_patch` configured feature attempting [feature] several times around the placement position,
 * configured in [block].
 *
 * ```kotlin
 * randomPatch("dandelion_patch", PlacedFeatures.FLOWER_DEFAULT) {
 *     tries = 64
 *     xzSpread = 5
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#random_patch
 */
fun ConfiguredFeatures.randomPatch(
	fileName: String,
	feature: PlacedFeatureArgument,
	block: RandomPatch.() -> Unit = {},
) = dp.configuredFeature(fileName, RandomPatch(feature).apply(block))
