package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.InstrumentOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface InstrumentTagArgument : TaggedResourceLocationArgument, InstrumentOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : InstrumentTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
