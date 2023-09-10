package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class ExplosionDecay(override var conditions: PredicateAsList? = null) : ItemFunction()

fun ItemModifier.explosionDecay(block: ExplosionDecay.() -> Unit = {}) {
	modifiers += ExplosionDecay().apply(block)
}
