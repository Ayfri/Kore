package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.asRangeOrInt
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

fun Position.x(value: IntRangeOrIntJson) = apply { x = value }
fun Position.x(value: IntRange) = apply { x = value.asRangeOrInt() }
fun Position.x(value: Int) = apply { x = value.asRangeOrInt() }

fun Position.y(value: IntRangeOrIntJson) = apply { y = value }
fun Position.y(value: IntRange) = apply { y = value.asRangeOrInt() }
fun Position.y(value: Int) = apply { y = value.asRangeOrInt() }

fun Position.z(value: IntRangeOrIntJson) = apply { z = value }
fun Position.z(value: IntRange) = apply { z = value.asRangeOrInt() }
fun Position.z(value: Int) = apply { z = value.asRangeOrInt() }
