package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.DataArgument
import io.github.ayfri.kore.arguments.types.FunctionOrTagArgument
import io.github.ayfri.kore.arguments.types.literals.compound
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.tag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.utils.nbt
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder

fun Function.function(name: String, group: Boolean = false, arguments: NbtCompound? = null) = addLine(
	command(
		"function",
		tag(name, datapack.name, group),
		compound(arguments)
	)
)

fun Function.function(name: String, group: Boolean = false, arguments: DataArgument, path: String? = null) = addLine(
	command(
		"function",
		tag(name, datapack.name, group),
		literal("with"),
		literal(arguments.literalName),
		arguments,
		literal(path)
	)
)

fun Function.function(namespace: String, name: String, group: Boolean = false, arguments: NbtCompound? = null) = addLine(
	command(
		"function",
		tag(name, namespace, group),
		compound(arguments)
	)
)

fun Function.function(namespace: String, name: String, group: Boolean = false, arguments: DataArgument, path: String? = null) = addLine(
	command(
		"function",
		tag(name, namespace, group),
		literal("with"),
		literal(arguments.literalName),
		arguments,
		literal(path)
	)
)

fun Function.function(function: FunctionOrTagArgument, arguments: NbtCompound? = null) = addLine(
	command(
		"function",
		function,
		compound(arguments)
	)
)

fun Function.function(function: FunctionOrTagArgument, arguments: DataArgument, path: String? = null) = addLine(
	command(
		"function",
		function,
		literal("with"),
		literal(arguments.literalName),
		arguments,
		literal(path)
	)
)

fun verifyArguments(functionId: String, macros: List<String>, argumentList: List<String>) {
	require(macros.size >= argumentList.size) { "Expected ${macros.size} arguments, but got ${argumentList.size} when calling function '$functionId'" }
	require(macros.all { it in argumentList }) {
		"Missing arguments '${macros.filter { it !in argumentList }.joinToString()}' when calling function '$functionId'"
	}
}

inline fun <reified T : Macros> Function.function(function: FunctionWithMacros<T>, arguments: NbtCompound): Command {
	verifyArguments(function.asId(), function.macros.args.toList(), arguments.keys.toList())

	return addLine(
		command(
			"function",
			function,
			compound(arguments)
		)
	)
}

inline fun <reified T : Macros> Function.function(
	function: FunctionWithMacros<T>,
	noinline builder: NbtCompoundBuilder.() -> Unit,
) = function(function, nbt(builder))
