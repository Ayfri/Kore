package functions

import DataPack
import arguments.*
import commands.Command
import commands.command
import commands.tellraw
import features.tags.addToTag
import java.io.File

open class Function(
	val name: String,
	val namespace: String = "minecraft",
	var directory: String = "",
	val datapack: DataPack
) {
	val lines = mutableListOf<String>()
	var debug = false

	fun addBlankLine() = lines.add("")

	open fun addLine(line: String) {
		lines.add(line)
	}

	open fun addLine(command: Command): Command {
		lines.add(command.toString())
		if (debug) lines.add(
			"tellraw @a ${
				textComponent {
					text = "/$command"

					clickEvent {
						action = ClickAction.SUGGEST_COMMAND
						value = "/$command".nbt
					}

					hoverEvent {
						action = HoverAction.SHOW_TEXT
						value = textComponent {
							text = "Click to copy command"
							italic = true
							color = Color.GRAY
						}.toNbtTag()
					}
				}.toJsonString()
			}"
		)

		return command
	}

	open fun comment(comment: String) {
		lines.add("# $comment")
	}

	override fun generate(directory: File) {
		val file = File(directory, "${this.directory}/$name.mcfunction")
		file.parentFile.mkdirs()

		if (debug) {
			lines.add(0, command("tellraw", allPlayers(), textComponent {
				text = "Running function "
				color = Color.GRAY
			} + textComponent {
				text = "$namespace:$name"
				color = Color.WHITE
				bold = true
			}).toString())

			tellraw(allPlayers(), textComponent {
				text = "Finished running function "
				color = Color.GRAY
			} + textComponent {
				text = "$namespace:$name"
				color = Color.WHITE
				bold = true
			})
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

	open fun debug(text: String, options: TextComponent.() -> Unit = {}) = debug(textComponent(text, options))

	open fun debug(textComponent: TextComponents) = tellraw(allPlayers(), textComponent)

	override fun toString() = lines.joinToString("\n")

	companion object {
		val EMPTY = object : Function("", datapack = DataPack("")) {
			override fun addLine(line: String) {}
			override fun addLine(command: Command) = command
			override fun comment(comment: String) {}
			override fun generate(directory: File) {}
			override fun clear() {}
			override fun toString() = ""
		}
	}
}

fun DataPack.function(name: String, namespace: String = this.name, directory: String = "", block: Function.() -> Unit) {
	addFunction(Function(name, namespace, directory, this).apply(block))
}

fun DataPack.generatedFunction(name: String, directory: String = "", block: Function.() -> Unit) =
	addGeneratedFunction(Function(name, this.name, directory, this).apply(block))

fun DataPack.load(name: String? = null, directory: String = "", block: Function.() -> Unit) =
	addToMinecraftTag("load", name, block, directory)

fun DataPack.tick(name: String? = null, directory: String = "", block: Function.() -> Unit) =
	addToMinecraftTag("tick", name, block, directory)

private fun DataPack.addToMinecraftTag(
	name: String,
	functionName: String?,
	block: Function.() -> Unit,
	directory: String
) {
	val function = Function("", "", "", this).apply(block)
	val name = functionName ?: "${name}_${function.hashCode()}"
	val finalName = generatedFunction(name, directory, block)
	val usageName = "${DataPack.GENERATED_FUNCTIONS_FOLDER}/$finalName"
	addToTag("minecraft", "functions", name) {
		add(usageName, "minecraft")
	}
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
