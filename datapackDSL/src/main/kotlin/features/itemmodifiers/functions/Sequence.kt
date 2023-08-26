package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import features.itemmodifiers.ItemModifierAsList
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class Sequence(
	override var conditions: PredicateAsList? = null,
	val functions: ItemModifierAsList? = null,
) : ItemFunction()

fun ItemModifier.sequence(block: ItemModifier.() -> Unit = {}) {
	modifiers += Sequence(functions = ItemModifier().apply(block))
}
