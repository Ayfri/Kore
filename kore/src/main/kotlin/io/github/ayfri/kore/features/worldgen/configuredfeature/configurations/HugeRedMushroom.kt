package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import kotlinx.serialization.Serializable

@Serializable
data class HugeRedMushroom(
	var capProvider: BlockStateProvider = simpleStateProvider(),
	var stemProvider: BlockStateProvider = simpleStateProvider(),
	var foliageRadius: Int? = null,
) : FeatureConfig()

fun hugeRedMushroom(
	capProvider: BlockStateProvider = simpleStateProvider(),
	stemProvider: BlockStateProvider = simpleStateProvider(),
	foliageRadius: Int? = null,
	block: HugeRedMushroom.() -> Unit = {},
) = HugeRedMushroom(capProvider, stemProvider, foliageRadius).apply(block)
