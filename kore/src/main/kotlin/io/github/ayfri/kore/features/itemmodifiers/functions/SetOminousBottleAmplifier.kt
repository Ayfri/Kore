package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetOminousBottleAmplifier(
	override var conditions: PredicateAsList? = null,
	var amplifier: Int,
) : ItemFunction()

fun ItemModifier.setOminousBottleAmplifier(amplifier: Int, block: SetOminousBottleAmplifier.() -> Unit = {}) {
	modifiers += SetOminousBottleAmplifier(amplifier = amplifier).apply(block)
}
