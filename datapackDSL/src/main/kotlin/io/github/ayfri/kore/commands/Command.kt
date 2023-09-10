package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.Argument

data class Command(val name: String, var hasMacro: Boolean = false) {
	private val macroPrefix get() = if (hasMacro) "$" else ""
	val arguments = mutableListOf<Argument>()
	override fun toString() = "$macroPrefix$name ${arguments.joinToString(" ", transform = Argument::asString)}".trim()
}

fun command(name: String, vararg arguments: Argument?) = Command(name).apply { this.arguments.addAll(arguments.filterNotNull()) }
