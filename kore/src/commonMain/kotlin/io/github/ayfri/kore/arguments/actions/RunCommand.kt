package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import kotlinx.serialization.Serializable

/** Runs [command] as the clicking player when the component is clicked. The string should not include a leading `/`. */
@Serializable
data class RunCommand(
	/** The command to execute, without a leading `/`. */
	var command: String,
) : Action(), ClickEvent, DialogAction

/** Runs the given command. */
fun ActionWrapper<*>.runCommand(command: String) = apply { action = RunCommand(command) }

/** Runs the given command. */
fun ActionWrapper<*>.runCommand(block: Function.() -> Command) = apply {
	action = RunCommand(emptyFunction().block().toString())
}
