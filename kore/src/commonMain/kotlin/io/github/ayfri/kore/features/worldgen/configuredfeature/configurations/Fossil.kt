package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ProcessorListArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import kotlinx.serialization.Serializable

@Serializable
data class Fossil(
	var maxEmptyCornersAllowed: Int = 0,
	var fossilStructures: List<StructureArgument> = emptyList(),
	var overlayStructures: List<StructureArgument> = emptyList(),
	var fossilProcessors: ProcessorListArgument,
	var overlayProcessors: ProcessorListArgument,
) : FeatureConfig()

fun ConfiguredFeatures.fossil(
	fileName: String,
	maxEmptyCornersAllowed: Int = 0,
	fossilStructures: List<StructureArgument> = emptyList(),
	overlayStructures: List<StructureArgument> = emptyList(),
	fossilProcessors: ProcessorListArgument,
	overlayProcessors: ProcessorListArgument,
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(
		fileName,
		Fossil(maxEmptyCornersAllowed, fossilStructures, overlayStructures, fossilProcessors, overlayProcessors),
	)
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
