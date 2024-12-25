package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import kotlinx.serialization.Serializable

@Serializable
data class SimpleBlock(
	var toPlace: BlockStateProvider = simpleStateProvider(),
	var scheduleTick: Boolean = false,
) : FeatureConfig()

fun simpleBlock(toPlace: BlockStateProvider = simpleStateProvider(), scheduleTick: Boolean = false) = SimpleBlock(toPlace, scheduleTick) 