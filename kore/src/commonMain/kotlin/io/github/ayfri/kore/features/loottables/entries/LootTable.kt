package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.generated.arguments.LootTableOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Loot entry that includes other loot tables, given by ID, list of IDs or loot table tag in [value].
 *
 * With [expand] `true` the game picks one of the loot tables at random, otherwise it drops all of them.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class LootTable(
	override var condition: PredicateCondition? = null,
	var expand: Boolean? = null,
	override var modifier: InlinableItemModifier? = null,
	var quality: Int? = null,
	var value: InlinableList<LootTableOrTagArgument>,
	var weight: Int? = null,
) : LootEntry()

/**
 * Add and configure a nested loot table entry holding the loot tables or loot table tags [value].
 *
 * ```
 * lootTable(LootTables.Gameplay.PIGLIN_BARTERING)  // "value": "minecraft:gameplay/piglin_bartering"
 * lootTable(LootTables.Equipment.TRIAL_CHAMBER, LootTables.Equipment.TRIAL_CHAMBER_MELEE, expand = true)  // one of the two
 * ```
 */
fun LootEntries.lootTable(vararg value: LootTableOrTagArgument, expand: Boolean? = null, block: LootTable.() -> Unit = {}) {
	add(LootTable(expand = expand, value = value.toList()).apply(block))
}
