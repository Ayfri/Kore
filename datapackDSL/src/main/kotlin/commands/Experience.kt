package commands

import arguments.numbers.Xp
import arguments.types.EntityArgument
import arguments.types.literals.literal
import functions.Function

class Experience(private val fn: Function, val target: EntityArgument) {
	fun add(amount: Xp) = fn.addLine(command("experience", literal("add"), target, literal(amount.toString())))
	fun queryLevels() = fn.addLine(command("experience", literal("query"), target, literal("levels")))
	fun queryPoints() = fn.addLine(command("experience", literal("query"), target, literal("points")))
	fun remove(amount: Xp) = add(-amount)
	fun set(amount: Xp) = fn.addLine(command("experience", literal("set"), target, literal(amount.toString())))

	operator fun plusAssign(amount: Xp) {
		add(amount)
	}

	operator fun minusAssign(amount: Xp) {
		remove(amount)
	}
}

fun Function.experience(target: EntityArgument) = Experience(this, target)
fun Function.experience(target: EntityArgument, block: Experience.() -> Command) = Experience(this, target).block()
fun Function.xp(target: EntityArgument) = Experience(this, target)
fun Function.xp(target: EntityArgument, block: Experience.() -> Command) = Experience(this, target).block()
