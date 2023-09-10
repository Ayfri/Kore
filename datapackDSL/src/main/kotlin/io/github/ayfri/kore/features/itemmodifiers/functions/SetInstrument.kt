package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.resources.tagged.InstrumentTagArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetInstrument(
	override var conditions: PredicateAsList? = null,
	val options: InstrumentTagArgument,
) : ItemFunction()

fun ItemModifier.setInstrument(instrument: InstrumentTagArgument, block: SetInstrument.() -> Unit = {}) {
	modifiers += SetInstrument(options = instrument).apply(block)
}
