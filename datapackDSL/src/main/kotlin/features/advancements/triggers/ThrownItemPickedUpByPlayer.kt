package features.advancements.triggers

import features.advancements.EntityOrPredicates
import features.advancements.types.ItemStack
import kotlinx.serialization.Serializable

@Serializable
data class ThrownItemPickedUpByPlayer(
	var entity: EntityOrPredicates? = null,
	var item: ItemStack? = null,
) : AdvancementTriggerCondition
