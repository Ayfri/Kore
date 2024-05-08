package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LoreComponent.Companion.LoreComponentSerializer::class)
data class LoreComponent(
	var list: ChatComponents = ChatComponents(),
) : Component() {
	companion object {
		object LoreComponentSerializer : InlineSerializer<LoreComponent, ChatComponents>(
			ChatComponents.Companion.ChatComponentsAsListEscapedSerializer,
			LoreComponent::list
		)
	}
}

fun ComponentsScope.lore(chatComponents: ChatComponents) = apply { this[ComponentTypes.LORE] = LoreComponent(chatComponents) }

fun ComponentsScope.lore(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	lore(textComponent(text, color, block))

fun ComponentsScope.lores(block: ChatComponents.() -> Unit) = lore(ChatComponents().apply(block))
