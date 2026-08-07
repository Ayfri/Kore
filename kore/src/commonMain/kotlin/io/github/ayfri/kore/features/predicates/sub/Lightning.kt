package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("type_specific/lightning")
data class LightningSubPredicate(
	var blocksSetOnFire: IntRangeOrIntJson? = null,
	var entityStruck: Entity? = null,
) : EntitySubPredicate()

fun EntityTypeSpecificScope.lightning(block: LightningSubPredicate.() -> Unit = {}) {
	entity.subPredicates += LightningSubPredicate().apply(block)
}

fun LightningSubPredicate.entityStruck(block: Entity.() -> Unit = {}) = apply {
	entityStruck = Entity().apply(block)
}
