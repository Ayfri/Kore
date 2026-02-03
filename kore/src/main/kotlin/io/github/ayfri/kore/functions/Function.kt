package io.github.ayfri.kore.functions

import io.github.ayfri.kore.Configuration
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.*
import io.github.ayfri.kore.arguments.actions.runCommand
import io.github.ayfri.kore.arguments.actions.suggestCommand
import io.github.ayfri.kore.arguments.chatcomponents.hover.showText
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.FunctionTagArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.features.tags.functionTag
import io.github.ayfri.kore.utils.ifNotEmpty
import java.io.File

/**
 * Represents a Minecraft function (.mcfunction) being built in memory.
 *
 * This class collects lines (commands and comments) and is able to write
 * the final `.mcfunction` file when requested. It also supports a debug
 * mode that injects `tellraw` commands showing the executed commands.
 *
 * Docs: https://kore.ayfri.com/docs/commands/functions
 * Macro reference: https://kore.ayfri.com/docs/commands/macros
 *
 * @param name the function name (without extension)
 * @param namespace the function namespace, defaults to `minecraft`
 * @param directory optional directory within `data/<namespace>/functions/`
 * @param datapack the owning [DataPack] instance
 */
open class Function(
	override val name: String,
	override val namespace: String = "minecraft",
	override var directory: String = "",
 	val datapack: DataPack,
) : FunctionArgument {
	private var debug = false
	internal var nextLineHasMacro = false
	val lines = mutableListOf<String>()
	val commandLines get() = lines.filter { !it.startsWith('#') && it.isNotBlank() && it.isNotEmpty() }
	val isInlinable get() = commandLines.size == 1

	/** Adds an empty blank line to the function. */
	fun addBlankLine() = lines.add("")

	private fun handleMacro(line: String): String {
		if (nextLineHasMacro) {
			if (Regex(Macros.MACRO_REGEX) in line && !line.startsWith('$')) return "$$line"
			nextLineHasMacro = false
		}

		return line
	}

	/**
	 * Adds a raw line (usually a command or a comment) to the function.
	 * The line will be processed to handle macros before being stored.
	 */
	open fun addLine(line: String) = handleMacro(line).also { lines += it }

	/**
	 * Adds a [Command] to the function and stores its string form.
	 * When debug mode is enabled, a `tellraw` entry showing the command
	 * will also be appended.
	 * Returns the given [Command] for fluent usage.
	 */
	open fun addLine(command: Command): Command {
		lines += handleMacro(command.toString())

		if (nextLineHasMacro) command.hasMacro = true

		if (debug) lines += "tellraw @a ${
			textComponent("/$command") {
				clickEvent {
					suggestCommand { 
						command
					}
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

	/** Adds a comment line (starts with '#'). */
	open fun comment(comment: String) {
		lines.add("# $comment")
	}

	/**
	 * Returns the output path of this function inside the datapack (relative).
	 * Example: `data/<namespace>/function/<directory>/<name>.mcfunction`.
	 */
	fun getFinalPath() = "data/$namespace/function/${directory.ifNotEmpty { "$it/" }}$name.mcfunction"

	/**
	 * Writes the function to disk under the given output directory.
	 * Creates parent directories if required and injects debug markers
	 * when debug mode is active.
	 */
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

				clickEvent {
					runCommand {
						function(asId())
					}
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

				clickEvent {
					runCommand {
						function(asId())
					}
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

	/** Clears all lines from the function. Useful for reusing the instance. */
	open fun clearLines() = lines.clear()

	/** Enable debug mode which augments the function with tellraw messages. */
	open fun startDebug() {
		debug = true
	}

	/** Disable debug mode. */
	open fun endDebug() {
		debug = false
	}

	/**
	 * Runs the provided [block] with debug mode enabled for the duration
	 * of the block, then disables debug mode.
	 */
	open fun debug(block: Function.() -> Unit) {
		startDebug()
		apply(block)
		endDebug()
	}

	/** Convenience helper to send a debug chat message with optional color. */
	open fun debug(text: String, color: Color? = null, options: ChatComponent.() -> Unit = {}) = debug(textComponent(text) {
		color?.let { this.color = it }
		options()
	})

	/** Sends a `tellraw` debug message to all players. */
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

/** Generate a function and register it in the load tag, executed once on world load, or when a `/reload` is executed. */
fun DataPack.load(name: String? = null, namespace: String = this.name, directory: String = "", block: Function.() -> Unit) =
	addToMinecraftTag("load", name, block, namespace, directory)

/** Generate a function and register it in the tick tag, executed every tick, 20 times per second. */
fun DataPack.tick(name: String? = null, namespace: String = this.name, directory: String = "", block: Function.() -> Unit) =
	addToMinecraftTag("tick", name, block, namespace, directory)

/** Generate a function and register it in a tag. */
private fun DataPack.addToMinecraftTag(
	fileName: String,
	functionName: String?,
	block: Function.() -> Unit,
	namespace: String = this.name,
	directory: String,
): FunctionArgument {
	val name = functionName ?: "${fileName}_${block.hashCode()}"
	val generatedFunction = generatedFunction(name, namespace, directory, block)
	functionTag(fileName, namespace = "minecraft") {
		this += generatedFunction.asId()
	}

	return generatedFunction
}

/** Registers the function in a tag. */
fun Function.setTag(
	tagFile: String,
	tagNamespace: String = namespace,
	entryNamespace: String = namespace,
	entryIsTag: Boolean = false,
	entryIsRequired: Boolean? = null,
) = datapack.functionTag(tagFile, tagNamespace) {
	add(name, entryNamespace, entryIsTag, entryIsRequired)
}

/** Registers the function in a tag. */
fun Function.setTag(
	tag: FunctionTagArgument,
	entryNamespace: String = namespace,
	entryIsTag: Boolean = false,
	entryIsRequired: Boolean? = null,
) = datapack.functionTag(tag.name, tag.namespace) {
	add(name, entryNamespace, entryIsTag, entryIsRequired)
}
