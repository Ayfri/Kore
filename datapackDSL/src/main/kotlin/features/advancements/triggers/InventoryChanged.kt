package features.advancements.triggers

import features.advancements.types.ItemStack
import features.advancements.types.Slots
import kotlinx.serialization.Serializable

@Serializable
data class InventoryChanged(
	var items: List<ItemStack>? = null,
	var slots: Slots? = null,
) : AdvancementTriggerCondition
