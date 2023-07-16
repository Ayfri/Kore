package arguments.types.resources

import arguments.Argument
import arguments.types.ResourceLocationArgument
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
