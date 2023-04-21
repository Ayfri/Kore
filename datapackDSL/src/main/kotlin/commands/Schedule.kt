package commands

import arguments.Argument
import arguments.literal
import arguments.numbers.TimeNumber
import arguments.time
import functions.Function
import functions.generatedFunction

class ScheduleFunction(private val fn: Function, val function: String) {
	fun append(time: TimeNumber) = fn.addLine(command("schedule", literal("function"), literal(function), time(time), literal("append")))
	fun clear() = fn.addLine(command("schedule", literal("clear"), literal(function)))
	fun replace(time: TimeNumber) = fn.addLine(command("schedule", literal("function"), literal(function), time(time), literal("replace")))
}

class Schedule(private val fn: Function) {
	fun append(function: String, time: TimeNumber) =
		fn.addLine(command("schedule", literal("function"), literal(function), time(time), literal("append")))

	fun append(function: Argument.Function, time: TimeNumber) =
		fn.addLine(command("schedule", literal("function"), function, time(time), literal("append")))

	fun clear(function: String) = fn.addLine(command("schedule", literal("clear"), literal(function)))
	fun clear(function: Argument.Function) = fn.addLine(command("schedule", literal("clear"), function))
	fun replace(function: String, time: TimeNumber) =
		fn.addLine(command("schedule", literal("function"), literal(function), time(time), literal("replace")))

	fun replace(function: Argument.Function, time: TimeNumber) =
		fn.addLine(command("schedule", literal("function"), function, time(time), literal("replace")))
}

val Function.schedules get() = Schedule(this)
fun Function.schedules(block: Schedule.() -> Command) = Schedule(this).block()
fun Function.schedule(function: String) = ScheduleFunction(this, function)
fun Function.schedule(function: Argument.Function) = ScheduleFunction(this, function.asString())

fun Function.schedule(delay: TimeNumber, function: String) = ScheduleFunction(this, function).also { it.append(delay) }
fun Function.schedule(delay: TimeNumber, function: Argument.Function) =
	ScheduleFunction(this, function.asString()).also { it.append(delay) }

fun Function.schedule(delay: TimeNumber, function: Function.() -> Unit) =
	schedule(delay, datapack.generatedFunction("schedule_${function.hashCode()}") { function() })
