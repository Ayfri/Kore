package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import kotlinx.serialization.Serializable

@Serializable
data class BlockPile(
	var stateProvider: BlockStateProvider = simpleStateProvider(),
) : FeatureConfig()

fun blockPile(stateProvider: BlockStateProvider = simpleStateProvider()) = BlockPile(stateProvider)
