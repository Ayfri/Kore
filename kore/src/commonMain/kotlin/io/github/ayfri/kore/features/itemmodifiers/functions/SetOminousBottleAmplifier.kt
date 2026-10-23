package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.ConstantContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import kotlinx.serialization.Serializable

/**
 * Sets the amplifier for ominous bottles. Mirrors `minecraft:set_ominous_bottle_amplifier`.
 *
 * [amplifier] is a context int provider, so it can be drawn from a range or read from a context int provider ID.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetOminousBottleAmplifier(
	var amplifier: ContextIntProvider,
	override var condition: PredicateCondition? = null,
) : ItemFunction()

/**
 * Add a `set_ominous_bottle_amplifier` step drawing the amplifier from [amplifier].
 *
 * ```
 * setOminousBottleAmplifier(uniform(2, 4)) // "amplifier": { "type": "minecraft:uniform", "max": 4, "min": 2 }
 * ```
 */
fun ItemModifier.setOminousBottleAmplifier(amplifier: ContextIntProvider, block: SetOminousBottleAmplifier.() -> Unit = {}) {
	modifiers += SetOminousBottleAmplifier(amplifier = amplifier).apply(block)
}

/**
 * Add a `set_ominous_bottle_amplifier` step with a fixed [amplifier], written as a constant context int provider.
 *
 * ```
 * setOminousBottleAmplifier(5) // "amplifier": 5
 * ```
 */
fun ItemModifier.setOminousBottleAmplifier(amplifier: Int, block: SetOminousBottleAmplifier.() -> Unit = {}) =
	setOminousBottleAmplifier(ConstantContextIntProvider(amplifier), block)
