package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Slots(
	var empty: IntRangeOrIntJson? = null,
	var full: IntRangeOrIntJson? = null,
	var occupied: IntRangeOrIntJson? = null,
)

fun slots(empty: IntRangeOrIntJson? = null, full: IntRangeOrIntJson? = null, occupied: IntRangeOrIntJson? = null) =
	Slots(empty, full, occupied)
