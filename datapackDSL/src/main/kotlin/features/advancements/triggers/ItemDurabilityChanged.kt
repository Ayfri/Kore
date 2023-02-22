package features.advancements.triggers

import features.advancements.serializers.IntRangeOrIntJson
import features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class ItemDurabilityChanged(
	var delta: IntRangeOrIntJson? = null,
	var durability: IntRangeOrIntJson? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition
