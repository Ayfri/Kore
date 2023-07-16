package data.block

import arguments.types.resources.BlockArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockState(
	@SerialName("Name")
	val name: BlockArgument,
	@SerialName("Properties")
	val properties: Map<String, String>? = null,
)
