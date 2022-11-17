package functions

import DataPack
import commands.Command
import java.io.File

class Function(val name: String, val namespace: String = "minecraft", var directory: String = "", val datapack: DataPack) {
	val lines = mutableListOf<String>()
	
	fun addBlankLine() = lines.add("")
	
	fun addLine(line: String) {
		lines.add(line)
	}
	
	fun addLine(command: Command): Command {
		lines.add(command.toString())
		return command
	}
	
	fun comment(comment: String) {
		lines.add("# $comment")
	}
	
	fun generate(functionsDir: File) {
		val file = File(functionsDir, "$directory/$name.mcfunction")
		file.parentFile.mkdirs()
		file.writeText(lines.joinToString("\n"))
	}
	
	fun clear() = lines.clear()
	
	override fun toString() = lines.joinToString("\n")
	
	companion object {
		val EMPTY = Function("", datapack = DataPack(""))
	}
}

fun DataPack.function(name: String, namespace: String = this.name, directory: String = "", block: Function.() -> Unit) {
	addFunction(Function(name, namespace, directory, this).apply(block))
}
