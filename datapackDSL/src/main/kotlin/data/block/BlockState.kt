package data.block

import kotlinx.serialization.Serializable

@Serializable
data class BlockState(
	val name: String,
	val properties: Map<String, String>,
)
