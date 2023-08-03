package functions

import kotlin.reflect.KProperty

open class Macros {
	val args = mutableSetOf<String>()

	companion object {
		const val MACRO_NAME_REGEX = "[a-zA-Z0-9_]+"
		const val MACRO_REGEX = "\\$\\($MACRO_NAME_REGEX\\)"
	}
}

inline operator fun String.getValue(thisRef: Macros, property: KProperty<*>): String {
	require(this matches Regex(Macros.MACRO_NAME_REGEX)) {
		"Macro name must only contain letters, numbers and underscores, got '$this'."
	}

	thisRef.args += this
	return "\$($this)"
}

/**
 * Uses the macro variable with the given [name] in the function.
 */
fun Function.macro(name: String) = "$($name)".also { nextLineHasMacro = true }
