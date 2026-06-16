package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.numbers.TimeType
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.time
import io.github.ayfri.kore.arguments.types.resources.TimeMarkerArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.TimelineArgument
import io.github.ayfri.kore.generated.arguments.types.WorldClockArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

/** Built-in markers accepted by `/time set`. */
@Serializable(TimePeriod.Companion.TimeSpecSerializer::class)
enum class TimePeriod {
	DAY,
	NOON,
	NIGHT,
	MIDNIGHT;

	companion object {
		data object TimeSpecSerializer : LowercaseSerializer<TimePeriod>(entries)
	}
}

/**
 * DSL scope for `/time` targeting a specific [clock].
 *
 * Every method on this class emits `time of <clock> <subcommand> …`. Obtain an instance via [Time.of].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
 */
data class TimeWithClock(private val fn: Function, private val clock: WorldClockArgument) {
	/**
	 * Adds [value] ticks to this clock.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun add(value: Int) =
		fn.addLine(command("time", literal("of"), clock, literal("add"), int(value)))

	/**
	 * Adds a [TimeNumber] duration to this clock.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun add(value: TimeNumber) =
		fn.addLine(command("time", literal("of"), clock, literal("add"), time(value)))

	/**
	 * Pauses this clock.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun pause() =
		fn.addLine(command("time", literal("of"), clock, literal("pause")))

	/**
	 * Queries the current time on this clock using the token mapped by [type].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun query(type: TimeType) =
		fn.addLine(command("time", literal("of"), clock, literal("query"), literal(type.commandName)))

	/**
	 * Queries the progress of a [timeline] on this clock.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun query(timeline: TimelineArgument) =
		fn.addLine(command("time", literal("of"), clock, literal("query"), timeline))

	/**
	 * Queries how many times a [timeline] has completed its full cycle on this clock.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun queryRepetitions(timeline: TimelineArgument) =
		fn.addLine(command("time", literal("of"), clock, literal("query"), timeline, literal("repetitions")))

	/**
	 * Queries the current absolute game time on this clock as an integer.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun queryTime() =
		fn.addLine(command("time", literal("of"), clock, literal("query"), literal("time")))

	/**
	 * Resumes this clock after a [pause].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun resume() =
		fn.addLine(command("time", literal("of"), clock, literal("resume")))

	/**
	 * Sets this clock to [value] ticks.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun set(value: Int) =
		fn.addLine(command("time", literal("of"), clock, literal("set"), int(value)))

	/**
	 * Sets this clock to a [TimeNumber] duration.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun set(value: TimeNumber) =
		fn.addLine(command("time", literal("of"), clock, literal("set"), time(value)))

	/**
	 * Sets this clock to a built-in marker such as day, noon, night, or midnight.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun set(period: TimePeriod) =
		fn.addLine(command("time", literal("of"), clock, literal("set"), literal(period.asArg())))

	/**
	 * Sets this clock to a named time-marker resource location.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun set(timemarker: TimeMarkerArgument) =
		fn.addLine(command("time", literal("of"), clock, literal("set"), timemarker))
}

/**
 * DSL scope for the `/time` command (default world clock).
 *
 * Use [of] to obtain a [TimeWithClock] scope that prefixes every subcommand with `of <clock>`.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
 */
data class Time(private val fn: Function) {
	/**
	 * Returns a [TimeWithClock] DSL scope that targets [clock] for all subcommands.
	 *
	 * Docs: `time of <clock> <subcommand>`
	 */
	fun of(clock: WorldClockArgument) = TimeWithClock(fn, clock)

	/**
	 * Adds [value] ticks to the current world clock.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun add(value: Int) = fn.addLine(command("time", literal("add"), int(value)))

	/**
	 * Adds a [TimeNumber] duration to the current world clock.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun add(value: TimeNumber) = fn.addLine(command("time", literal("add"), time(value)))

	/**
	 * Pauses the world clock.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun pause() = fn.addLine(command("time", literal("pause")))

	/**
	 * Queries the current time using the token mapped by [type].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun query(type: TimeType) = fn.addLine(command("time", literal("query"), literal(type.commandName)))

	/**
	 * Queries the progress of a [timeline].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun query(timeline: TimelineArgument) = fn.addLine(command("time", literal("query"), timeline))

	/**
	 * Queries how many times a [timeline] has completed its full cycle.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun queryRepetitions(timeline: TimelineArgument) =
		fn.addLine(command("time", literal("query"), timeline, literal("repetitions")))

	/**
	 * Queries the current absolute game time as an integer.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun queryTime() = fn.addLine(command("time", literal("query"), literal("time")))

	/**
	 * Resumes the world clock after a [pause].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun resume() = fn.addLine(command("time", literal("resume")))

	/**
	 * Sets the world clock to [value] ticks.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun set(value: Int) = fn.addLine(command("time", literal("set"), int(value)))

	/**
	 * Sets the world clock to a [TimeNumber] duration.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun set(value: TimeNumber) = fn.addLine(command("time", literal("set"), time(value)))

	/**
	 * Sets the world clock to a built-in marker such as day, noon, night, or midnight.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun set(period: TimePeriod) = fn.addLine(command("time", literal("set"), literal(period.asArg())))

	/**
	 * Sets the world clock to a named time-marker resource location.
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun set(timemarker: TimeMarkerArgument) = fn.addLine(command("time", literal("set"), timemarker))
}

/** Returns the reusable [Time] DSL. */
val Function.time get() = Time(this)

/** Opens the [Time] DSL. */
fun Function.time(block: Time.() -> Command) = Time(this).block()
