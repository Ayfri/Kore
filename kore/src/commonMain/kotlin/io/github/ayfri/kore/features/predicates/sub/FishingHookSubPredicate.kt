package io.github.ayfri.kore.features.predicates.sub

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Matches a fishing hook by whether it landed in open water, keyed under `minecraft:type_specific/fishing_hook`. */
@Serializable
@SerialName("type_specific/fishing_hook")
data class FishingHookSubPredicate(var inOpenWater: Boolean? = null) : EntitySubPredicate()

/** Adds a [FishingHookSubPredicate] matching fishing hooks by [inOpenWater]. */
fun EntityTypeSpecificScope.fishingHook(inOpenWater: Boolean? = null, block: FishingHookSubPredicate.() -> Unit = {}) {
	entity.subPredicates += FishingHookSubPredicate(inOpenWater).apply(block)
}
