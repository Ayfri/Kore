package features.itemmodifiers.functions

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class SetInstrument(
	val instrument: Argument.InstrumentTag
) : ItemFunctionSurrogate
