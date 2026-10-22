package io.github.ayfri.kore.helpers.conversations

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent

/** How a [Conversation.choice] is shown to the player. */
enum class ChoiceStyle {
	/** Clickable options in the chat, see [ChatChoiceFormat]. */
	CHAT,

	/** A dialog with one button per option, closable with Escape without choosing, the choice then stays pending. */
	DIALOG,
}

/**
 * How a [ChoiceStyle.CHAT] choice is written in the chat, set for a whole conversation with [Conversation.chatFormat] and
 * overridden for one choice with [ConversationChoice.chat].
 *
 * ```kotlin
 * chatFormat = ChatChoiceFormat(separator = null)  // one option per line instead of "[Accept]  [Decline]"
 * ```
 *
 * @property decorate Wraps the label of each option, the result is what is clickable and carries the hover text. The default puts
 * the label in brackets of its own color: `Accept` in green gives a green `[Accept]`.
 * @property prompt Whether the title of the choice is said first, as a [Conversation.line] from the speaker.
 * @property separator What is written between two options on the same line, `null` to write each option on its own line.
 */
data class ChatChoiceFormat(
	var decorate: (label: ChatComponents) -> ChatComponents = { label ->
		val color = label.firstOrNull()?.color
		textComponent("[", color) + label + textComponent("]", color)
	},
	var prompt: Boolean = true,
	var separator: ChatComponents? = textComponent("  "),
)
