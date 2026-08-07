package io.github.ayfri.kore.features.predicates.sub

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("type_specific/fishing_hook")
data class FishingHookSubPredicate(var inOpenWater: Boolean? = null) : EntitySubPredicate()

fun EntityTypeSpecificScope.fishingHook(inOpenWater: Boolean? = null, block: FishingHookSubPredicate.() -> Unit = {}) {
	entity.subPredicates += FishingHookSubPredicate(inOpenWater).apply(block)
}
