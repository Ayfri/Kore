package features.itemmodifiers.functions

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class SetLootTable(
	var type: Argument.BlockEntityType,
	var name: String,
	var seed: Int? = null,
) : ItemFunctionSurrogate
