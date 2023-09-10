package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.time
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction

class ScheduleFunction(private val fn: Function, val function: String) {
	fun append(time: TimeNumber) = fn.addLine(command("schedule", literal("function"), literal(function), time(time), literal("append")))
	fun clear() = fn.addLine(command("schedule", literal("clear"), literal(function)))
	fun replace(time: TimeNumber) = fn.addLine(command("schedule", literal("function"), literal(function), time(time), literal("replace")))
}

class Schedule(private val fn: Function) {
	fun append(function: String, time: TimeNumber) =
		fn.addLine(command("schedule", literal("function"), literal(function), time(time), literal("append")))

	fun append(function: FunctionArgument, time: TimeNumber) =
		fn.addLine(command("schedule", literal("function"), function, time(time), literal("append")))

	fun clear(function: String) = fn.addLine(command("schedule", literal("clear"), literal(function)))
	fun clear(function: FunctionArgument) = fn.addLine(command("schedule", literal("clear"), function))
	fun replace(function: String, time: TimeNumber) =
		fn.addLine(command("schedule", literal("function"), literal(function), time(time), literal("replace")))

	fun replace(function: FunctionArgument, time: TimeNumber) =
		fn.addLine(command("schedule", literal("function"), function, time(time), literal("replace")))
}

val Function.schedules get() = Schedule(this)
fun Function.schedules(block: Schedule.() -> Command) = Schedule(this).block()
fun Function.schedule(function: String) = ScheduleFunction(this, function)
fun Function.schedule(function: FunctionArgument) = ScheduleFunction(this, function.asString())

fun Function.schedule(delay: TimeNumber, function: String) = ScheduleFunction(this, function).also { it.append(delay) }
fun Function.schedule(delay: TimeNumber, function: FunctionArgument) =
	ScheduleFunction(this, function.asString()).also { it.append(delay) }

fun Function.schedule(delay: TimeNumber, function: Function.() -> Unit) =
	schedule(delay, datapack.generatedFunction("schedule_${function.hashCode()}") { function() })
