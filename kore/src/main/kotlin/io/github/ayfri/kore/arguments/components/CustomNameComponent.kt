package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable

@Serializable(with = CustomNameComponent.Companion.CustomNameComponentSerializer::class)
data class CustomNameComponent(
	val component: ChatComponents,
) : Component() {
	companion object {
		object CustomNameComponentSerializer : InlineSerializer<CustomNameComponent, ChatComponents>(
			ChatComponents.Companion.ChatComponentsEscapedSerializer,
			CustomNameComponent::component
		)
	}
}

fun Components.customName(component: ChatComponents) = apply { this[ComponentTypes.CUSTOM_NAME] = CustomNameComponent(component) }
fun Components.customName(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	customName(textComponent(text, color, block))
