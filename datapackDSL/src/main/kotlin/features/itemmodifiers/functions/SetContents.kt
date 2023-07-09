package features.itemmodifiers.functions

import arguments.Argument
import features.loottables.entries.Item
import kotlinx.serialization.Serializable

@Serializable
data class SetContents(
	var type: Argument.BlockEntityType,
	var entries: List<Item>,
) : ItemFunctionSurrogate
