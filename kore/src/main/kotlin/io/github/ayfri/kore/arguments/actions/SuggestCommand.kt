package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import kotlinx.serialization.Serializable

@Serializable
data class SuggestCommand(
	var command: String,
) : ClickEvent()

fun ActionWrapper<*>.suggestChatMessage(command: String) = apply { action = SuggestCommand(command) }
fun ActionWrapper<*>.suggestCommand(block: Function.() -> Command) = apply {
	action = SuggestCommand("/${emptyFunction().block()}")
}
