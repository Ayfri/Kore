package features.advancements.triggers

import arguments.types.resources.LootTableArgument
import kotlinx.serialization.Serializable

@Serializable
data class PlayerGeneratesContainerLoot(
	var lootTable: LootTableArgument,
) : AdvancementTriggerCondition
