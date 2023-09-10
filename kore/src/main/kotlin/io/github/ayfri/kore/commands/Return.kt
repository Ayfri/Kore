package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.PredicateArgument
import io.github.ayfri.kore.commands.execute.ExecuteCondition
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.execute.run
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction

@Deprecated("This subcommand has been postponed to after 1.20.2.", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
fun Function.returnIf(condition: ExecuteCondition.() -> Unit, block: Function.() -> Command) = execute {
	ifCondition(condition)
	run {
		returnRun(block)
	}
}

fun Function.returnIf(returnValue: Int, condition: ExecuteCondition.() -> Unit) = execute {
	ifCondition(condition)
	run {
		returnValue(returnValue)
	}
}

@Deprecated("This subcommand has been postponed to after 1.20.2.", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
fun Function.returnIf(vararg predicates: PredicateArgument, block: Function.() -> Command) = execute {
	ifCondition {
		predicates.forEach { predicate(it) }
	}
	run {
		returnRun(block)
	}
}

fun Function.returnIf(returnValue: Int, vararg predicates: PredicateArgument) = execute {
	ifCondition {
		predicates.forEach { predicate(it) }
	}
	run {
		returnValue(returnValue)
	}
}

@Deprecated("This subcommand has been postponed to after 1.20.2.", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
fun Function.returnUnless(condition: ExecuteCondition.() -> Unit, block: Function.() -> Command) = execute {
	unlessCondition(condition)
	run {
		returnRun(block)
	}
}

fun Function.returnUnless(returnValue: Int, condition: ExecuteCondition.() -> Unit) = execute {
	unlessCondition(condition)
	run {
		returnValue(returnValue)
	}
}

@Deprecated("This subcommand has been postponed to after 1.20.2.", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
fun Function.returnUnless(vararg predicates: PredicateArgument, block: Function.() -> Command) = execute {
	unlessCondition {
		predicates.forEach { predicate(it) }
	}
	run {
		returnRun(block)
	}
}

fun Function.returnUnless(returnValue: Int, vararg predicates: PredicateArgument) = execute {
	unlessCondition {
		predicates.forEach { predicate(it) }
	}
	run {
		returnValue(returnValue)
	}
}

@Deprecated("This subcommand has been postponed to after 1.20.2.", level = DeprecationLevel.ERROR)
fun Function.returnRun(block: Function.() -> Command): Command {
	val function = Function("", "", "", datapack).apply { block() }

	if (function.lines.size == 1) return addLine(command("return", literal("run"), literal(function.lines[0])))

	val name = "generated_${hashCode()}"
	val generatedFunction = datapack.generatedFunction(name) { block() }
	if (generatedFunction.name == name) comment("Generated function ${asString()}")

	@Suppress("DEPRECATION_ERROR")
	return returnRun(generatedFunction)
}

@Deprecated("This subcommand has been postponed to after 1.20.2.", level = DeprecationLevel.ERROR)
fun Function.returnRun(function: FunctionArgument) = addLine(command("return", literal("run"), literal("function"), function))

fun Function.returnValue(value: Int) = addLine(command("return", int(value)))
