package io.github.ayfri.kore.features.worldgen.structureset

import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureSetArgument
import kotlinx.serialization.Serializable

@Serializable
data class ExclusionZone(
	var otherSet: StructureSetArgument,
	var chunkCount: Int = 1,
)
