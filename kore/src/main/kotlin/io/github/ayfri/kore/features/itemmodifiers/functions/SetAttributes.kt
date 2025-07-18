package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.data.item.AttributeModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.generated.arguments.types.AttributeArgument
import kotlinx.serialization.Serializable

@Serializable
data class SetAttributes(
	override var conditions: PredicateAsList? = null,
	val modifiers: MutableList<AttributeModifier> = mutableListOf(),
	var replace: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setAttributes(
	modifiers: MutableList<AttributeModifier> = mutableListOf(),
	replace: Boolean? = null,
	block: SetAttributes.() -> Unit = {},
) =
	SetAttributes(modifiers = modifiers, replace = replace).apply(block).also { this.modifiers += it }

fun ItemModifier.setAttributes(modifier: AttributeModifier, replace: Boolean? = null, block: SetAttributes.() -> Unit = {}) =
	setAttributes(mutableListOf(modifier), replace, block)

fun SetAttributes.attribute(
	attribute: AttributeArgument,
	amount: NumberProvider = constant(0f),
	operation: AttributeModifierOperation = AttributeModifierOperation.ADD_VALUE,
	slot: List<EquipmentSlot>? = null,
	uuid: UUIDArgument? = null,
	block: AttributeModifier.() -> Unit = {},
) = apply {
	modifiers += AttributeModifier(attribute, amount, operation, slot, uuid).apply(block)
}

fun SetAttributes.attribute(
	attribute: AttributeArgument,
	amount: Float,
	operation: AttributeModifierOperation = AttributeModifierOperation.ADD_VALUE,
	slot: List<EquipmentSlot>? = null,
	uuid: UUIDArgument? = null,
	block: AttributeModifier.() -> Unit = {},
) = attribute(attribute, constant(amount), operation, slot, uuid, block)
