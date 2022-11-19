package functions

import DataPack
import commands.Command
import tags.addToTag
import java.io.File

open class Function(val name: String, val namespace: String = "minecraft", var directory: String = "", val datapack: DataPack) {
	val lines = mutableListOf<String>()
	
	fun addBlankLine() = lines.add("")
	
	open fun addLine(line: String) {
		lines.add(line)
	}
	
	open fun addLine(command: Command): Command {
		lines.add(command.toString())
		return command
	}
	
	open fun comment(comment: String) {
		lines.add("# $comment")
	}
	
	open fun generate(functionsDir: File) {
		val file = File(functionsDir, "$directory/$name.mcfunction")
		file.parentFile.mkdirs()
		file.writeText(lines.joinToString("\n"))
	}
	
	open fun clear() = lines.clear()
	
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

fun Function.setTag(tagFile: String, tagNamespace: String = namespace, entryNamespace: String = namespace, group: Boolean = false, required: Boolean? = null) {
	datapack.addToTag(tagNamespace, "functions", tagFile) {
		add(name, entryNamespace, group, required)
	}
}
