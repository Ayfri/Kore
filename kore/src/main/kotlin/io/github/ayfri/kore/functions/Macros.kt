package io.github.ayfri.kore.functions

import kotlin.reflect.KProperty

/**
* Holder for macro identifiers usable inside a `Function` instance.
*
* Macros are textual placeholders (for example `$(id)`) that can be injected
* into generated command lines. The [args] set contains every macro name
* that has been declared for this scope.
*
* Docs: https://kore.ayfri.com/docs/functions/macros
*/
open class Macros {
	/** Set of declared macro names for this macro holder. */
	val args = mutableSetOf<String>()

	companion object {
		/** Regex that matches a valid macro identifier. */
		const val MACRO_NAME_REGEX = "[a-zA-Z0-9_]+"

		/** Regex pattern used to find macro expressions like `$(name)`. */
		const val MACRO_REGEX = "\\$\\($MACRO_NAME_REGEX\\)"
	}
}

/**
* Delegate allowing `val foo by "bar"` style declarations for macros.
* The delegate validates the macro name, registers it in [thisRef.args]
* and returns the textual macro `$(name)` to be used in command lines.
*/
inline operator fun String.getValue(thisRef: Macros, property: KProperty<*>): String {
	require(this matches Regex(Macros.MACRO_NAME_REGEX)) {
		"Macro name must only contain letters, numbers and underscores, got '$this'."
	}

	thisRef.args += this
	return $$"$($$this)"
}

/**
 * Inserts a macro usage `$(name)` into the function and marks the next
 * line as containing a macro so it will be handled correctly when generating
 * the final output.
 */
fun Function.macro(name: String) = "$($name)".also { nextLineHasMacro = true }
