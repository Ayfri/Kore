package features.advancements.triggers

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class PlayerGeneratesContainerLoot(
	var lootTable: Argument.LootTable,
) : AdvancementTriggerCondition
