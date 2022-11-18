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

class DataPack(private val fn: Function, val name: String) {
	fun disable() = fn.addLine(command("datapack", literal("disable"), literal(name)))
	fun enable(priority: DatapackPriority? = null) = fn.addLine(command("datapack", literal("enable"), literal(name), literal(priority?.asArg())))
	fun enableFirst() = fn.addLine(command("datapack", literal("enable"), literal("first"), literal(name)))
	fun enableLast() = fn.addLine(command("datapack", literal("enable"), literal("last"), literal(name)))
	fun enableBefore(name: String) = fn.addLine(command("datapack", literal("enable"), literal("before"), literal(name), literal(name)))
	fun enableAfter(name: String) = fn.addLine(command("datapack", literal("enable"), literal("after"), literal(name), literal(name)))
}

class DataPacks(private val fn: Function) {
	fun available() = fn.addLine(command("datapack", literal("available")))
	fun enabled() = fn.addLine(command("datapack", literal("enabled")))
	fun list() = fn.addLine(command("datapack", literal("list")))
}

val Function.dataPacks get() = DataPacks(this)
fun Function.dataPack(name: String, block: DataPack.() -> Unit) = DataPack(this, name).apply(block)
