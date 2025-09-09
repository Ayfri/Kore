package io.github.ayfri.kore.arguments.chatcomponents.click

import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import kotlinx.serialization.Serializable

@Serializable
data class SuggestCommand(
	var command: String,
) : ClickEvent()

fun ClickEventContainer.suggestChatMessage(command: String) = apply { event = SuggestCommand(command) }

fun ClickEventContainer.suggestCommand(block: Function.() -> Command) = apply {
	event = SuggestCommand("/${emptyFunction().block()}")
}
