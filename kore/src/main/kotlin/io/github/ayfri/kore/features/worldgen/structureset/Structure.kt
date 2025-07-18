package io.github.ayfri.kore.features.worldgen.structureset

import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import kotlinx.serialization.Serializable

@Serializable
data class Structure(
	var structure: ConfiguredStructureArgument,
	var weight: Int = 1,
)
