package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class FillPlayerHead(
	val entity: Source,
) : ItemFunction()

fun ItemModifier.fillPlayerHead(entity: Source, block: FillPlayerHead.() -> Unit = {}) {
	modifiers += FillPlayerHead(entity).apply(block)
}
