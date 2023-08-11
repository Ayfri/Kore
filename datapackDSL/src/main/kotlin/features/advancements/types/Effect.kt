package features.advancements.types

import features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Effect(
	var ambiant: Boolean? = null,
	var amplifier: IntRangeOrIntJson? = null,
	var duration: IntRangeOrIntJson? = null,
	var visible: Boolean? = null,
)

fun effect(init: Effect.() -> Unit = {}) = Effect().apply(init)
