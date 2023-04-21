package data.block

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockState(
	@SerialName("Name")
	val name: String,
	val properties: Map<String, String>,
)
