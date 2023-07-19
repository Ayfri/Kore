package features.itemmodifiers.functions

import arguments.types.resources.BlockEntityTypeArgument
import features.itemmodifiers.ItemModifier
import features.loottables.entries.Item
import kotlinx.serialization.Serializable

@Serializable
data class SetContents(
	var type: BlockEntityTypeArgument,
	var entries: List<Item>,
) : ItemFunction()

fun ItemModifier.setContents(type: BlockEntityTypeArgument, entries: MutableList<Item>.() -> Unit = {}) =
	SetContents(type, buildList(entries)).also { modifiers += it }
