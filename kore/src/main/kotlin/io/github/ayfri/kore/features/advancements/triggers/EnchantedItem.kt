package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class EnchantedItem(
	var item: ItemStack? = null,
	var levels: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition
