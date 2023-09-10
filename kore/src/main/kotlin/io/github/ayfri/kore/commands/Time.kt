package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.TimeType
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

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

class Time(private val fn: Function) {
	fun add(value: Int) = fn.addLine(command("time", literal("add"), int(value)))
	fun query(type: TimeType) = fn.addLine(command("time", literal("query"), literal(type.commandName)))
	fun set(value: Int) = fn.addLine(command("time", literal("set"), int(value)))
	fun set(period: TimePeriod) = fn.addLine(command("time", literal("set"), literal(period.asArg())))
}

val Function.time get() = Time(this)
fun Function.time(block: Time.() -> Command) = Time(this).block()
