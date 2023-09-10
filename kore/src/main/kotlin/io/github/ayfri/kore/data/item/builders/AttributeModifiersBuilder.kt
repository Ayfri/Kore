package io.github.ayfri.kore.data.item.builders

import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.AttributeArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.data.item.AttributeModifier
import io.github.ayfri.kore.data.item.EquipmentSlot
import io.github.ayfri.kore.features.predicates.providers.NumberProvider

class AttributeModifiersBuilder {
	val modifiers = mutableSetOf<AttributeModifier>()

	fun modifier(
		attribute: AttributeArgument,
		amount: NumberProvider,
		operation: AttributeModifierOperation,
		slot: List<EquipmentSlot>? = null,
		uuid: UUIDArgument? = null
	) {
		modifiers += AttributeModifier(attribute, amount, operation, slot, uuid)
	}

	fun modifier(
		attribute: String,
		amount: NumberProvider,
		operation: AttributeModifierOperation,
		slot: List<EquipmentSlot>? = null,
		uuid: UUIDArgument? = null
	) {
		modifier(AttributeArgument(attribute), amount, operation, slot, uuid)
	}

	fun modifier(block: AttributeModifierBuilder.() -> Unit) {
		modifiers += AttributeModifierBuilder().apply(block).build()
	}

	operator fun AttributeArgument.invoke(
		amount: NumberProvider,
		operation: AttributeModifierOperation,
		slot: List<EquipmentSlot>? = null,
		uuid: UUIDArgument? = null
	) = modifier(this, amount, operation, slot, uuid)
}

class AttributeModifierBuilder {
	var attribute: AttributeArgument? = null
	var amount: NumberProvider? = null
	var operation: AttributeModifierOperation? = null
	var slot: List<EquipmentSlot>? = null
	var uuid: UUIDArgument? = null

	fun build() = AttributeModifier(
		attribute ?: error("Attribute not set"),
		amount ?: error("Amount not set"),
		operation ?: error("Operation not set"),
		slot,
		uuid,
	)
}
