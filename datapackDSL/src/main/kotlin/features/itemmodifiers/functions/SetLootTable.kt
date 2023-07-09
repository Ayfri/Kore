package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data class SetLootTable(
	var type: Argument.BlockEntityType,
	var name: String,
	var seed: Int? = null,
) : ItemFunctionSurrogate

fun ItemModifierEntry.setLootTable(type: Argument.BlockEntityType, name: String, seed: Int? = null) {
	function = SetLootTable(type, name, seed)
}

fun ItemModifierEntry.setLootTable(type: Argument.BlockEntityType, name: Argument.LootTable, seed: Int? = null) {
	function = SetLootTable(type, name.asString(), seed)
}
