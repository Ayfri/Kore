package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.generatedFunctionName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Runs a command built from [template] by substituting macro placeholders with matching dialog input values.
 * Undefined macros are replaced with an empty string.
 */
@Serializable
@SerialName("dynamic/run_command")
data class DynamicRunCommand(
	/** Command template string. Macro names matching dialog input IDs are substituted at runtime. */
	var template: String,
) : Action(), DialogAction

/** Dynamically build a command to run, you can use macros with the same names as the inputs, undefined macros will just be replaced with an empty string. */
fun DialogActionContainer.dynamicRunCommand(template: String) = apply { action = DynamicRunCommand(template) }

/** Dynamically build a command to run, you can use macros with the same names as the inputs, undefined macros will just be replaced with an empty string. */
fun DialogActionContainer.dynamicRunCommand(block: Function.() -> Command) = apply {
	val commandLines = emptyFunction { block() }.commandLines
	require(commandLines.size == 1) { "DynamicRunCommand without any Function or Datapack context accepts exactly one command, got ${commandLines.size}." }
	action = DynamicRunCommand(commandLines.single())
}

/** Dynamically build a command to run, you can use macros with the same names as the inputs, undefined macros will just be replaced with an empty string. */
fun DialogActionContainer.dynamicRunCommand(dp: DataPack, block: Function.() -> Command) = apply {
	val result = Function("", "", datapack = dp).apply { block() }
	result.commandLines.singleOrNull()?.let {
		action = DynamicRunCommand(it)
		return@apply
	}
	val newFunction = dp.generatedFunction(generatedFunctionName("generated", result.lines)) { lines += result.lines }
	action = DynamicRunCommand("function ${newFunction.asId()}")
}
