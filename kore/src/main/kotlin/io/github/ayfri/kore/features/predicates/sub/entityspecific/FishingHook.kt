package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable

@Serializable
data class FishingHook(var inOpenWater: Boolean? = null) : EntityTypeSpecific()

fun Entity.fishingHookTypeSpecific(inOpenWater: Boolean? = null, block: FishingHook.() -> Unit = {}) = apply {
	typeSpecific = FishingHook(inOpenWater).apply(block)
}
