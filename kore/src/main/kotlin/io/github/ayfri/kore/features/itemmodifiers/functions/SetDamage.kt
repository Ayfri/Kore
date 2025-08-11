package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import kotlinx.serialization.Serializable

/**
 * Sets or adds to the damage value of a damageable item. Mirrors `minecraft:set_damage`.
 * Accepts a number provider for dynamic damage values.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetDamage(
	override var conditions: PredicateAsList? = null,
	var damage: NumberProvider,
	var add: Boolean? = null,
) : ItemFunction()

/** Add a `set_damage` step with a number provider. */
fun ItemModifier.setDamage(damage: NumberProvider, add: Boolean? = null, block: SetDamage.() -> Unit = {}) {
	modifiers += SetDamage(damage = damage, add = add).apply(block)
}

/** Float convenience overload for `set_damage`. */
fun ItemModifier.setDamage(damage: Float, add: Boolean? = null, block: SetDamage.() -> Unit = {}) {
	modifiers += SetDamage(damage = constant(damage), add = add).apply(block)
}
