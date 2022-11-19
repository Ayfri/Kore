package commands

import arguments.literal
import arguments.numbers.TimeNumber
import arguments.time
import functions.Function

class Schedule(private val fn: Function) {
	fun append(function: String, time: TimeNumber) = fn.addLine(
		command(
			"schedule", literal("function"), literal(function), time(time), literal("append")
		)
	)
	
	fun clear(function: String) = fn.addLine(
		command(
			"schedule", literal("function"), literal(function), literal("clear")
		)
	)
	
	fun replace(function: String, time: TimeNumber) = fn.addLine(
		command(
			"schedule", literal("function"), literal(function), time(time), literal("replace")
		)
	)
}

val Function.schedules get() = Schedule(this)
fun Function.schedules(block: Schedule.() -> Command) = Schedule(this).block()
