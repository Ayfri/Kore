package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

@Serializable(DatapackPriority.Companion.DatapackPrioritySerializer::class)
enum class DatapackPriority {
	FIRST,
	LAST;

	companion object {
		data object DatapackPrioritySerializer : LowercaseSerializer<DatapackPriority>(entries)
	}
}

class DataPackCommandEntry(private val fn: Function, val name: String) {
	fun disable() = fn.addLine(command("datapack", literal("disable"), literal(name)))
	fun enable(priority: DatapackPriority? = null) =
		fn.addLine(command("datapack", literal("enable"), literal(name), literal(priority?.asArg())))

	fun enableFirst() = fn.addLine(command("datapack", literal("enable"), literal("first"), literal(name)))
	fun enableLast() = fn.addLine(command("datapack", literal("enable"), literal("last"), literal(name)))
	fun enableBefore(name: String) =
		fn.addLine(command("datapack", literal("enable"), literal(this.name), literal("before"), literal(name)))

	fun enableAfter(name: String) = fn.addLine(command("datapack", literal("enable"), literal(this.name), literal("after"), literal(name)))
}

class DataPacksCommandEntries(private val fn: Function) {
	fun available() = fn.addLine(command("datapack", literal("list"), literal("available")))
	fun enabled() = fn.addLine(command("datapack", literal("list"), literal("enabled")))
	fun list() = fn.addLine(command("datapack", literal("list")))
}

val Function.dataPacks get() = DataPacksCommandEntries(this)
fun Function.dataPack(name: String, block: DataPackCommandEntry.() -> Command) = DataPackCommandEntry(this, name).block()
