package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Sets the amplifier for ominous bottles. Mirrors `minecraft:set_ominous_bottle_amplifier`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetOminousBottleAmplifier(
	override var conditions: PredicateAsList? = null,
	var amplifier: Int,
) : ItemFunction()

/** Add a `set_ominous_bottle_amplifier` step. */
fun ItemModifier.setOminousBottleAmplifier(amplifier: Int, block: SetOminousBottleAmplifier.() -> Unit = {}) {
	modifiers += SetOminousBottleAmplifier(amplifier = amplifier).apply(block)
}
