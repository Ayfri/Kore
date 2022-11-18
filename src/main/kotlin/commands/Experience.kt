package commands

import arguments.Argument
import arguments.literal
import arguments.numbers.Xp
import functions.Function

class Experience(private val fn: Function, val target: Argument.Entity) {
	fun add(amount: Xp) = fn.addLine(command("experience", fn.literal("add"), target, fn.literal(amount.toString())))
	fun set(amount: Xp) = fn.addLine(command("experience", fn.literal("set"), target, fn.literal(amount.toString())))
	fun queryLevels() = fn.addLine(command("experience", fn.literal("query"), target, fn.literal("levels")))
	fun queryPoints() = fn.addLine(command("experience", fn.literal("query"), target, fn.literal("points")))
	
	operator fun plusAssign(amount: Xp) {
		add(amount)
	}
}

fun Function.experience(target: Argument.Entity, block: Experience.() -> Unit) = Experience(this, target).apply(block)
