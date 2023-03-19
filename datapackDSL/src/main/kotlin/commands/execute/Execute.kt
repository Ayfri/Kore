package commands.execute

import DataPack.Companion.GENERATED_FUNCTIONS_FOLDER
import arguments.Argument
import arguments.literal
import commands.Command
import commands.command
import functions.Function
import functions.function
import functions.generatedFunction

context(Function)
fun Execute.run(name: String, namespace: String = datapack.name, block: Function.() -> Unit): Argument.Function {
	datapack.function(name, namespace, block = block)
	return Argument.Function(namespace, "$GENERATED_FUNCTIONS_FOLDER/$name")
}

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
	val namespace = datapack.name

	val finalName = datapack.generatedFunction(name) { block() }
	val usageName = "$GENERATED_FUNCTIONS_FOLDER/$finalName"

	if (finalName.name == name) comment("Generated function $namespace:$usageName")

	return Argument.Function(namespace, usageName)
}

fun Function.execute(block: Execute.() -> Argument.Function): Command {
	val execute = Execute()
	val run = execute.block()
	val runArg = if (run.toString() == "") run.asId() else "function ${run.asId()}"
	return addLine(command("execute", *execute.getArguments(), literal("run"), literal(runArg)))
}
