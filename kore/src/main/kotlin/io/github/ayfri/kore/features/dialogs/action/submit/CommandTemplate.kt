package io.github.ayfri.kore.features.dialogs.action.submit

import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import kotlinx.serialization.Serializable

@Serializable
data class CommandTemplate(
	var template: String,
) : SubmitMethod()

fun SubmitMethodContainer.commandTemplate(template: String) {
	method = CommandTemplate(template)
}

fun SubmitMethodContainer.commandTemplate(block: Function.() -> Command) {
	method = CommandTemplate(emptyFunction().block().toString())
}
