package features.itemmodifiers.functions

import kotlinx.serialization.Serializable

@Serializable
data class SetLore(
	var entity: Source? = null,
	var lore: List<String> = emptyList(),
	var replace: Boolean? = null,
) : ItemFunctionSurrogate
