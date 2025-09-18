package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import io.github.ayfri.kore.functions.generatedFunction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("dynamic/run_command")
data class DynamicRunCommand(
	var template: String,
) : Action(), DialogAction

/** Dynamically build a command to run, you can use macros with the same names as the inputs, undefined macros will just be replaced with an empty string. */
fun DialogActionContainer.dynamicRunCommand(template: String) = apply { action = DynamicRunCommand(template) }

/** Dynamically build a command to run, you can use macros with the same names as the inputs, undefined macros will just be replaced with an empty string. */
fun DialogActionContainer.dynamicRunCommand(block: Function.() -> Command) = apply {
	val result = emptyFunction { block() }
	if (result.commandLines.size > 1) throw IllegalArgumentException("DynamicRunCommand without any Function or Datapack context can only accept a single command, other commands will be discarded.")
	action = DynamicRunCommand(result.commandLines.last())
}

/** Dynamically build a command to run, you can use macros with the same names as the inputs, undefined macros will just be replaced with an empty string. */
fun DialogActionContainer.dynamicRunCommand(dp: DataPack, block: Function.() -> Command) = apply {
	val result = emptyFunction { block() }
	if (result.commandLines.size == 1) {
		action = DynamicRunCommand(result.commandLines.first())
		return@apply
	}
	val newFunction = dp.generatedFunction("generated_${hashCode()}") {
		block()
	}
	action = DynamicRunCommand("function ${newFunction.name}")
}

/** Dynamically build a command to run, you can use macros with the same names as the inputs. */
context(dp: DataPack)
fun DialogActionContainer.dynamicRunCommand(block: Function.() -> Command) = apply { dynamicRunCommand(dp, block) }
