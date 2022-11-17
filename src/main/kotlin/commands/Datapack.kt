package commands

import arguments.literal
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(DatapackPriority.Companion.DatapackPrioritySerializer::class)
enum class DatapackPriority {
	FIRST,
	LAST;
	
	companion object {
		val values = values()
		
		object DatapackPrioritySerializer : LowercaseSerializer<DatapackPriority>(values)
	}
}

class Datapack(private val fn: Function, val name: String) {
	fun disable() = fn.addLine(command("datapack", fn.literal("disable"), fn.literal(name)))
	fun enable(priority: DatapackPriority? = null) = fn.addLine(command("datapack", fn.literal("enable"), fn.literal(name), fn.literal(priority?.asArg())))
	fun enableFirst() = fn.addLine(command("datapack", fn.literal("enable"), fn.literal("first"), fn.literal(name)))
	fun enableLast() = fn.addLine(command("datapack", fn.literal("enable"), fn.literal("last"), fn.literal(name)))
	fun enableBefore(name: String) = fn.addLine(command("datapack", fn.literal("enable"), fn.literal("before"), fn.literal(name), fn.literal(name)))
	fun enableAfter(name: String) = fn.addLine(command("datapack", fn.literal("enable"), fn.literal("after"), fn.literal(name), fn.literal(name)))
}

class Datapacks(private val fn: Function) {
	fun available() = fn.addLine(command("datapack", fn.literal("available")))
	fun enabled() = fn.addLine(command("datapack", fn.literal("enabled")))
	fun list() = fn.addLine(command("datapack", fn.literal("list")))
}

fun Function.datapack(name: String, block: Datapack.() -> Unit) = Datapack(this, name).apply(block)
val Function.datapacks get() = Datapacks(this)
