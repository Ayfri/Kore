package features.worldgen.configuredfeature.blockstateprovider

import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
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
