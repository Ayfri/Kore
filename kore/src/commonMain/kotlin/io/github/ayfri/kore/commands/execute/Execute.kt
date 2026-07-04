package io.github.ayfri.kore.commands.execute

import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.functions.EmptyFunction
import io.github.ayfri.kore.functions.Function

/**
 * Builder for the `/execute` command.
 *
 * Kore uses this scope to assemble the chain of subcommands before the final `run` clause.
 * The resulting DSL mirrors vanilla `execute` behavior while keeping the command generation type-safe.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/execute)
 */
fun Function.execute(block: Execute.() -> Unit): Command {
	val execute = Execute()
	execute.block()
	if (execute.run is EmptyFunction && (execute.run as EmptyFunction).lines.isEmpty()) return addLine(
		command(
			"execute",
			*execute.getArguments()
		)
	)

	val runArg = if (execute.run.toString() == "") execute.run.asId() else "function ${execute.run.asId()}"
	return addLine(command("execute", *execute.getArguments(), literal("run"), literal(runArg)))
}
