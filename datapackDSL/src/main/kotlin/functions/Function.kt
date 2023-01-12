package functions

import DataPack
import arguments.*
import commands.Command
import commands.tellraw
import tags.addToTag
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
				textComponent(textComponent {
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
				}).asString()
			}"
		)
		return command
	}

	open fun comment(comment: String) {
		lines.add("# $comment")
	}

	open fun generate(functionsDir: File) {
		val file = File(functionsDir, "$directory/$name.mcfunction")
		file.parentFile.mkdirs()

		val text = when {
			debug -> """
				|tellraw @a ${
				textComponent(textComponent {
					text = "Running function "
					color = Color.GRAY
				} + textComponent {
					text = "$namespace:$name"
					color = Color.WHITE
					bold = true
				}).asString()
			}
				|
				|$this
				|
				|tellraw @a ${
				textComponent(textComponent {
					text = "Finished running function "
					color = Color.GRAY
				} + textComponent {
					text = "$namespace:$name"
					color = Color.WHITE
					bold = true
				}).asString()
			}
			""".trimMargin()

			else -> toString()
		}

		file.writeText(text)
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
		block()
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
			override fun generate(functionsDir: File) {}
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
