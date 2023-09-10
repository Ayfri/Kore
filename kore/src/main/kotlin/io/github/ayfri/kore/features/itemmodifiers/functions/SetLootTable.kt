package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.resources.BlockEntityTypeArgument
import io.github.ayfri.kore.arguments.types.resources.LootTableArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
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
	modifiers += SetLootTable(type = type, name = name.asString(), seed = seed)
		.apply(block)
}
