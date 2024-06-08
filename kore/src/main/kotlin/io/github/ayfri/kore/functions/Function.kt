package io.github.ayfri.kore.functions

import io.github.ayfri.kore.Configuration
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.*
import io.github.ayfri.kore.arguments.chatcomponents.events.ClickAction
import io.github.ayfri.kore.arguments.chatcomponents.events.showText
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.FunctionTagArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.features.tags.addToTag
import io.github.ayfri.kore.utils.ifNotEmpty
import java.io.File

open class Function(
	override val name: String,
	override val namespace: String = "minecraft",
	override var directory: String = "",
	val datapack: DataPack,
) : FunctionArgument {
	private var debug = false
	internal var nextLineHasMacro = false
	val lines = mutableListOf<String>()

	fun addBlankLine() = lines.add("")

	private fun handleMacro(line: String): String {
		if (nextLineHasMacro) {
			if (Regex(Macros.MACRO_REGEX) in line && !line.startsWith('$')) return "$$line"
			nextLineHasMacro = false
		}

		return line
	}

	open fun addLine(line: String) = handleMacro(line).also { lines += it }

	open fun addLine(command: Command): Command {
		lines += handleMacro(command.toString())

		if (nextLineHasMacro) command.hasMacro = true

		if (debug) lines += "tellraw @a ${
			textComponent("/$command") {
				clickEvent(ClickAction.SUGGEST_COMMAND) {
					value = "/$command"
				}

				hoverEvent {
					showText("Click to copy command") {
						italic = true
						color = Color.GRAY
					}
				}
			}.toJsonString()
		}"

		return command
	}

	open fun comment(comment: String) {
		lines.add("# $comment")
	}

	fun getFinalPath() = "data/$namespace/functions/${directory.ifNotEmpty { "$it/" }}$name.mcfunction"

	fun generate(directory: File) {
		val file = File(directory, "${this.directory}/$name.mcfunction")
		file.parentFile.mkdirs()

		if (debug) {
			lines.add(0, command("tellraw", allPlayers(), (textComponent("Running function ") {
				color = Color.GRAY
				italic = true
			} + textComponent(asId()) {
				color = Color.WHITE
				bold = true
				italic = true

				clickEvent(ClickAction.RUN_COMMAND) {
					value = "/function ${asId()}"
				}

				hoverEvent {
					showText("Click to execute function") {
						italic = true
						color = Color.GRAY
					}
				}
			}).asJsonArg()).toString())

			lines.add(command("tellraw", allPlayers(), (textComponent("Finished running function ", Color.GRAY) {
				italic = true
			} + text(asId(), Color.WHITE) {
				bold = true
				italic = true

				clickEvent(ClickAction.RUN_COMMAND) {
					value = "/function ${asId()}"
				}

				hoverEvent {
					showText("Click to execute function") {
						italic = true
						color = Color.GRAY
					}
				}
			}).asJsonArg()).toString())
		}

		file.writeText(toString())
	}

	open fun clearLines() = lines.clear()

	open fun startDebug() {
		debug = true
	}

	open fun endDebug() {
		debug = false
	}

	open fun debug(block: Function.() -> Unit) {
		startDebug()
		apply(block)
		endDebug()
	}

	open fun debug(text: String, color: Color? = null, options: ChatComponent.() -> Unit = {}) = debug(textComponent(text) {
		color?.let { this.color = it }
		options()
	})

	open fun debug(textComponent: ChatComponents) = tellraw(allPlayers(), textComponent)

	override fun toString() = lines.joinToString("\n")
}

/**
 * Creates a new function with a name and a lambda block.
 * You can also specify the namespace and the directory of the function.
 */
fun DataPack.function(name: String, namespace: String = this.name, directory: String = "", block: Function.() -> Unit) =
	addFunction(Function(name, namespace, directory, this).apply(block))

/**
 * Generates a function with a name and a lambda block.
 * The directory of the generated function is [Configuration.generatedFunctionsFolder] which by default is equal to [DataPack.DEFAULT_GENERATED_FUNCTIONS_FOLDER].
 *
 * **Note:** Generated functions with same content will be merged into one function, the first one will be used and the others will be ignored.
 */
fun DataPack.generatedFunction(name: String, namespace: String = this.name, directory: String = "", block: Function.() -> Unit) =
	addGeneratedFunction(
		Function(name, namespace, "${configuration.generatedFunctionsFolder}${directory.ifNotEmpty { "/$it" }}", this).apply(block)
	)

/**
 * Generates a function call with a prefix and a lambda block.
 * If the function has only one command, it will return the command itself.
 * Else if the function has more than one command, it will generate a new function with the block and return a function call to it.
 *
 * The generated name has the pattern:
 * ```kotlin
 * "${prefix}_${hashCode()}"
 * ```
 *
 * This functions is useful when you need to have a function call in a command or a command call like in the [Function.execute] command.
 *
 * **Note:** Generated functions with same content will be merged into one function, the first one will be used and the others will be ignored.
 */
fun Function.generatedFunctionCall(
	prefix: String,
	namespace: String = datapack.name,
	directory: String = "",
	block: Function.() -> Unit,
): Command {
	val name = "${prefix}_${hashCode()}"
	val function = Function("", "", "", datapack).apply { block() }
	if (function.lines.size == 1) {
		return command(function.lines.first())
	}

	val generatedFunction = datapack.generatedFunction(name, namespace, directory) {
		comment("Generated function ${asString()}")
		block()
	}
	return Function("", "", "", datapack).function(generatedFunction.name)
}

fun DataPack.load(name: String? = null, namespace: String = this.name, directory: String = "", block: Function.() -> Unit) =
	addToMinecraftTag("load", name, block, namespace, directory)

fun DataPack.tick(name: String? = null, namespace: String = this.name, directory: String = "", block: Function.() -> Unit) =
	addToMinecraftTag("tick", name, block, namespace, directory)

private fun DataPack.addToMinecraftTag(
	fileName: String,
	functionName: String?,
	block: Function.() -> Unit,
	namespace: String = this.name,
	directory: String,
): FunctionArgument {
	val name = functionName ?: "${fileName}_${block.hashCode()}"
	val generatedFunction = generatedFunction(name, namespace, directory, block)
	addToTag<FunctionTagArgument>(fileName, type = "functions", namespace = "minecraft") {
		this += generatedFunction.asId()
	}

	return generatedFunction
}

fun Function.setTag(
	tagFile: String,
	tagNamespace: String = namespace,
	entryNamespace: String = namespace,
	entryIsTag: Boolean = false,
	entryIsRequired: Boolean? = null,
) {
	datapack.addToTag<FunctionTagArgument>(fileName = tagFile, type = "functions", namespace = tagNamespace) {
		add(name, entryNamespace, entryIsTag, entryIsRequired)
	}
}

fun Function.setTag(
	tag: FunctionTagArgument,
	entryNamespace: String = namespace,
	entryIsTag: Boolean = false,
	entryIsRequired: Boolean? = null,
) {
	datapack.addToTag<FunctionTagArgument>(fileName = tag.name, type = "functions", namespace = tag.namespace) {
		add(name, entryNamespace, entryIsTag, entryIsRequired)
	}
}
