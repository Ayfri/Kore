package io.github.ayfri.kore.arguments

/**
 * Minimal JS implementation: only [Argument.asString] is honored. Rich states/components/nbt-backed
 * deserialization (used by the JVM-only `bindings/` datapack importer) is not available on JS, since the DSL
 * only ever builds [Argument]s there, never parses them back from an existing datapack.
 */
actual fun createArgumentProxy(value: String): Argument = object : Argument {
	override fun asString() = value
}

actual val canDeserializeTypedArguments: Boolean = false
