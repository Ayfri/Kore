package features.itemmodifiers.functions

import arguments.types.resources.BlockEntityType
import arguments.types.resources.LootTableArgument
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class SetLootTable(
	var type: BlockEntityType,
	var name: String,
	var seed: Int? = null,
) : ItemFunction()

fun ItemModifier.setLootTable(type: BlockEntityType, name: String, seed: Int? = null, block: SetLootTable.() -> Unit = {}) {
	modifiers += SetLootTable(type, name, seed).apply(block)
}

fun ItemModifier.setLootTable(
	type: BlockEntityType,
	name: LootTableArgument,
	seed: Int? = null,
	block: SetLootTable.() -> Unit = {}
) {
	modifiers += SetLootTable(type, name.asString(), seed).apply(block)
}
