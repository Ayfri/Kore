package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.types.resources.AttributeModifierArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.AttributeArgument
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttributeModifier(
	val type: AttributeArgument,
	val slot: EquipmentSlot? = null,
	val id: AttributeModifierArgument,
	val amount: Double,
	val operation: AttributeModifierOperation,
)

@Serializable(with = AttributeModifiersComponent.Companion.AttributeModifiersComponentSerializer::class)
data class AttributeModifiersComponent(
	val modifiers: MutableList<AttributeModifier>,
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean? = null,
) : Component() {
	companion object {
		data object AttributeModifiersComponentSerializer : SinglePropertySimplifierSerializer<AttributeModifiersComponent, List<AttributeModifier>>(
			AttributeModifiersComponent::class,
			AttributeModifiersComponent::modifiers,
		)
	}
}

fun ComponentsScope.attributeModifiers(
	modifiers: List<AttributeModifier>,
	showInTooltip: Boolean? = null,
) = apply { this[ItemComponentTypes.ATTRIBUTE_MODIFIERS] = AttributeModifiersComponent(modifiers.toMutableList(), showInTooltip) }

fun ComponentsScope.attributeModifiers(
	vararg modifiers: AttributeModifier,
	showInTooltip: Boolean? = null,
) = attributeModifiers(modifiers.toList(), showInTooltip)

fun ComponentsScope.attributeModifiers(modifiers: AttributeModifiersComponent.() -> Unit) =
	AttributeModifiersComponent(mutableListOf()).apply(modifiers).let { this[ItemComponentTypes.ATTRIBUTE_MODIFIERS] = it }

fun AttributeModifiersComponent.modifier(
	type: AttributeArgument,
	slot: EquipmentSlot? = null,
	id: AttributeModifierArgument,
	amount: Double,
	operation: AttributeModifierOperation,
) = apply { modifiers += AttributeModifier(type, slot, id, amount, operation) }

fun AttributeModifiersComponent.modifier(
	type: AttributeArgument,
	slot: EquipmentSlot? = null,
	name: String,
	namespace: String = "minecraft",
	amount: Double,
	operation: AttributeModifierOperation,
) = apply { modifiers += AttributeModifier(type, slot, AttributeModifierArgument(name, namespace), amount, operation) }
