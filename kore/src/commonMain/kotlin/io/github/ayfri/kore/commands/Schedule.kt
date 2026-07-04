package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.time
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction

/** DSL scope for a scheduled function reference. */
class ScheduleFunction(private val fn: Function, val function: String) {
	/** Schedules [function] to run again after [time] from now. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
	fun append(time: TimeNumber) = fn.addLine(command("schedule", literal("function"), literal(function), time(time), literal("append")))
	/** Cancels the pending run for [function]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
	fun clear() = fn.addLine(command("schedule", literal("clear"), literal(function)))
	/** Replaces any pending run for [function] with [time]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
	fun replace(time: TimeNumber) = fn.addLine(command("schedule", literal("function"), literal(function), time(time), literal("replace")))
}

/** DSL scope for the `/schedule` command. */
class Schedule(private val fn: Function) {
	/** Schedules the named [function] to run again after [time] from now. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
	fun append(function: String, time: TimeNumber) =
		fn.addLine(command("schedule", literal("function"), literal(function), time(time), literal("append")))

	/** Schedules [function] to run again after [time] from now. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
	fun append(function: FunctionArgument, time: TimeNumber) =
		fn.addLine(command("schedule", literal("function"), function, time(time), literal("append")))

	/** Cancels the pending run for the named [function]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
	fun clear(function: String) = fn.addLine(command("schedule", literal("clear"), literal(function)))
	/** Cancels the pending run for [function]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
	fun clear(function: FunctionArgument) = fn.addLine(command("schedule", literal("clear"), function))
	/** Replaces the pending run for the named [function] with [time]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
	fun replace(function: String, time: TimeNumber) =
		fn.addLine(command("schedule", literal("function"), literal(function), time(time), literal("replace")))

	/** Replaces the pending run for [function] with [time]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
	fun replace(function: FunctionArgument, time: TimeNumber) =
		fn.addLine(command("schedule", literal("function"), function, time(time), literal("replace")))
}

/** Returns the reusable [Schedule] DSL. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
val Function.schedules get() = Schedule(this)
/** Opens the [Schedule] DSL. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
fun Function.schedules(block: Schedule.() -> Command) = Schedule(this).block()
/** Returns the reusable [ScheduleFunction] DSL for [function]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
fun Function.schedule(function: String) = ScheduleFunction(this, function)
/** Returns the reusable [ScheduleFunction] DSL for [function]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
fun Function.schedule(function: FunctionArgument) = ScheduleFunction(this, function.asString())

/** Schedules [function] to run after [delay]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
fun Function.schedule(delay: TimeNumber, function: String) = ScheduleFunction(this, function).also { it.append(delay) }
/** Schedules [function] to run after [delay]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
fun Function.schedule(delay: TimeNumber, function: FunctionArgument) =
	ScheduleFunction(this, function.asString()).also { it.append(delay) }

/** Schedules the generated function built from [function] to run after [delay]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/schedule) */
fun Function.schedule(delay: TimeNumber, function: Function.() -> Unit) =
	schedule(delay, datapack.generatedFunction("schedule_${function.hashCode()}") { function() })
