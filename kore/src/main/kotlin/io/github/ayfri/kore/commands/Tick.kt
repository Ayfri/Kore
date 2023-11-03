package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.time
import io.github.ayfri.kore.functions.Function

class Tick(private val fn: Function) {
	fun freeze() = fn.addLine(command("tick", literal("freeze")))
	fun query() = fn.addLine(command("tick", literal("query")))
	fun rate(rate: Float) = fn.addLine(command("tick", literal("rate"), float(rate)))
	fun sprint(time: Int) = fn.addLine(command("tick", literal("sprint"), int(time)))
	fun sprint(time: TimeNumber) = fn.addLine(command("tick", literal("sprint"), time(time)))
	fun sprintStop() = fn.addLine(command("tick", literal("sprint"), literal("stop")))
	fun stepStop() = fn.addLine(command("tick", literal("step"), literal("stop")))
	fun step() = fn.addLine(command("tick", literal("step")))
	fun step(time: Int) = fn.addLine(command("tick", literal("step"), int(time)))
	fun step(time: TimeNumber) = fn.addLine(command("tick", literal("step"), time(time)))
	fun unfreeze() = fn.addLine(command("tick", literal("unfreeze")))
}

val Function.tick get() = Tick(this)
fun Function.tick(block: Tick.() -> Command) = Tick(this).block()
