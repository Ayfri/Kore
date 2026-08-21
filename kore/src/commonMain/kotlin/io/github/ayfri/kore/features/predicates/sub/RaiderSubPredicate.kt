package io.github.ayfri.kore.features.predicates.sub

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Matches a raid participant by whether a raid is running and whether it is the captain, keyed under `minecraft:type_specific/raider`. */
@Serializable
@SerialName("type_specific/raider")
data class RaiderSubPredicate(
	var hasRaid: Boolean? = null,
	var isCaptain: Boolean? = null,
) : EntitySubPredicate()

/** Adds a [RaiderSubPredicate]. */
fun EntityTypeSpecificScope.raider(hasRaid: Boolean? = null, isCaptain: Boolean? = null, block: RaiderSubPredicate.() -> Unit = {}) {
	entity.subPredicates += RaiderSubPredicate(hasRaid, isCaptain).apply(block)
}
