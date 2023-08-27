package commands

import arguments.types.DataArgument
import arguments.types.FunctionOrTagArgument
import arguments.types.literals.compound
import arguments.types.literals.literal
import arguments.types.literals.tag
import functions.Function
import functions.FunctionWithMacros
import functions.Macros
import net.benwoodworth.knbt.NbtCompound

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
