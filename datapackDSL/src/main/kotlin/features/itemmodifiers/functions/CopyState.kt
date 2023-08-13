package features.itemmodifiers.functions

import arguments.types.resources.BlockArgument
import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class CopyState(
	override var conditions: PredicateAsList? = null,
	var block: BlockArgument,
	var properties: List<String> = emptyList(),
) : ItemFunction()

fun ItemModifier.copyState(block: BlockArgument, properties: List<String> = emptyList(), init: CopyState.() -> Unit = {}) {
	modifiers += CopyState(block = block, properties = properties).apply(init)
}

fun ItemModifier.copyState(block: BlockArgument, vararg properties: String, init: CopyState.() -> Unit = {}) {
	modifiers += CopyState(block = block, properties = properties.toList()).apply(init)
}
