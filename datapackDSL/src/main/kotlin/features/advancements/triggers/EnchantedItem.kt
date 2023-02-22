package features.advancements.triggers

import features.advancements.serializers.IntRangeOrIntJson
import features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class EnchantedItem(
	var item: ItemStack? = null,
	var levels: IntRangeOrIntJson? = null,
) : AdvancementTriggerCondition
