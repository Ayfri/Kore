package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data class SetInstrument(
	val instrument: Argument.InstrumentTag
) : ItemFunctionSurrogate

fun ItemModifierEntry.setInstrument(instrument: Argument.InstrumentTag) {
	function = SetInstrument(instrument)
}
