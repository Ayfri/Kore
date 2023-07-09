package features.itemmodifiers.functions

import kotlinx.serialization.Serializable

@Serializable
data class Reference(
	val name: String
) : ItemFunctionSurrogate
