package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

/**
 * Passes when the loot context has a killer player, or when it has none if [inverse] is `true`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - killed_by_player](https://minecraft.wiki/w/Predicate#killed_by_player)
 */
@Serializable
data class KilledByPlayer(
	var inverse: Boolean? = null,
) : PredicateCondition()

/** Adds a [KilledByPlayer] condition. */
fun Predicate.killedByPlayer(inverse: Boolean? = null) {
	predicateConditions += KilledByPlayer(inverse)
}
