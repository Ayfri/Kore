package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ChatTypeArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(chatType: String, namespace: String = "minecraft") = object : ChatTypeArgument {
			override val name = chatType
			override val namespace = namespace
		}
	}
}
