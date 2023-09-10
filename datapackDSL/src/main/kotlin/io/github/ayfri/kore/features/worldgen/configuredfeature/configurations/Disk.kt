package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class Disk(
	var stateProvider: BlockStateProvider,
	var target: BlockPredicate,
	var radius: IntProvider = constant(0),
	var halfHeight: Int = 0,
) : FeatureConfig()

fun disk(
	stateProvider: BlockStateProvider,
	target: BlockPredicate,
	radius: IntProvider = constant(0),
	halfHeight: Int = 0,
	block: Disk.() -> Unit = {},
) = Disk(stateProvider, target, radius, halfHeight).apply(block)

fun disk(
	stateProvider: BlockStateProvider,
	target: BlockPredicate,
	radius: Int,
	halfHeight: Int = 0,
) = Disk(stateProvider, target, constant(radius), halfHeight)
