package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.generated.arguments.LootTableOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Triggered when a player generates loot from a container with one of the [lootTables] set, loot table IDs or loot table tags.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#playergeneratescontainerloot
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class PlayerGeneratesContainerLoot(
	override var player: EntityOrPredicates? = null,
	var lootTables: InlinableList<LootTableOrTagArgument>,
) : AdvancementTriggerCondition()

/** Add a `playerGeneratesContainerLoot` criterion, triggered when a player generates loot from a container set with one of [lootTables]. */
fun AdvancementCriteria.playerGeneratesContainerLoot(
	name: String,
	vararg lootTables: LootTableOrTagArgument,
	block: PlayerGeneratesContainerLoot.() -> Unit,
) {
	criteria[name] = PlayerGeneratesContainerLoot(lootTables = lootTables.toList()).apply(block)
}
