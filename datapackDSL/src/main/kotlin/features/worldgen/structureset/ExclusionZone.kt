package features.worldgen.structureset

import arguments.types.resources.worldgen.StructureSetArgument
import kotlinx.serialization.Serializable

@Serializable
data class ExclusionZone(
	var otherSet: StructureSetArgument,
	var chunkCount: Int = 1,
)
