package commands

import arguments.literal
import arguments.numbers.TimeNumber
import arguments.time
import functions.Function

class Schedule(private val fn: Function) {
	fun append(function: String, time: TimeNumber) = fn.addLine(
		command(
			"schedule", fn.literal("function"), fn.literal(function), fn.time(time), fn.literal("append")
		)
	)
	
	fun clear(function: String) = fn.addLine(
		command(
			"schedule", fn.literal("function"), fn.literal(function), fn.literal("clear")
		)
	)
	
	fun replace(function: String, time: TimeNumber) = fn.addLine(
		command(
			"schedule", fn.literal("function"), fn.literal(function), fn.time(time), fn.literal("replace")
		)
	)
}

fun Function.schedule(block: Schedule.() -> Unit) = Schedule(this).apply(block)
