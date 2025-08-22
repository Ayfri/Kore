package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Position(
	var x: IntRangeOrIntJson? = null,
	var y: IntRangeOrIntJson? = null,
	var z: IntRangeOrIntJson? = null,
)

fun position(x: IntRangeOrIntJson? = null, y: IntRangeOrIntJson? = null, z: IntRangeOrIntJson? = null) =
	Position(x, y, z)
