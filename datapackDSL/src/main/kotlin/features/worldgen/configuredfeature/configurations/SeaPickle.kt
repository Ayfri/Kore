package features.worldgen.configuredfeature.configurations

import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class SeaPickle(
	var count: IntProvider = constant(0),
) : FeatureConfig()

fun seaPickle(count: IntProvider = constant(0)) = SeaPickle(count)
