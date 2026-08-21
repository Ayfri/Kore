package io.github.ayfri.kore.features.predicates.sub

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Matches a sheep by whether it is sheared, keyed under `minecraft:type_specific/sheep`. */
@Serializable
@SerialName("type_specific/sheep")
data class SheepSubPredicate(var sheared: Boolean? = null) : EntitySubPredicate()

/** Adds a [SheepSubPredicate] matching sheep by [sheared]. */
fun EntityTypeSpecificScope.sheep(sheared: Boolean? = null, block: SheepSubPredicate.() -> Unit = {}) {
	entity.subPredicates += SheepSubPredicate(sheared).apply(block)
}
