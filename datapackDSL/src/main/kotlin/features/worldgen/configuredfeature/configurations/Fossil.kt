package features.worldgen.configuredfeature.configurations

import arguments.types.resources.worldgen.ProcessorListArgument
import arguments.types.resources.worldgen.StructureArgument
import kotlinx.serialization.Serializable

@Serializable
data class Fossil(
	var maxEmptyCornersAllowed: Int = 0,
	var fossilStructures: List<StructureArgument> = emptyList(),
	var overlayStructures: List<StructureArgument> = emptyList(),
	var fossilProcessors: ProcessorListArgument,
	var overlayProcessors: ProcessorListArgument,
) : FeatureConfig()

fun fossil(
	maxEmptyCornersAllowed: Int = 0,
	fossilStructures: List<StructureArgument> = emptyList(),
	overlayStructures: List<StructureArgument> = emptyList(),
	fossilProcessors: ProcessorListArgument,
	overlayProcessors: ProcessorListArgument,
) = Fossil(maxEmptyCornersAllowed, fossilStructures, overlayStructures, fossilProcessors, overlayProcessors)
