package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class CopyState(
	var block: Argument.Block,
	var properties: List<String> = emptyList(),
) : ItemFunction()

fun ItemModifier.copyState(block: Argument.Block, properties: List<String> = emptyList(), init: CopyState.() -> Unit = {}) {
	modifiers += CopyState(block, properties).apply(init)
}

fun ItemModifier.copyState(block: Argument.Block, vararg properties: String, init: CopyState.() -> Unit = {}) {
	modifiers += CopyState(block, properties.toList()).apply(init)
}
