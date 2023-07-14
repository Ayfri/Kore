package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifier
import features.loottables.entries.Item
import kotlinx.serialization.Serializable

@Serializable
data class SetContents(
	var type: Argument.BlockEntityType,
	var entries: List<Item>,
) : ItemFunction()

fun ItemModifier.setContents(type: Argument.BlockEntityType, entries: MutableList<Item>.() -> Unit = {}) =
	SetContents(type, buildList(entries)).also { modifiers += it }
