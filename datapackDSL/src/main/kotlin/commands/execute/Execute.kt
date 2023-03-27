package commands.execute

import arguments.Argument
import arguments.literal
import commands.Command
import commands.command
import functions.Function
import functions.generatedFunction

context(Function)
fun Execute.run(name: String, directory: String = "", block: Function.() -> Unit) = datapack.generatedFunction(name, directory, block)

context(Function)
fun Execute.run(block: Function.() -> Command): Argument.Function {
	val function = Function("", "", "", datapack).apply { block() }

	if (function.lines.size == 1) return Function.EMPTY.apply {
		block().apply {
			arguments.replaceAll {
				when (it) {
					is Argument.Entity -> this@run.targetArg(it)
					else -> it
				}
			}
		}
	}

	val name = "generated_${hashCode()}"
	val generatedFunction = datapack.generatedFunction(name) { block() }
	if (generatedFunction.name == name) comment("Generated function ${asString()}")

	return generatedFunction
}

fun Function.execute(block: Execute.() -> Argument.Function): Command {
	val execute = Execute()
	val run = execute.block()
	val runArg = if (run.toString() == "") run.asId() else "function ${run.asId()}"
	return addLine(command("execute", *execute.getArguments(), literal("run"), literal(runArg)))
}
