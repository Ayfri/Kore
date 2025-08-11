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
 * Defines a Minecraft loot table and how it is serialized by Kore.
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
 * Create and register a new loot table in this [DataPack].
 *
 * Docs: https://kore.ayfri.com/docs/loot-tables
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
