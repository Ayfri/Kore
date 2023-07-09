package features.itemmodifiers.functions

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class CopyState(
	var block: Argument.Block,
	var properties: List<String>
) : ItemFunctionSurrogate
