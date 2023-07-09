package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data class FillPlayerHead(
	val entity: Source,
) : ItemFunctionSurrogate

fun ItemModifierEntry.fillPlayerHead(entity: Source) {
	function = FillPlayerHead(entity)
}
