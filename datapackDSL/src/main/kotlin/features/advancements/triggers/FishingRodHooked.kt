package features.advancements.triggers

import features.advancements.EntityOrPredicates
import features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class FishingRodHooked(
	var entity: EntityOrPredicates? = null,
	var item: ItemStack? = null,
	var rod: ItemStack? = null,
) : AdvancementTriggerCondition
