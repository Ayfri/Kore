package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable

@Serializable
data class Lightning(
	var blocksSetOnFire: IntRangeOrIntJson? = null,
	var entityStruck: Entity? = null,
) : EntityTypeSpecific()

fun Entity.lightningTypeSpecific(block: Lightning.() -> Unit = {}) = apply {
	typeSpecific = Lightning().apply(block)
}

fun Lightning.entityStruck(block: Entity.() -> Unit = {}) = apply {
	entityStruck = Entity().apply(block)
}
