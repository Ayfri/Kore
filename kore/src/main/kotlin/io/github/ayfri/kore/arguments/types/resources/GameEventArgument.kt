package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.GameEventOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
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
