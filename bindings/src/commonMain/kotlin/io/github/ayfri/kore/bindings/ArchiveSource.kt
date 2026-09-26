package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.generation.zip.readZipEntries

/**
 * A datapack fully materialized in memory, keyed by normalized relative path
 * (forward slashes, no leading slash) to its UTF-8 text content.
 */
class InMemoryDatapack(val files: Map<String, String>)

/**
 * Reads a zip-format datapack from raw bytes into memory through Kore's multiplatform [readZipEntries] (native
 * `java.util.zip` inflate on the JVM, pure Kotlin on JS), no filesystem access involved.
 */
fun readZipDatapack(bytes: ByteArray) = InMemoryDatapack(
	readZipEntries(bytes)
		.filterNot { it.isDirectory }
		.associate { it.name.replace('\\', '/').trimStart('/') to it.content.decodeToString() }
)
