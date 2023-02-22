package features.advancements.types

import arguments.Argument
import features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Location(
	var biome: Argument.Biome? = null,
	var block: Block? = null,
	var dimension: Argument.Dimension? = null,
	var fluid: Fluid? = null,
	var light: IntRangeOrIntJson? = null,
	var position: Position? = null,
	var smokey: Boolean? = null,
	var structure: Argument.Structure? = null,
)

fun location(init: Location.() -> Unit = {}): Location {
	val location = Location()
	location.init()
	return location
}
