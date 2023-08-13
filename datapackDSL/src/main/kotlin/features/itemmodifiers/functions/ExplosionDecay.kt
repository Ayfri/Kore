package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class ExplosionDecay(override var conditions: PredicateAsList? = null) : ItemFunction()

fun ItemModifier.explosionDecay(block: ExplosionDecay.() -> Unit = {}) {
	modifiers += ExplosionDecay().apply(block)
}
