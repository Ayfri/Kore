package io.github.ayfri.kore.features.loottables

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.RandomSequenceArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven loot table definition.
 *
 * Loot tables control what items are generated in contexts like chests, entities, fishing,
 * and block drops. Configure pools, entries and conditions, and optionally apply global item
 * modifier functions to post-process the generated items.
 *
 * Docs: https://kore.ayfri.com/docs/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class LootTable(
	@Transient
	override var fileName: String = "loot_table",
	var functions: ItemModifierAsList? = null,
	var pools: List<LootPool>? = null,
	var randomSequence: RandomSequenceArgument? = null,
	var type: LootTableType? = null,
) : Generator("loot_table") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a loot table using a builder block.
 *
 * Produces `data/<namespace>/loot_table/<fileName>.json`. Use the DSL to add pools and
 * table-wide item modifier functions.
 */
fun DataPack.lootTable(fileName: String = "loot_table", init: LootTable.() -> Unit = {}): LootTableArgument {
	val lootTable = LootTable(fileName).apply(init)
	lootTables += lootTable
	return LootTableArgument(fileName, lootTable.namespace ?: name)
}

/**
 * Define global item modifier functions applied to the whole loot table.
 *
 * Docs: https://kore.ayfri.com/docs/loot-tables
 */
fun LootTable.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}

/**
 * Add a loot pool to this table.
 *
 * Docs: https://kore.ayfri.com/docs/loot-tables
 */
fun LootTable.pool(rolls: NumberProvider = constant(1f), block: LootPool.() -> Unit = {}) {
	pools = (pools ?: emptyList()) + LootPool(rolls).apply(block)
}
