package data.block

import arguments.Argument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockState(
	@SerialName("Name")
	val name: Argument.Block,
	@SerialName("Properties")
	val properties: Map<String, String>? = null,
)
