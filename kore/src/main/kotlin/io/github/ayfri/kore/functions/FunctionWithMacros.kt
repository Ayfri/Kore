package io.github.ayfri.kore.functions

import io.github.ayfri.kore.DataPack

class FunctionWithMacros<out T : Macros>(
	name: String,
	macros: T,
	namespace: String = "minecraft",
	directory: String = "",
	datapack: DataPack,
) : Function(
	name,
	namespace,
	directory,
	datapack,
) {
	private val _macros = macros

	/**
	 * The macros that can be used in this function.
	 * Should only be used inside a command (as it defines the next command using .
	 */
	val macros
		get() = _macros.also {
			nextLineHasMacro = true
		}
}

fun <T : Macros> DataPack.function(
	name: String,
	macros: () -> T,
	namespace: String = this.name,
	directory: String = "",
	function: FunctionWithMacros<T>.() -> Unit,
) = FunctionWithMacros(
	name,
	macros(),
	namespace,
	directory,
	this,
).apply(function).also(::addFunction)

/**
 * Adds a line to the function that uses the given macros.
 */
fun Function.eval(vararg macroNames: String) = addLine("$${macroNames.joinToString(" ") { "$($it)" }}")
