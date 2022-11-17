package commands

import arguments.Argument

data class Command(val name: String) {
	val arguments = mutableListOf<Argument>()
	override fun toString() = "$name ${arguments.joinToString(" ") { it.asString() }}"
}

fun command(name: String, vararg arguments: Argument?) = Command(name).apply { this.arguments.addAll(arguments.filterNotNull()) }
