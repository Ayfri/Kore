package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Slots(
	var empty: IntRangeOrIntJson? = null,
	var full: IntRangeOrIntJson? = null,
	var occupied: IntRangeOrIntJson? = null,
)

fun slots(empty: IntRangeOrIntJson? = null, full: IntRangeOrIntJson? = null, occupied: IntRangeOrIntJson? = null) =
	Slots(empty, full, occupied)
