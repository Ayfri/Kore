package features.itemmodifiers.functions

import arguments.types.resources.BlockEntityTypeArgument
import arguments.types.resources.LootTableArgument
import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetLootTable(
	override var conditions: PredicateAsList? = null,
	var type: BlockEntityTypeArgument,
	var name: String,
	var seed: Int? = null,
) : ItemFunction()

fun ItemModifier.setLootTable(type: BlockEntityTypeArgument, name: String, seed: Int? = null, block: SetLootTable.() -> Unit = {}) {
	modifiers += SetLootTable(type = type, name = name, seed = seed).apply(block)
}

fun ItemModifier.setLootTable(
	type: BlockEntityTypeArgument,
	name: LootTableArgument,
	seed: Int? = null,
	block: SetLootTable.() -> Unit = {},
) {
	modifiers += SetLootTable(type = type, name = name.asString(), seed = seed).apply(block)
}
