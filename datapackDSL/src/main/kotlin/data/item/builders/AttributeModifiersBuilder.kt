package data.item.builders

import arguments.Argument
import commands.AttributeModifierOperation
import data.item.AttributeModifier
import data.item.EquipmentSlot
import features.predicates.providers.NumberProvider

class AttributeModifiersBuilder {
	val modifiers = mutableSetOf<AttributeModifier>()

	fun modifier(
		attribute: Argument.Attribute,
		amount: NumberProvider,
		operation: AttributeModifierOperation,
		slot: List<EquipmentSlot>? = null,
		uuid: Argument.UUID? = null
	) {
		modifiers += AttributeModifier(attribute, amount, operation, slot, uuid)
	}

	fun modifier(
		attribute: String,
		amount: NumberProvider,
		operation: AttributeModifierOperation,
		slot: List<EquipmentSlot>? = null,
		uuid: Argument.UUID? = null
	) {
		modifier(Argument.Attribute(attribute), amount, operation, slot, uuid)
	}

	fun modifier(block: AttributeModifierBuilder.() -> Unit) {
		modifiers += AttributeModifierBuilder().apply(block).build()
	}

	operator fun Argument.Attribute.invoke(
		amount: NumberProvider,
		operation: AttributeModifierOperation,
		slot: List<EquipmentSlot>? = null,
		uuid: Argument.UUID? = null
	) = modifier(this, amount, operation, slot, uuid)
}

class AttributeModifierBuilder {
	var attribute: Argument.Attribute? = null
	var amount: NumberProvider? = null
	var operation: AttributeModifierOperation? = null
	var slot: List<EquipmentSlot>? = null
	var uuid: Argument.UUID? = null

	fun build() = AttributeModifier(
		attribute ?: error("Attribute not set"),
		amount ?: error("Amount not set"),
		operation ?: error("Operation not set"),
		slot,
		uuid,
	)
}
