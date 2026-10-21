package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponent
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:sign_text_front` and `minecraft:sign_text_back` item components, which set the text of one side of a sign.
 * Both components share this shape: exactly 4 [messages] lines, optionally as many [filteredMessages] shown to players with the profanity filter.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#sign_text_front
 */
@Serializable
data class SignTextComponent(
	var color: DyeColors? = null,
	var filteredMessages: @Serializable(ChatComponents.Companion.ChatComponentsLinesSerializer::class) ChatComponents? = null,
	var hasGlowingText: Boolean? = null,
	var messages: @Serializable(ChatComponents.Companion.ChatComponentsLinesSerializer::class) ChatComponents,
) : Component()

private fun ComponentsScope.signText(
	type: ItemComponentTypes,
	lines: Array<out ChatComponent>,
	block: SignTextComponent.() -> Unit,
) = apply {
	val signText = SignTextComponent(messages = ChatComponents(*lines)).apply(block)
	require(signText.messages.list.size == 4) { "A sign side has exactly 4 messages, got ${signText.messages.list.size}." }
	require(signText.filteredMessages?.list?.size.let { it == null || it == 4 }) {
		"A sign side has exactly 4 filtered messages, got ${signText.filteredMessages?.list?.size}."
	}
	this[type] = signText
}

/**
 * Sets the text of the back of a sign, one component per line (exactly 4).
 *
 * ```
 * signTextBack(text("Closed", Color.RED), text(), text("Come back later"), text()) { color = DyeColors.RED }
 * ```
 */
fun ComponentsScope.signTextBack(vararg lines: ChatComponent, block: SignTextComponent.() -> Unit = {}) =
	signText(ItemComponentTypes.SIGN_TEXT_BACK, lines, block)

/**
 * Sets the text of the front of a sign, one component per line (exactly 4).
 *
 * ```
 * signTextFront(text("Shop"), text(), text("Open"), text()) { hasGlowingText = true }
 * ```
 */
fun ComponentsScope.signTextFront(vararg lines: ChatComponent, block: SignTextComponent.() -> Unit = {}) =
	signText(ItemComponentTypes.SIGN_TEXT_FRONT, lines, block)
