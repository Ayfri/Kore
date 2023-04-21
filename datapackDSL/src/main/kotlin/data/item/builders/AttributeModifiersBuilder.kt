package data.item.builders

import arguments.Argument
import commands.AttributeModifierOperation
import data.item.AttributeModifier
import data.item.EquipmentSlot

class AttributeModifiersBuilder {
	val modifiers = mutableSetOf<AttributeModifier>()

	fun modifier(
		attribute: Argument.Attribute,
		amount: Double,
		operation: AttributeModifierOperation,
		slot: EquipmentSlot? = null,
		uuid: Argument.UUID? = null
	) {
		modifiers += AttributeModifier(attribute, amount, operation, slot, uuid)
	}

	fun modifier(
		attribute: String,
		amount: Double,
		operation: AttributeModifierOperation,
		slot: EquipmentSlot? = null,
		uuid: Argument.UUID? = null
	) {
		modifier(Argument.Attribute(attribute), amount, operation, slot, uuid)
	}

	fun modifier(block: AttributeModifierBuilder.() -> Unit) {
		modifiers += AttributeModifierBuilder().apply(block).build()
	}

	operator fun Argument.Attribute.invoke(
		amount: Double,
		operation: AttributeModifierOperation,
		slot: EquipmentSlot? = null,
		uuid: Argument.UUID? = null
	) = modifier(this, amount, operation, slot, uuid)
}

class AttributeModifierBuilder {
	var attribute: Argument.Attribute? = null
	var amount: Double? = null
	var operation: AttributeModifierOperation? = null
	var slot: EquipmentSlot? = null
	var uuid: Argument.UUID? = null

	fun build() = AttributeModifier(
		attribute ?: error("Attribute not set"),
		amount ?: error("Amount not set"),
		operation ?: error("Operation not set"),
		slot,
		uuid,
	)
}
