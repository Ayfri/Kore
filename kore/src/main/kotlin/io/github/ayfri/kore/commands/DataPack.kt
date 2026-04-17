package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

/** Load order used by `datapack enable`. */
@Serializable(DatapackPriority.Companion.DatapackPrioritySerializer::class)
enum class DatapackPriority {
	FIRST,
	LAST;

	companion object {
		data object DatapackPrioritySerializer : LowercaseSerializer<DatapackPriority>(entries)
	}
}

/**
 * DSL scope for a single datapack identified by [name] (the id used by the server, not the display name).
 *
 * See the [Minecraft wiki](https://minecraft.wiki/w/Commands/datapack).
 */
class DataPackCommandEntry(private val fn: Function, val name: String) {
	/** Creates a new datapack with the given [id] and display [name]. */
	fun create(id: String, name: ChatComponents) =
		fn.addLine(command("datapack", literal("create"), literal(id), name))

	/** Disables the datapack so it is no longer loaded on reload. */
	fun disable() = fn.addLine(command("datapack", literal("disable"), literal(name)))

	/** Enables the datapack, optionally forcing it to load [priority] (first or last). */
	fun enable(priority: DatapackPriority? = null) =
		fn.addLine(command("datapack", literal("enable"), literal(name), literal(priority?.asArg())))

	/** Enables the datapack and places it first in the load order. */
	fun enableFirst() = fn.addLine(command("datapack", literal("enable"), literal(name), literal("first")))

	/** Enables the datapack and places it last in the load order. */
	fun enableLast() = fn.addLine(command("datapack", literal("enable"), literal(name), literal("last")))

	/** Enables the datapack immediately before [name] in the load order. */
	fun enableBefore(name: String) =
		fn.addLine(command("datapack", literal("enable"), literal(this.name), literal("before"), literal(name)))

	/** Enables the datapack immediately after [name] in the load order. */
	fun enableAfter(name: String) = fn.addLine(command("datapack", literal("enable"), literal(this.name), literal("after"), literal(name)))
}

/** Entry point for `datapack list` subcommands. */
class DataPacksCommandEntries(private val fn: Function) {
	/** Lists all available but disabled datapacks. */
	fun available() = fn.addLine(command("datapack", literal("list"), literal("available")))

	/** Lists all currently enabled datapacks. */
	fun enabled() = fn.addLine(command("datapack", literal("list"), literal("enabled")))

	/** Lists every datapack known by the server. */
	fun list() = fn.addLine(command("datapack", literal("list")))
}

val Function.dataPacks get() = DataPacksCommandEntries(this)
fun Function.dataPack(name: String, block: DataPackCommandEntry.() -> Command) = DataPackCommandEntry(this, name).block()
