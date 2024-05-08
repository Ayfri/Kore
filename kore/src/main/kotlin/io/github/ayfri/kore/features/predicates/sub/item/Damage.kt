package io.github.ayfri.kore.features.predicates.sub.item

import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Damage(
	var durability: IntRangeOrIntJson? = null,
	var damage: IntRangeOrIntJson? = null,
)

fun damage(init: Damage.() -> Unit = {}) = Damage().apply(init)
