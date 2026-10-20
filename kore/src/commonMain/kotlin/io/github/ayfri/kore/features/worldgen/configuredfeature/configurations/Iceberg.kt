package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `iceberg` feature.
 *
 * Places a floating iceberg of [state]. Vanilla uses packed ice and blue ice icebergs in the frozen oceans.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#iceberg
 *
 * @property state The block state the iceberg is made of.
 */
@Serializable
data class Iceberg(
	var state: BlockState = blockStateStone(),
) : FeatureConfig()

/**
 * Creates an `iceberg` configured feature made of [state].
 *
 * ```kotlin
 * iceberg("iceberg_blue", state = blockState(Blocks.BLUE_ICE))
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#iceberg
 */
fun ConfiguredFeatures.iceberg(fileName: String, state: BlockState = blockStateStone()) =
	dp.configuredFeature(fileName, Iceberg(state))
