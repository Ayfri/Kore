package data.block

import arguments.types.resources.BlockArgument
import serializers.JsonSerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockState(
	@JsonSerialName("Name")
	val name: BlockArgument,
	@JsonSerialName("Properties")
	val properties: Map<String, String>? = null,
)
