package features.advancements.triggers

import features.advancements.types.ItemStack
import features.advancements.types.Location
import kotlinx.serialization.Serializable

@Serializable
data class ItemUsedOnBlock(
	var location: Location? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition
