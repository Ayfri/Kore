package commands

import arguments.int
import arguments.literal
import functions.Function

class Trigger(private val fn: Function, val objective: String) {
	fun add(value: Int) = fn.addLine(command("trigger", fn.literal(objective), fn.literal("add"), fn.int(value)))
	fun set(value: Int) = fn.addLine(command("trigger", fn.literal(objective), fn.literal("set"), fn.int(value)))
	
	operator fun plusAssign(value: Int) {
		add(value)
	}
	
	operator fun minusAssign(value: Int) {
		add(-value)
	}
}

fun Function.trigger(objective: String, block: Trigger.() -> Unit) = Trigger(this, objective).block()
