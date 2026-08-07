package io.github.ayfri.kore.features.predicates.sub

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("type_specific/sheep")
data class SheepSubPredicate(val sheared: Boolean? = null) : EntitySubPredicate()

fun EntityTypeSpecificScope.sheep(sheared: Boolean? = null) {
	entity.subPredicates += SheepSubPredicate(sheared)
}
