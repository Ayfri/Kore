package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.Serializable

@Serializable
data class PlayerGeneratesContainerLoot(
	override var name: String,
	override var conditions: EntityOrPredicates? = null,
	var lootTable: LootTableArgument,
) : AdvancementTriggerCondition()

fun AdvancementCriteria.playerGeneratesContainerLoot(
	name: String,
	lootTable: LootTableArgument,
	block: PlayerGeneratesContainerLoot.() -> Unit,
) {
	criteria += PlayerGeneratesContainerLoot(name, lootTable = lootTable).apply(block)
}
