package io.github.ayfri.kore.arguments.chatcomponents.click

import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import kotlinx.serialization.Serializable

@Serializable
data class RunCommand(
	var command: String,
) : ClickEvent()

fun ClickEventContainer.runCommand(command: String) = apply { event = RunCommand(command) }
fun ClickEventContainer.runCommand(block: Function.() -> Command) = apply {
	event = RunCommand(emptyFunction().block().toString())
}
