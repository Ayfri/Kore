package features.itemmodifiers.functions

import arguments.types.resources.tagged.InstrumentTagArgument
import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetInstrument(
	override var conditions: PredicateAsList? = null,
	val options: InstrumentTagArgument,
) : ItemFunction()

fun ItemModifier.setInstrument(instrument: InstrumentTagArgument, block: SetInstrument.() -> Unit = {}) {
	modifiers += SetInstrument(options = instrument).apply(block)
}
