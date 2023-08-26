package commands.execute

import arguments.types.EntityArgument
import arguments.types.literals.literal
import arguments.types.resources.FunctionArgument
import commands.Command
import commands.command
import functions.Function
import functions.emptyFunction
import functions.generatedFunction

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
