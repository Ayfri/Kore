package io.github.ayfri.kore.commands.execute

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import io.github.ayfri.kore.functions.generatedFunction

context(Function)
fun Execute.run(name: String, directory: String = "", block: Function.() -> Unit) = datapack.generatedFunction(name, directory, block)

context(Function)
fun Execute.run(block: Function.() -> Command): FunctionArgument {
	val function = Function("", "", "", datapack).apply { block() }
	val nonCommentedLines = function.lines.filter { !it.startsWith("#") }

	if (nonCommentedLines.size == 1) return emptyFunction(datapack) {
		block().apply {
			arguments.replaceAll {
				when (it) {
					is EntityArgument -> targetArg(it)
					else -> it
				}
			}
		}
		lines.removeAll { it.startsWith("#") }
	}

	val name = "generated_${hashCode()}"
	val generatedFunction = datapack.generatedFunction(name) { block() }
	if (generatedFunction.name == name) comment("Generated function ${asString()}")

	return generatedFunction
}

fun Function.execute(block: Execute.() -> FunctionArgument): Command {
	val execute = Execute()
	val run = execute.block()
	val runArg = if (run.toString() == "") run.asId() else "function ${run.asId()}"
	return addLine(command("execute", *execute.getArguments(), literal("run"), literal(runArg)))
}
