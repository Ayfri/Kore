package features.advancements.types

import features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Position(
	var x: IntRangeOrIntJson? = null,
	var y: IntRangeOrIntJson? = null,
	var z: IntRangeOrIntJson? = null,
)

fun position(x: IntRangeOrIntJson? = null, y: IntRangeOrIntJson? = null, z: IntRangeOrIntJson? = null) = Position(x, y, z)
