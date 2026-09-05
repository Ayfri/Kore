package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class EndGateway(
	var exact: Boolean = false,
	var exit: TripleAsArray<Int, Int, Int>? = null,
) : FeatureConfig()

fun ConfiguredFeatures.endGateway(
	fileName: String,
	exact: Boolean = false,
	exit: TripleAsArray<Int, Int, Int>? = null,
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, EndGateway(exact, exit))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
