package commands

import arguments.int
import arguments.literal
import arguments.numbers.TimeType
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(TimeSpec.Companion.TimeSpecSerializer::class)
enum class TimeSpec {
	DAY,
	NOON,
	NIGHT,
	MIDNIGHT;
	
	companion object {
		val values = values()
		
		object TimeSpecSerializer : LowercaseSerializer<TimeSpec>(values)
	}
}

class Time(private val fn: Function) {
	fun add(value: Int) = fn.addLine(command("time", fn.literal("add"), fn.int(value)))
	fun query(type: TimeType) = fn.addLine(command("time", fn.literal("query"), fn.literal(type.asArg())))
	fun set(value: Int) = fn.addLine(command("time", fn.literal("set"), fn.int(value)))
	fun set(spec: TimeSpec) = fn.addLine(command("time", fn.literal("set"), fn.literal(spec.asArg())))
}

val Function.time get() = Time(this)
