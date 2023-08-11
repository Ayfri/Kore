package features.loottables

import DataPack
import Generator
import arguments.types.resources.LootTableArgument
import arguments.types.resources.RandomSequenceArgument
import features.itemmodifiers.ItemModifier
import features.itemmodifiers.ItemModifierAsList
import features.predicates.providers.NumberProvider
import features.predicates.providers.constant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class LootTable(
	@Transient
	override var fileName: String = "loot_table",
	var functions: ItemModifierAsList? = null,
	var pools: List<LootPool>? = null,
	var randomSequence: RandomSequenceArgument? = null,
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.lootTable(fileName: String, block: LootTable.() -> Unit = {}): LootTableArgument {
	val lootTable = LootTable(fileName).apply(block)
	lootTables += lootTable
	return LootTableArgument(fileName, name)
}

fun LootTable.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}

fun LootTable.pool(rolls: NumberProvider = constant(1f), block: LootPool.() -> Unit = {}) {
	pools = (pools ?: emptyList()) + LootPool(rolls).apply(block)
}
