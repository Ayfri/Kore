package io.github.ayfri.kore.features.predicates.sub

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("type_specific/raider")
data class RaiderSubPredicate(
	var hasRaid: Boolean? = null,
	var isCaptain: Boolean? = null,
) : EntitySubPredicate()

fun EntityTypeSpecificScope.raider(hasRaid: Boolean? = null, isCaptain: Boolean? = null, block: RaiderSubPredicate.() -> Unit = {}) {
	entity.subPredicates += RaiderSubPredicate(hasRaid, isCaptain).apply(block)
}
