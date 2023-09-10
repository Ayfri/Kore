package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.arguments.types.resources.worldgen.BiomeArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.DimensionArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.StructureArgument
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Location(
	var biome: BiomeArgument? = null,
	var block: Block? = null,
	var dimension: DimensionArgument? = null,
	var fluid: Fluid? = null,
	var light: IntRangeOrIntJson? = null,
	var position: Position? = null,
	var smokey: Boolean? = null,
	var structure: StructureArgument? = null,
)

fun location(init: Location.() -> Unit = {}) = Location().apply(init)
