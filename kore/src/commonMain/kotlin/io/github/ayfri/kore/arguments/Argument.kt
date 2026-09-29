package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable

@Serializable(Argument.ArgumentSerializer::class)
interface Argument {
	fun asString(): String

	data object ArgumentSerializer : ToStringSerializer<Argument>(
		transform = { asString() },
		fromString = ::createArgumentProxy
	)

	companion object {
		internal fun parse(value: String) = parseArgument(value)
	}
}

/**
 * An enum passed to commands as is, written as its lowercase name: `command("gamemode", Gamemode.CREATIVE)` gives
 * `gamemode creative`. An entry the game names differently overrides [asString], and its serializer writes [asString].
 */
interface EnumArgument : Argument {
	val name: String

	override fun asString() = name.lowercase()
}

/**
 * Builds an [Argument] instance from its raw string form (e.g. `"minecraft:stone[...]{...}"`), dynamically
 * implementing whatever [Argument] sub-interfaces the deserialization target needs.
 *
 * Only meaningful on the JVM, where the datapack importer (`bindings/`) parses arguments back from existing
 * datapacks; on JS, [Argument]s are only ever built by the DSL, never parsed, so the actual is a minimal stub.
 */
expect fun createArgumentProxy(value: String): Argument

/** Whether [createArgumentProxy] rebuilds a fully-typed [Argument] (`true` on JVM) or just the `asString` stub. */
expect val canDeserializeTypedArguments: Boolean
