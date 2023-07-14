package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class SetLootTable(
	var type: Argument.BlockEntityType,
	var name: String,
	var seed: Int? = null,
) : ItemFunction()

fun ItemModifier.setLootTable(type: Argument.BlockEntityType, name: String, seed: Int? = null, block: SetLootTable.() -> Unit = {}) {
	modifiers += SetLootTable(type, name, seed).apply(block)
}

fun ItemModifier.setLootTable(
	type: Argument.BlockEntityType,
	name: Argument.LootTable,
	seed: Int? = null,
	block: SetLootTable.() -> Unit = {}
) {
	modifiers += SetLootTable(type, name.asString(), seed).apply(block)
}
