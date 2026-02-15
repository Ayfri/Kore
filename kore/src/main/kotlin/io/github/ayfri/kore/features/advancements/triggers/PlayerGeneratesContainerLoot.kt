package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.Serializable

/**
 * Triggered when a player generates loot from a container.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#playergeneratescontainerloot
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class PlayerGeneratesContainerLoot(
	override var player: EntityOrPredicates? = null,
	var lootTable: LootTableArgument,
) : AdvancementTriggerCondition()

/** Add a `playerGeneratesContainerLoot` criterion, triggered when a player generates loot from a container. */
fun AdvancementCriteria.playerGeneratesContainerLoot(
	name: String,
	lootTable: LootTableArgument,
	block: PlayerGeneratesContainerLoot.() -> Unit,
) {
	criteria[name] = PlayerGeneratesContainerLoot(lootTable = lootTable).apply(block)
}
