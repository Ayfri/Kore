package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.AttributeArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttributeModifier(
	val type: AttributeArgument,
	val slot: EquipmentSlot? = null,
	val uuid: UUIDArgument,
	val name: String,
	val amount: Double,
	val operation: AttributeModifierOperation,
)

@Serializable
data class AttributeModifiersComponent(
	val modifiers: MutableList<AttributeModifier>,
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component()

fun Components.attributeModifiers(
	modifiers: List<AttributeModifier>,
	showInTooltip: Boolean? = null,
) = apply { components["attribute_modifiers"] = AttributeModifiersComponent(modifiers.toMutableList(), showInTooltip) }

fun Components.attributeModifiers(
	vararg modifiers: AttributeModifier,
	showInTooltip: Boolean? = null,
) = attributeModifiers(modifiers.toList(), showInTooltip)

fun Components.attributeModifiers(modifiers: AttributeModifiersComponent.() -> Unit) =
	AttributeModifiersComponent(mutableListOf()).apply(modifiers).let { components["attribute_modifiers"] = it }

fun AttributeModifiersComponent.modifier(
	type: AttributeArgument,
	slot: EquipmentSlot? = null,
	uuid: UUIDArgument,
	name: String,
	amount: Double,
	operation: AttributeModifierOperation,
) = apply { modifiers += AttributeModifier(type, slot, uuid, name, amount, operation) }
