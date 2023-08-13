package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import features.predicates.providers.NumberProvider
import features.predicates.providers.constant
import kotlinx.serialization.Serializable

@Serializable
data class SetDamage(
	override var conditions: PredicateAsList? = null,
	var damage: NumberProvider,
	var add: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setDamage(damage: NumberProvider, add: Boolean? = null, block: SetDamage.() -> Unit = {}) {
	modifiers += SetDamage(damage = damage, add = add).apply(block)
}

fun ItemModifier.setDamage(damage: Float, add: Boolean? = null, block: SetDamage.() -> Unit = {}) {
	modifiers += SetDamage(damage = constant(damage), add = add).apply(block)
}
