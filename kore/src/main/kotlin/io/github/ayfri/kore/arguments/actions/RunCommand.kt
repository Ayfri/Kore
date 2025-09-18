package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import kotlinx.serialization.Serializable

@Serializable
data class RunCommand(
	var command: String,
) : Action(), ClickEvent, DialogAction

/** Runs the given command. */
fun ActionWrapper<*>.runCommand(command: String) = apply { action = RunCommand(command) }

/** Runs the given command. */
fun ActionWrapper<*>.runCommand(block: Function.() -> Command) = apply {
	action = RunCommand(emptyFunction().block().toString())
}
