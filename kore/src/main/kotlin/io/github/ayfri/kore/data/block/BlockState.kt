package io.github.ayfri.kore.data.block

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockState(
	@JsonSerialName("Name")
	val name: BlockArgument,
	@JsonSerialName("Properties")
	val properties: Map<String, String>? = null,
)
