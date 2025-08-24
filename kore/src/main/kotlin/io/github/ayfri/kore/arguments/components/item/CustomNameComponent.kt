package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = CustomNameComponent.Companion.CustomNameComponentSerializer::class)
data class CustomNameComponent(val component: ChatComponents) : Component() {
	companion object {
		data object CustomNameComponentSerializer : InlineAutoSerializer<CustomNameComponent>(CustomNameComponent::class)
	}
}

fun ComponentsScope.customName(component: ChatComponents) = apply { this[ItemComponentTypes.CUSTOM_NAME] = CustomNameComponent(component) }
fun ComponentsScope.customName(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	customName(textComponent(text, color, block))
