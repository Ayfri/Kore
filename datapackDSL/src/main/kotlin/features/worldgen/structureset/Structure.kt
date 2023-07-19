package features.worldgen.structureset

import arguments.types.resources.worldgen.ConfiguredStructureArgument
import kotlinx.serialization.Serializable

@Serializable
data class Structure(
	var structure: ConfiguredStructureArgument,
	var weight: Int = 1,
)
