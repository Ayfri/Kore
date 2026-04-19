package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.numbers.TimeType
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.time
import io.github.ayfri.kore.functions.Function
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
 * DSL scope for the `/time` command.
 *
 * `/time` changes or queries the world's clock. Use [add] to advance the current clock, [query]
 * to read the current value, and [set] to move the clock to a specific value or one of the built-
 * in markers such as day, noon, night, or midnight.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
 */
data class Time(private val fn: Function) {
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
	 * Queries the current time using the token mapped by [type].
	 *
	 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/time)
	 */
	fun query(type: TimeType) = fn.addLine(command("time", literal("query"), literal(type.commandName)))

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
}

/** Returns the reusable [Time] DSL. */
val Function.time get() = Time(this)

/** Opens the [Time] DSL. */
fun Function.time(block: Time.() -> Command) = Time(this).block()
