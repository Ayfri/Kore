package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import kotlinx.serialization.Serializable

@Serializable
data class SuggestCommand(
	var command: String,
) : Action(), ClickEvent, DialogAction

/** Autofill the chat input with the given message. */
fun ActionWrapper<*>.suggestChatMessage(command: String) = apply { action = SuggestCommand(command) }

/** Autofill the chat input with the given command. */
fun ActionWrapper<*>.suggestCommand(block: Function.() -> Command) = apply {
	action = SuggestCommand("/${emptyFunction().block()}")
}
