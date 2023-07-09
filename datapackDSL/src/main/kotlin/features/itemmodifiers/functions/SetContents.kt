package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifierEntry
import features.loottables.entries.Item
import kotlinx.serialization.Serializable

@Serializable
data class SetContents(
	var type: Argument.BlockEntityType,
	var entries: List<Item>,
) : ItemFunctionSurrogate

fun ItemModifierEntry.setContents(type: Argument.BlockEntityType, entries: MutableList<Item>.() -> Unit = {}) {
	function = SetContents(type, buildList(entries))
}
