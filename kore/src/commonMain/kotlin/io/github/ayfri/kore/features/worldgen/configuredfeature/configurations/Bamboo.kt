package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `bamboo` feature.
 *
 * Grows a bamboo stalk, optionally on a podzol disk. Vanilla places it in the bamboo jungle, with `0.2` for the
 * stalks that come with podzol and `0.0` for the others.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#bamboo
 *
 * @property probability Between `0` and `1`, the chance of a podzol disk of radius `1` to `4` under the bamboo.
 */
@Serializable
data class Bamboo(
	var probability: Double = 0.0,
) : FeatureConfig()

/**
 * Creates a `bamboo` configured feature with a [probability] of podzol under the stalk.
 *
 * ```kotlin
 * bamboo("bamboo_some_podzol", probability = 0.2)
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#bamboo
 */
fun ConfiguredFeatures.bamboo(fileName: String, probability: Double) =
	dp.configuredFeature(fileName, Bamboo(probability))
