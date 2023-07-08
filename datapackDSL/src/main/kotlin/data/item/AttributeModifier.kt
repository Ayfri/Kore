package data.item

import arguments.Argument
import commands.AttributeModifierOperation
import kotlinx.serialization.Serializable
import serializers.EnumOrdinalSerializer

@Serializable
data class AttributeModifier(
	val attribute: Argument.Attribute,
	val amount: Double,
	@Serializable(AttributeModifierOperationSerializer::class) val operation: AttributeModifierOperation,
	val slot: EquipmentSlot? = null,
	val uuid: Argument.UUID? = null,
) {
	companion object {
		object AttributeModifierOperationSerializer : EnumOrdinalSerializer<AttributeModifierOperation>(AttributeModifierOperation.entries)
	}
}
