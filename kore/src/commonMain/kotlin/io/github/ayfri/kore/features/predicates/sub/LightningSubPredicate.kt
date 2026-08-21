package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Matches a lightning bolt by what it set on fire and what it struck, keyed under `minecraft:type_specific/lightning`. */
@Serializable
@SerialName("type_specific/lightning")
data class LightningSubPredicate(
	var blocksSetOnFire: IntRangeOrIntJson? = null,
	var entityStruck: EntityPredicate? = null,
) : EntitySubPredicate()

/** Adds a [LightningSubPredicate]. */
fun EntityTypeSpecificScope.lightning(block: LightningSubPredicate.() -> Unit = {}) {
	entity.subPredicates += LightningSubPredicate().apply(block)
}

/** Matches the entity struck by the lightning bolt. */
fun LightningSubPredicate.entityStruck(block: EntityPredicate.() -> Unit = {}) = apply {
	entityStruck = EntityPredicate().apply(block)
}
