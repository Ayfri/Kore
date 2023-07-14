package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
class ExplosionDecay : ItemFunction()

fun ItemModifier.explosionDecay(block: ExplosionDecay.() -> Unit = {}) {
	modifiers += ExplosionDecay().apply(block)
}
