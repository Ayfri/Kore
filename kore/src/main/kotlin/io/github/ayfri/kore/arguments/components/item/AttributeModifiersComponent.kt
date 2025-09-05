package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.types.resources.AttributeModifierArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.AttributeArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable


@Serializable(with = AttributeModifierDisplay.Companion.AttributeModifierDisplaySerializer::class)
sealed class AttributeModifierDisplay {
	companion object {
		data object AttributeModifierDisplaySerializer : NamespacedPolymorphicSerializer<AttributeModifierDisplay>(
			kClass = AttributeModifierDisplay::class,
			useMinecraftPrefix = false
		)
	}
}

@Serializable
data object Default : AttributeModifierDisplay()

@Serializable
data object Hidden : AttributeModifierDisplay()

@Serializable
data class Override(var value: ChatComponents) : AttributeModifierDisplay()

@Serializable
data class AttributeModifier(
	var type: AttributeArgument,
	var slot: EquipmentSlot? = null,
	var id: AttributeModifierArgument,
	var amount: Double,
	var operation: AttributeModifierOperation,
	var display: AttributeModifierDisplay? = null,
)

@Serializable(with = AttributeModifiersComponent.Companion.AttributeModifiersComponentSerializer::class)
data class AttributeModifiersComponent(
	var modifiers: MutableList<AttributeModifier>,
) : Component() {
	companion object {
		data object AttributeModifiersComponentSerializer : InlineAutoSerializer<AttributeModifiersComponent>(AttributeModifiersComponent::class)
	}
}

fun ComponentsScope.attributeModifiers(
	modifiers: List<AttributeModifier>,
) = apply { this[ItemComponentTypes.ATTRIBUTE_MODIFIERS] = AttributeModifiersComponent(modifiers.toMutableList()) }

fun ComponentsScope.attributeModifiers(
	vararg modifiers: AttributeModifier,
) = attributeModifiers(modifiers.toList())

fun ComponentsScope.attributeModifiers(modifiers: AttributeModifiersComponent.() -> Unit) =
	AttributeModifiersComponent(mutableListOf()).apply(modifiers).let { this[ItemComponentTypes.ATTRIBUTE_MODIFIERS] = it }

fun AttributeModifiersComponent.modifier(
	type: AttributeArgument,
	slot: EquipmentSlot? = null,
	id: AttributeModifierArgument,
	amount: Double,
	operation: AttributeModifierOperation,
	block: AttributeModifier.() -> Unit = {},
) = apply { modifiers += AttributeModifier(type, slot, id, amount, operation).apply(block) }

fun AttributeModifiersComponent.modifier(
	type: AttributeArgument,
	slot: EquipmentSlot? = null,
	name: String,
	namespace: String = "minecraft",
	amount: Double,
	operation: AttributeModifierOperation,
	block: AttributeModifier.() -> Unit = {},
) = apply { modifiers += AttributeModifier(type, slot, AttributeModifierArgument(name, namespace), amount, operation).apply(block) }


fun AttributeModifier.displayDefault() = apply { display = Default }
fun AttributeModifier.displayHidden() = apply { display = Hidden }
fun AttributeModifier.displayOverride(value: ChatComponents) = apply { display = Override(value) }
fun AttributeModifier.displayOverride(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	display = Override(textComponent(text, color, block))
}
