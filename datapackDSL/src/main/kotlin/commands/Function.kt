package commands

import arguments.types.DataArgument
import arguments.types.literals.compound
import arguments.types.literals.literal
import arguments.types.literals.tag
import arguments.types.resources.FunctionArgument
import functions.Function
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

fun Function.function(function: FunctionArgument, arguments: NbtCompound? = null) = addLine(
	command(
		"function",
		function,
		compound(arguments)
	)
)

fun Function.function(function: FunctionArgument, arguments: DataArgument, path: String? = null) = addLine(
	command(
		"function",
		function,
		literal("with"),
		literal(arguments.literalName),
		arguments,
		literal(path)
	)
)
