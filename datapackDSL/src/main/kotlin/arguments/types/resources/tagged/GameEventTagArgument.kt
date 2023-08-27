package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.GameEventOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface GameEventTagArgument : TaggedResourceLocationArgument, GameEventOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : GameEventTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
