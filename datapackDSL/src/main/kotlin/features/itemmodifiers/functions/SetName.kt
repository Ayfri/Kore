package features.itemmodifiers.functions

import arguments.chatcomponents.ChatComponent
import kotlinx.serialization.Serializable

@Serializable
data class SetName(
	var entity: Source? = null,
	var name: ChatComponent,
) : ItemFunctionSurrogate
