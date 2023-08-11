package features.worldgen.configuredfeature.configurations

import serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class EndGateway(
	var exact: Boolean = false,
	var exit: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
) : FeatureConfig()

fun endGateway(exact: Boolean = false, exit: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0)) = EndGateway(exact, exit)
