package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class SetInstrument(
	val instrument: Argument.InstrumentTag
) : ItemFunction()

fun ItemModifier.setInstrument(instrument: Argument.InstrumentTag, block: SetInstrument.() -> Unit = {}) {
	modifiers += SetInstrument(instrument).apply(block)
}
