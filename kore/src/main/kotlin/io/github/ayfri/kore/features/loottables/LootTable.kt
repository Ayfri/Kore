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

fun DataPack.lootTable(fileName: String = "loot_table", init: LootTable.() -> Unit = {}): LootTableArgument {
	val lootTable = LootTable(fileName).apply(init)
	lootTables += lootTable
	return LootTableArgument(fileName, lootTable.namespace ?: name)
}

fun LootTable.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}

fun LootTable.pool(rolls: NumberProvider = constant(1f), block: LootPool.() -> Unit = {}) {
	pools = (pools ?: emptyList()) + LootPool(rolls).apply(block)
}
