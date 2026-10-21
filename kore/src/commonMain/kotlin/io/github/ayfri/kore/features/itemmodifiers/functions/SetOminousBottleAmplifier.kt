package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.ConstantNumberProvider
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import kotlinx.serialization.Serializable

/**
 * Sets the amplifier for ominous bottles. Mirrors `minecraft:set_ominous_bottle_amplifier`.
 *
 * [amplifier] is a number provider, so it can be drawn from a range or read from a number provider ID.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetOminousBottleAmplifier(
	var amplifier: NumberProvider,
	override var condition: PredicateCondition? = null,
) : ItemFunction()

/**
 * Add a `set_ominous_bottle_amplifier` step drawing the amplifier from [amplifier].
 *
 * ```
 * setOminousBottleAmplifier(uniform(2f, 4f)) // "amplifier": { "type": "minecraft:uniform", "min": 2.0, "max": 4.0 }
 * ```
 */
fun ItemModifier.setOminousBottleAmplifier(amplifier: NumberProvider, block: SetOminousBottleAmplifier.() -> Unit = {}) {
	modifiers += SetOminousBottleAmplifier(amplifier = amplifier).apply(block)
}

/**
 * Add a `set_ominous_bottle_amplifier` step with a fixed [amplifier], written as a constant number provider.
 *
 * ```
 * setOminousBottleAmplifier(5) // "amplifier": 5.0
 * ```
 */
fun ItemModifier.setOminousBottleAmplifier(amplifier: Int, block: SetOminousBottleAmplifier.() -> Unit = {}) =
	setOminousBottleAmplifier(ConstantNumberProvider(amplifier.toFloat()), block)
