package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import kotlinx.serialization.Serializable

/**
 * Configuration for the `netherrack_replace_blobs` feature.
 *
 * Replaces the [target] blocks inside a blob around the placement position with [state]. Vanilla builds the basalt and
 * blackstone blobs of the basalt deltas with it, replacing netherrack.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#netherrack_replace_blobs
 *
 * @property state The block state placed.
 * @property target The block state replaced, its properties are ignored.
 * @property radius Radius of the blob, `0` to `12`.
 */
@Serializable
data class NetherrackReplaceBlobs(
	var state: BlockState = blockStateStone(),
	var target: BlockState = blockStateStone(),
	var radius: IntProvider = ConstantIntProvider(0),
) : FeatureConfig(), IntProviderScope

/**
 * Creates a `netherrack_replace_blobs` configured feature replacing [target] with [state] in blobs of [radius],
 * configured in [block].
 *
 * The int provider builders are scoped to [block] and to the enclosing `configuredFeatures` block.
 *
 * ```kotlin
 * netherrackReplaceBlobs(
 *     "basalt_blobs",
 *     state = blockState(Blocks.BASALT, "axis" to "y"),
 *     target = blockState(Blocks.NETHERRACK),
 *     radius = uniform(3, 7),
 * )
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#netherrack_replace_blobs
 */
fun ConfiguredFeatures.netherrackReplaceBlobs(
	fileName: String,
	state: BlockState = blockStateStone(),
	target: BlockState = blockStateStone(),
	radius: IntProvider = ConstantIntProvider(0),
	block: NetherrackReplaceBlobs.() -> Unit = {},
) = dp.configuredFeature(fileName, NetherrackReplaceBlobs(state, target, radius).apply(block))
