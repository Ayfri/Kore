package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import kotlinx.serialization.Serializable

@Serializable
data class NetherrackReplaceBlobs(
	var state: BlockState = blockStateStone(),
	var target: BlockState = blockStateStone(),
	var radius: IntProvider = ConstantIntProvider(0),
) : FeatureConfig(), IntProviderScope

fun ConfiguredFeatures.netherrackReplaceBlobs(
	fileName: String,
	state: BlockState = blockStateStone(),
	target: BlockState = blockStateStone(),
	radius: IntProvider = ConstantIntProvider(0),
	block: NetherrackReplaceBlobs.() -> Unit = {},
) = dp.configuredFeature(fileName, NetherrackReplaceBlobs(state, target, radius).apply(block))
