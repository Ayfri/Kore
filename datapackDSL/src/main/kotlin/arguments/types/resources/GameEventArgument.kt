package arguments.types.resources

import arguments.Argument
import arguments.types.GameEventOrTagArgument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface GameEventArgument : ResourceLocationArgument, GameEventOrTagArgument {
	companion object {
		operator fun invoke(gameEvent: String, namespace: String = "minecraft") = object : GameEventArgument {
			override val name = gameEvent
			override val namespace = namespace
		}
	}
}
