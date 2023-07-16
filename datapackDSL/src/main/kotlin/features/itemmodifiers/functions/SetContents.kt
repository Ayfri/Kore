package features.itemmodifiers.functions

import arguments.types.resources.BlockEntityType
import features.itemmodifiers.ItemModifier
import features.loottables.entries.Item
import kotlinx.serialization.Serializable

@Serializable
data class SetContents(
	var type: BlockEntityType,
	var entries: List<Item>,
) : ItemFunction()

fun ItemModifier.setContents(type: BlockEntityType, entries: MutableList<Item>.() -> Unit = {}) =
	SetContents(type, buildList(entries)).also { modifiers += it }
