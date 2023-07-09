package features.itemmodifiers.functions

import kotlinx.serialization.Serializable

@Serializable
data class FillPlayerHead(
	val entity: Source,
) : ItemFunctionSurrogate
