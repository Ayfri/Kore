package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class ItemDurabilityChanged(
	var delta: IntRangeOrIntJson? = null,
	var durability: IntRangeOrIntJson? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition
