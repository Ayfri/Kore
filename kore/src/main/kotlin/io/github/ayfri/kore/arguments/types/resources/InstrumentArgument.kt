package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.InstrumentOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
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
