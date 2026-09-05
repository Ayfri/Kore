package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.types.DataArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.getValue

/** Macros for the `kore_string_parse` helper: writes `$(value)` unquoted into the destination. */
class ParseMacros internal constructor() : Macros() {
	val value by "value"
	val dstStorage by "dstStorage"
	val dstPath by "dstPath"
}

/** Macros for the `kore_string_to_string` helper: writes `"$(value)"` into the destination. */
class ToStringMacros internal constructor() : Macros() {
	val value by "value"
	val dstName by "dstName"
}

/**
 * Registers a macro that evaluates the value of a [DynamicString] as SNBT and writes the parsed
 * value into the specified destination storage path.
 */
internal fun DynamicStringRuntime.parseHelper(): FunctionWithMacros<ParseMacros> =
	ensure(OopConstants.stringParseMacroName, ::ParseMacros) {
		addLine("data modify storage ${macros.dstStorage} ${macros.dstPath} set value ${macros.value}")
	}

/**
 * Registers a macro that writes the textual representation of an arbitrary NBT value into a target
 * [DynamicString]. Works for primitive types (byte / short / int / long / float / double / string)
 * as well as compounds and lists: the macro expansion always emits the NBT literal form between
 * quotes.
 */
internal fun DynamicStringRuntime.toStringHelper(): FunctionWithMacros<ToStringMacros> =
	ensure(OopConstants.stringToStringMacroName, ::ToStringMacros) {
		addLine("data modify storage $libStorage ${heapPath(macros.dstName)} set value \"${macros.value}\"")
	}

/**
 * Parses the textual content of this [DynamicString] as SNBT and stores the resulting NBT value
 * into [target]/[targetPath].
 *
 * Supports numbers (`42`, `3.14`, `1L`), booleans (`true`/`false`), strings (`"hello"`) and full
 * NBT literals (`{a: 1, b: [1,2,3]}`).
 */
context(fn: Function)
fun DynamicString.parseTo(target: DataArgument, targetPath: String) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	rt.parseHelper()
	val args = rt.argsPath(OopConstants.stringParseMacroName)
	fn.data(storage) {
		modify("$args.value") { set(storage, nbtPath) }
		modify("$args.dstStorage", target.asString())
		modify("$args.dstPath", targetPath)
	}
	fn.function(
		namespace = fn.datapack.name,
		name = OopConstants.stringParseMacroName,
		arguments = storage,
		path = args
	)
}

/**
 * Reads the NBT value located at [sourcePath] inside [source] and stores its textual NBT
 * representation into this [DynamicString].
 */
context(fn: Function)
fun DynamicString.setFromNbt(source: DataArgument, sourcePath: String) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	rt.toStringHelper()
	val args = rt.argsPath(OopConstants.stringToStringMacroName)
	fn.data(storage) {
		modify("$args.value") { set(source, sourcePath) }
		modify("$args.dstName", name)
	}
	fn.function(
		namespace = fn.datapack.name,
		name = OopConstants.stringToStringMacroName,
		arguments = storage,
		path = args
	)
}
