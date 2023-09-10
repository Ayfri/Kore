package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class RandomizedIntProvider(
	var property: String = "",
	var values: IntProvider = constant(0),
	var source: BlockStateProvider = simpleStateProvider(),
) : BlockStateProvider()

fun randomizedIntProvider(
	property: String = "",
	values: IntProvider = constant(0),
	source: BlockStateProvider = simpleStateProvider(),
	block: RandomizedIntProvider.() -> Unit = {},
) = RandomizedIntProvider(property, values, source).apply(block)
