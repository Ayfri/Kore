package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.features.advancements.serializers.FloatRangeOrFloatJson
import kotlinx.serialization.Serializable

@Serializable
data class Damage(
	var blocked: Boolean? = null,
	var dealt: FloatRangeOrFloatJson? = null,
	var sourceEntity: Entity? = null,
	var taken: FloatRangeOrFloatJson? = null,
	var type: DamageSource? = null,
)

fun damage(init: Damage.() -> Unit = {}) = Damage().apply(init)
