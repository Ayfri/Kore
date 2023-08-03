package pack

import DataPack
import arguments.chatcomponents.ChatComponents
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a pack that contains game data and resources.
 *
 * @property format The format of the pack.
 * @property description The description of the pack.
 */
@Serializable
data class Pack(
	@SerialName("pack_format")
	var format: Int,
	@Serializable
	var description: ChatComponents,
)

fun DataPack.pack(block: Pack.() -> Unit) = pack.run(block)
