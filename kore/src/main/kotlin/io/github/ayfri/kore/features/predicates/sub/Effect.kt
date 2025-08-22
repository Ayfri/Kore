package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Effect(
	var ambiant: Boolean? = null,
	var amplifier: IntRangeOrIntJson? = null,
	var duration: IntRangeOrIntJson? = null,
	var visible: Boolean? = null,
)

fun effect(init: Effect.() -> Unit = {}) = Effect().apply(init)
