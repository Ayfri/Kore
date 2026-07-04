package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import kotlinx.serialization.Serializable

@Serializable
data class HugeFungus(
	var hatState: BlockStateProvider = simpleStateProvider(),
	var decorState: BlockStateProvider = simpleStateProvider(),
	var stemState: BlockStateProvider = simpleStateProvider(),
	var validBaseBlock: BlockStateProvider = simpleStateProvider(),
	var replaceableBlocks: BlockPredicate = True,
	var planted: Boolean? = null,
) : FeatureConfig()

fun hugeFungus(block: HugeFungus.() -> Unit = {}) = HugeFungus().apply(block)
