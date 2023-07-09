package features.itemmodifiers.functions

import data.item.AttributeModifier
import kotlinx.serialization.Serializable

@Serializable
data class SetAttributes(
	val modifiers: List<AttributeModifier>
) : ItemFunctionSurrogate
