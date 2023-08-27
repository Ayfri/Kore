package features.advancements.types

import arguments.types.resources.worldgen.BiomeArgument
import arguments.types.resources.worldgen.DimensionArgument
import arguments.types.resources.worldgen.StructureArgument
import features.advancements.serializers.IntRangeOrIntJson
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
