package features.itemmodifiers.functions

import arguments.types.resources.tagged.InstrumentTagArgument
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class SetInstrument(
	val instrument: InstrumentTagArgument
) : ItemFunction()

fun ItemModifier.setInstrument(instrument: InstrumentTagArgument, block: SetInstrument.() -> Unit = {}) {
	modifiers += SetInstrument(instrument).apply(block)
}
