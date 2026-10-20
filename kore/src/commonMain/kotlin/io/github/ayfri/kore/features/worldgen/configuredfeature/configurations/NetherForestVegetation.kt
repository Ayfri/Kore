package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `nether_forest_vegetation` feature.
 *
 * Scatters blocks over the floor around the placement position. Vanilla scatters the roots, fungi and sprouts of the
 * crimson and warped forests with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#nether_forest_vegetation
 *
 * @property stateProvider The block state provider of the scattered blocks.
 * @property spreadWidth Horizontal size of the area the blocks are scattered in, `1` or more.
 * @property spreadHeight Vertical size of the area the blocks are scattered in, `1` or more.
 */
@Serializable
data class NetherForestVegetation(
	var stateProvider: BlockStateProvider = SimpleStateProvider(),
	var spreadWidth: Int = 0,
	var spreadHeight: Int = 0,
) : FeatureConfig(), BlockStateProviderScope

/**
 * Creates a `nether_forest_vegetation` configured feature scattering [NetherForestVegetation.stateProvider] over an
 * area of [spreadWidth] by [spreadHeight], configured in [block].
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * netherForestVegetation("crimson_roots", spreadWidth = 8, spreadHeight = 4) {
 *     stateProvider = weightedStateProvider {
 *         entry(Blocks.CRIMSON_ROOTS, weight = 87)
 *         entry(Blocks.CRIMSON_FUNGUS, weight = 11)
 *         entry(Blocks.WARPED_FUNGUS)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#nether_forest_vegetation
 */
fun ConfiguredFeatures.netherForestVegetation(
	fileName: String,
	spreadWidth: Int = 0,
	spreadHeight: Int = 0,
	block: NetherForestVegetation.() -> Unit = {},
) =
	dp.configuredFeature(fileName, NetherForestVegetation(spreadWidth = spreadWidth, spreadHeight = spreadHeight).apply(block))
