package features.worldgen.configuredfeature.configurations

import arguments.types.resources.StructureArgument
import arguments.types.resources.worldgen.ProcessorArgument
import kotlinx.serialization.Serializable

@Serializable
data class Fossil(
	var maxEmptyCornersAllowed: Int = 0,
	var fossilStructures: List<StructureArgument> = emptyList(),
	var overlayStructures: List<StructureArgument> = emptyList(),
	var fossilProcessors: ProcessorArgument,
	var overlayProcessors: ProcessorArgument,
) : FeatureConfig()

fun fossil(
	maxEmptyCornersAllowed: Int = 0,
	fossilStructures: List<StructureArgument> = emptyList(),
	overlayStructures: List<StructureArgument> = emptyList(),
	fossilProcessors: ProcessorArgument,
	overlayProcessors: ProcessorArgument,
) = Fossil(maxEmptyCornersAllowed, fossilStructures, overlayStructures, fossilProcessors, overlayProcessors)
