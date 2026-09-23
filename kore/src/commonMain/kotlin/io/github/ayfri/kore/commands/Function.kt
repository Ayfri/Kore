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

/**
 * Resolves [name] against the current datapack, keeping an already namespaced id (`ns:name`) as is.
 * Functions without a datapack (the `runCommand { }` / `suggestCommand { }` builders) can't resolve a bare name.
 */
private fun Function.functionId(name: String, group: Boolean) = when {
	':' in name -> tag(name, group)
	datapack.name.isEmpty() -> error(
		"Cannot resolve the namespace of function '$name' here, there is no datapack in scope. Pass a namespaced id like 'my_pack:$name' or the function itself."
	)

	else -> tag(name, datapack.name, group)
}

/** Calls a function by [name], resolved in the current datapack unless already namespaced, optionally passing NBT [arguments]. */
fun Function.function(name: String, group: Boolean = false, arguments: NbtCompound? = null) = addLine(
	command(
		"function",
		functionId(name, group),
		compound(arguments)
	)
)

/** Calls a function by [name], resolved in the current datapack unless already namespaced, passing data from [arguments] at [path]. */
fun Function.function(name: String, group: Boolean = false, arguments: DataArgument, path: String? = null) = addLine(
	command(
		"function",
		functionId(name, group),
		literal("with"),
		literal(arguments.literalName),
		arguments,
		literal(path)
	)
)

/** Calls a function in [namespace] using [name], optionally passing NBT [arguments]. */
fun Function.function(namespace: String, name: String, group: Boolean = false, arguments: NbtCompound? = null) = addLine(
	command(
		"function",
		tag(name, namespace, group),
		compound(arguments)
	)
)

/** Calls a function in [namespace] using [name], passing data from [arguments] at [path]. */
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

/** Calls [function], optionally passing NBT [arguments]. */
fun Function.function(function: FunctionOrTagArgument, arguments: NbtCompound? = null) = addLine(
	command(
		"function",
		function,
		compound(arguments)
	)
)

/** Calls [function], passing data from [arguments] at [path]. */
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

/** Validates that a macro call provides the required [macros] in [argumentList]. */
fun verifyArguments(functionId: String, macros: List<String>, argumentList: List<String>) {
	require(macros.size >= argumentList.size) { "Expected ${macros.size} arguments, but got ${argumentList.size} when calling function '$functionId'" }
	require(macros.all { it in argumentList }) {
		"Missing arguments '${macros.filter { it !in argumentList }.joinToString()}' when calling function '$functionId'"
	}
}

/** Calls a macro-enabled [function] with the given NBT [arguments]. */
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

/** Calls a macro-enabled [function] using the provided NBT [builder]. */
inline fun <reified T : Macros> Function.function(
	function: FunctionWithMacros<T>,
	noinline builder: NbtCompoundBuilder.() -> Unit,
) = function(function, nbt(builder))
