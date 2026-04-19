package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.execute.ExecuteCondition
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument

/**
 * DSL scope for the `/return` command.
 *
 * `/return` ends the current function immediately and propagates success and result values back to
 * the caller, which makes it useful for early exits and conditional function flows.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/return)
 */
fun Function.returnFail() = addLine(command("return", literal("fail")))

/**
 * Runs [block] under [condition] and returns whatever [block] returns to the caller.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/return)
 */
fun Function.returnIf(condition: ExecuteCondition.() -> Unit, block: Function.() -> Command) = execute {
	ifCondition(condition)
	run {
		returnRun(block)
	}
}

/**
 * Returns [returnValue] when [condition] matches.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/return)
 */
fun Function.returnIf(returnValue: Int, condition: ExecuteCondition.() -> Unit) = execute {
	ifCondition(condition)
	run {
		returnValue(returnValue)
	}
}

/**
 * Runs [block] if all [predicates] matches and returns the nested result to the caller.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/return)
 */
fun Function.returnIf(vararg predicates: PredicateArgument, block: Function.() -> Command) = execute {
	ifCondition {
		predicates.forEach { predicate(it) }
	}
	run {
		returnRun(block)
	}
}

/**
 * Returns [returnValue] when every [predicate] matches.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/return)
 */
fun Function.returnIf(returnValue: Int, vararg predicates: PredicateArgument) = execute {
	ifCondition {
		predicates.forEach { predicate(it) }
	}
	run {
		returnValue(returnValue)
	}
}

/**
 * Runs [block] unless [condition] matches, then returns the nested result to the caller.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/return)
 */
fun Function.returnUnless(condition: ExecuteCondition.() -> Unit, block: Function.() -> Command) = execute {
	unlessCondition(condition)
	run {
		returnRun(block)
	}
}

/**
 * Returns [returnValue] unless [condition] matches.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/return)
 */
fun Function.returnUnless(returnValue: Int, condition: ExecuteCondition.() -> Unit) = execute {
	unlessCondition(condition)
	run {
		returnValue(returnValue)
	}
}

/**
 * Runs [block] unless any [predicate] matches, then returns the nested result to the caller.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/return)
 */
fun Function.returnUnless(vararg predicates: PredicateArgument, block: Function.() -> Command) = execute {
	unlessCondition {
		predicates.forEach { predicate(it) }
	}
	run {
		returnRun(block)
	}
}

/**
 * Returns [returnValue] unless every [predicate] matches.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/return)
 */
fun Function.returnUnless(returnValue: Int, vararg predicates: PredicateArgument) = execute {
	unlessCondition {
		predicates.forEach { predicate(it) }
	}
	run {
		returnValue(returnValue)
	}
}

/**
 * Runs [block] and returns the nested command result to the caller.
 *
 * If [block] emits a single command, Kore inlines it. Otherwise Kore generates a helper function
 * and returns that generated function.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/return)
 */
fun Function.returnRun(block: Function.() -> Command): Command {
	val function = Function("", "", "", datapack).apply { block() }

	if (function.lines.size == 1) return addLine(command("return", literal("run"), literal(function.lines[0])))

	val name = "generated_${hashCode()}"
	val generatedFunction = datapack.generatedFunction(name) { block() }
	if (generatedFunction.name == name) comment("Generated function ${asString()}")

	@Suppress("DEPRECATION_ERROR")
	return returnRun(generatedFunction)
}

/**
 * Returns the result of calling [function].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/return)
 */
fun Function.returnRun(function: FunctionArgument) = addLine(command("return", literal("run"), literal("function"), function))

/**
 * Ends the current function successfully with [value].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/return)
 */
fun Function.returnValue(value: Int) = addLine(command("return", int(value)))
