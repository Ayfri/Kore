package io.github.ayfri.kore.data.block

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockState(
	@JsonSerialName("Name")
	@SerialName("Name")
	var name: BlockArgument,
	@JsonSerialName("Properties")
	@SerialName("Properties")
	var properties: Map<String, String>? = null,
) {
	init {
		val states = name.states
		if (states.isNotEmpty()) {
			properties = states.toMap()
			name.states.clear()
		}
	}
}
