package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data class CopyState(
	var block: Argument.Block,
	var properties: List<String> = emptyList(),
) : ItemFunctionSurrogate

fun ItemModifierEntry.copyState(block: Argument.Block, properties: List<String> = emptyList()) {
	function = CopyState(block, properties)
}

fun ItemModifierEntry.copyState(block: Argument.Block, vararg properties: String) {
	function = CopyState(block, properties.toList())
}
