package io.github.ayfri.kore.functions

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.events.ClickEvent
import io.github.ayfri.kore.arguments.chatcomponents.events.runCommand
import io.github.ayfri.kore.commands.Command

/**
 * Internal class used to represent an empty function.
 *
 * This is used to represent an argument inside a command, which can be any command, like `execute run ...` where `...` can be any command.
 */
class EmptyFunction internal constructor(datapack: DataPack = DataPack("")) : Function("", datapack.name, datapack = datapack) {
	override fun addLine(line: String) = ""
	override fun addLine(command: Command): Command {
		lines.clear()
		lines += command.toString()
		return command
	}

	override fun comment(comment: String) {}
	override fun clearLines() {}
	override fun toString() = ""
	override fun asId() = lines.getOrElse(0) { "" }
}

/**
 * Creates an empty function, without a datapack linked to it.
 * This is when you need to generate a command call without any context, like in a [ClickEvent.runCommand].
 * If you need this function to be public, please create an issue on the GitHub repository with your use case.
 */
internal fun emptyFunction(block: Function.() -> Unit = {}) = EmptyFunction().apply(block)

/**
 * Creates an empty function.
 */
fun emptyFunction(datapack: DataPack, block: Function.() -> Unit = {}) = EmptyFunction(datapack).apply(block)
