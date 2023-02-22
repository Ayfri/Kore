package features.advancements.triggers

import features.advancements.EntityOrPredicates
import features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class VillagerTrade(
	var item: ItemStack? = null,
	var zombie: EntityOrPredicates? = null,
) : AdvancementTriggerCondition
