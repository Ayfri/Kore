package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.features.predicates.providers.utils.toUniformProvider
import kotlinx.serialization.Serializable

/**
 * Sets or adds to the damage value of a damageable item. Mirrors `minecraft:set_damage`.
 * Accepts a context float provider for dynamic damage values.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetDamage(
	override var condition: PredicateCondition? = null,
	var damage: ContextFloatProvider,
	var add: Boolean? = null,
) : ItemFunction()

/** Add a `set_damage` step with a context float provider. */
fun ItemModifier.setDamage(damage: ContextFloatProvider, add: Boolean? = null, block: SetDamage.() -> Unit = {}) {
	modifiers += SetDamage(damage = damage, add = add).apply(block)
}

/** Float convenience overload for `set_damage`. */
fun ItemModifier.setDamage(damage: Float, add: Boolean? = null, block: SetDamage.() -> Unit = {}) {
	modifiers += SetDamage(damage = constant(damage), add = add).apply(block)
}

/**
 * Add a `set_damage` step drawing the damage uniformly from [damage], the game never returning the end itself.
 *
 * ```
 * setDamage(0.2f..0.8f)  // "damage": {"type": "minecraft:uniform", "max": 0.8, "min": 0.2}
 * ```
 */
fun ItemModifier.setDamage(damage: ClosedFloatingPointRange<Float>, add: Boolean? = null, block: SetDamage.() -> Unit = {}) =
	setDamage(damage.toUniformProvider(), add, block)
