package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class ShotCrossbow(
	var item: ItemStack? = null,
) : AdvancementTriggerCondition
