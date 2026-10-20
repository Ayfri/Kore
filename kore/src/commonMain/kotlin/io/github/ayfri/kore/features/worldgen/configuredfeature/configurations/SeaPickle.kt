package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import kotlinx.serialization.Serializable

/**
 * Configuration for the `sea_pickle` feature.
 *
 * Places a colony of sea pickles on the sea floor around the placement position. Vanilla places the sea pickles of the
 * warm oceans with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#sea_pickle
 *
 * @property count Number of attempts to place a sea pickle, `0` to `256`.
 */
@Serializable
data class SeaPickle(
	var count: IntProvider = ConstantIntProvider(0),
) : FeatureConfig(), IntProviderScope

/**
 * Creates a `sea_pickle` configured feature making [count] placement attempts.
 *
 * The int provider builders are scoped to the enclosing `configuredFeatures` block.
 *
 * ```kotlin
 * seaPickle("sea_pickles", count = constant(20))
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#sea_pickle
 */
fun ConfiguredFeatures.seaPickle(fileName: String, count: IntProvider = ConstantIntProvider(0)) =
	dp.configuredFeature(fileName, SeaPickle(count))
