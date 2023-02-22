package features.advancements.triggers

import features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class UsingItem(
	var item: ItemStack? = null,
) : AdvancementTriggerCondition
