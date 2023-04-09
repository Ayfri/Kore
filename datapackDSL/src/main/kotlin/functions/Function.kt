package functions

import DataPack
import Generator
import arguments.Argument
import arguments.ChatComponents
import arguments.Color
import arguments.allPlayers
import arguments.chatcomponents.*
import arguments.chatcomponents.events.ClickAction
import arguments.chatcomponents.events.showText
import commands.Command
import commands.command
import commands.tellraw
import features.tags.addToTag
import utils.ifNotEmpty
import java.io.File

open class Function(
	override val name: String,
	override val namespace: String = "minecraft",
	override var directory: String = "",
	val datapack: DataPack
) : Generator, Argument.Function {
	val lines = mutableListOf<String>()
	private var debug = false

	fun addBlankLine() = lines.add("")

	open fun addLine(line: String) {
		lines.add(line)
	}

	open fun addLine(command: Command): Command {
		lines.add(command.toString())
		if (debug) lines.add(
			"tellraw @a ${
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
		)

		return command
	}

	open fun comment(comment: String) {
		lines.add("# $comment")
	}

	override fun generate(dataPack: DataPack, directory: File) {
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

	open fun clear() = lines.clear()

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

	companion object {
		val EMPTY = object : Function("", datapack = DataPack("")) {
			override fun addLine(line: String) {}
			override fun addLine(command: Command): Command {
				lines.clear()
				lines += command.toString()
				return command
			}

			override fun comment(comment: String) {}
			override fun generate(dataPack: DataPack, directory: File) {}
			override fun clear() {}
			override fun toString() = ""
			override fun asId() = lines.getOrElse(0) { "" }
		}
	}
}

fun DataPack.function(name: String, namespace: String = this.name, directory: String = "", block: Function.() -> Unit) =
	addFunction(Function(name, namespace, directory, this).apply(block))

fun DataPack.generatedFunction(name: String, directory: String = "", block: Function.() -> Unit) =
	addGeneratedFunction(
		Function(name, this.name, "${DataPack.GENERATED_FUNCTIONS_FOLDER}${directory.ifNotEmpty { "/$it" }}", this).apply(block)
	)

fun DataPack.load(name: String? = null, directory: String = "", block: Function.() -> Unit) =
	addToMinecraftTag("load", name, block, directory)

fun DataPack.tick(name: String? = null, directory: String = "", block: Function.() -> Unit) =
	addToMinecraftTag("tick", name, block, directory)

private fun DataPack.addToMinecraftTag(
	fileName: String,
	functionName: String?,
	block: Function.() -> Unit,
	directory: String
): Argument.Function {
	val name = functionName ?: "${fileName}_${block.hashCode()}"
	val generatedFunction = generatedFunction(name, directory, block)
	addToTag("minecraft", "functions", fileName) {
		this += generatedFunction.asId()
	}

	return generatedFunction
}

fun Function.setTag(
	tagFile: String,
	tagNamespace: String = namespace,
	entryNamespace: String = namespace,
	group: Boolean = false,
	required: Boolean? = null
) {
	datapack.addToTag(tagNamespace, "functions", tagFile) {
		add(this@setTag.name, entryNamespace, group, required)
	}
}
