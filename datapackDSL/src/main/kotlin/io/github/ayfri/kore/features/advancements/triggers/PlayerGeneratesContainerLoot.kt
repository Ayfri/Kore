package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.types.resources.LootTableArgument
import kotlinx.serialization.Serializable

@Serializable
data class PlayerGeneratesContainerLoot(
	var lootTable: LootTableArgument,
) : AdvancementTriggerCondition
