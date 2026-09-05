package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `nether_forest_vegetation` feature, the roots and fungi scattered over the nether forest
 * floors.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#nether_forest_vegetation
 *
 * @property stateProvider The block states scattered over the floor.
 * @property spreadWidth The horizontal size of the area the blocks are scattered in.
 * @property spreadHeight The vertical size of the area the blocks are scattered in.
 */
@Serializable
data class NetherForestVegetation(
	var stateProvider: BlockStateProvider = SimpleStateProvider(),
	var spreadWidth: Int = 0,
	var spreadHeight: Int = 0,
) : FeatureConfig(), BlockStateProviderScope

/**
 * Creates a `nether_forest_vegetation` configured feature, scattering the blocks given by
 * [NetherForestVegetation.stateProvider].
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * netherForestVegetation("crimson_roots", spreadWidth = 8, spreadHeight = 4) {
 *     stateProvider = weightedStateProvider {
 *         entry(Blocks.CRIMSON_ROOTS, weight = 2)
 *         entry(Blocks.CRIMSON_FUNGUS)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#nether_forest_vegetation
 */
fun ConfiguredFeatures.netherForestVegetation(
	fileName: String,
	spreadWidth: Int = 0,
	spreadHeight: Int = 0,
	block: NetherForestVegetation.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature =
		ConfiguredFeature(fileName, NetherForestVegetation(spreadWidth = spreadWidth, spreadHeight = spreadHeight).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
