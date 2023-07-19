package features.itemmodifiers.functions

import arguments.types.resources.BlockEntityTypeArgument
import arguments.types.resources.LootTableArgument
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class SetLootTable(
	var type: BlockEntityTypeArgument,
	var name: String,
	var seed: Int? = null,
) : ItemFunction()

fun ItemModifier.setLootTable(type: BlockEntityTypeArgument, name: String, seed: Int? = null, block: SetLootTable.() -> Unit = {}) {
	modifiers += SetLootTable(type, name, seed).apply(block)
}

fun ItemModifier.setLootTable(
	type: BlockEntityTypeArgument,
	name: LootTableArgument,
	seed: Int? = null,
	block: SetLootTable.() -> Unit = {}
) {
	modifiers += SetLootTable(type, name.asString(), seed).apply(block)
}
