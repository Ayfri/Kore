package io.github.ayfri.kore.functions

import io.github.ayfri.kore.DataPack

/**
* A [Function] that exposes a typed set of [Macros] for safe macro usage.
*
* The generic parameter `T` represents a user-provided holder of macro
* identifiers (subclass of [Macros]). Accessing `macros` marks the next
* added line as containing macros so the function generation can handle it.
*
* Docs: https://kore.ayfri.com/docs/functions
* Macro reference: https://kore.ayfri.com/docs/functions/macros
*
* @param name function name
* @param macros instance providing macro identifiers
* @param namespace function namespace
* @param directory optional function directory
* @param datapack owning datapack
*/
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
	* Accessing this property will mark the next line as containing macros.
	*/
	val macros
		get() = _macros.also {
			nextLineHasMacro = true
		}
}

/**
* Convenience builder that creates a [FunctionWithMacros] and registers it
* in the receiving [DataPack]. The provided `macros` factory will be used
* to instantiate the typed macro holder for the function scope.
*/
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
* Adds a line to the function that contains the given macro usages.
* Each provided name will be expanded to the `$(name)` macro form.
*
* Docs: https://kore.ayfri.com/docs/functions/macros
 */
fun Function.eval(vararg macroNames: String) = addLine("$${macroNames.joinToString(" ") { "$($it)" }}")
