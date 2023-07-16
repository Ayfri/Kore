package arguments.types.resources

import arguments.Argument
import arguments.types.InstrumentOrTagArgument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface InstrumentArgument : ResourceLocationArgument, InstrumentOrTagArgument {
	companion object {
		operator fun invoke(instrument: String, namespace: String = "minecraft") = object : InstrumentArgument {
			override val name = instrument
			override val namespace = namespace
		}
	}
}
