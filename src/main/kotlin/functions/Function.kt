package functions

import DataPack
import commands.Command
import java.io.File

class Function(val name: String, val namespace: String = "minecraft", val lines: MutableList<String> = mutableListOf()) {
	var directory = ""
	
	fun addBlank() = lines.add("")
	
	fun addLine(line: String) {
		lines.add(line)
	}
	
	fun addLine(command: Command) {
		lines.add(command.toString())
	}
	
	fun comment(comment: String) {
		lines.add("# $comment")
	}
	
	fun generate(functionsDir: File) {
		val file = File(functionsDir, "$directory/$name.mcfunction")
		file.parentFile.mkdirs()
		file.writeText(lines.joinToString("\n"))
	}
	
	override fun toString() = lines.joinToString("\n")
}

fun DataPack.function(name: String, namespace: String = this.name, directory: String = "", block: Function.() -> Unit) {
	val function = Function(name, namespace)
	function.directory = directory
	function.block()
	functions.add(function)
}
