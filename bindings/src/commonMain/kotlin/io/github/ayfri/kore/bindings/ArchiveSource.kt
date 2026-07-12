package io.github.ayfri.kore.bindings

import dev.karmakrafts.kompress.ExperimentalCompressionApi
import dev.karmakrafts.kompress.archive.extract
import dev.karmakrafts.kompress.zip.unzip
import kotlinx.io.Buffer
import kotlinx.io.readByteArray

/**
 * A datapack fully materialized in memory, keyed by normalized relative path
 * (forward slashes, no leading slash) to its UTF-8 text content.
 */
class InMemoryDatapack(val files: Map<String, String>)

/**
 * Reads a zip-format datapack from raw bytes into memory using a pure-Kotlin,
 * multiplatform (incl. JS) DEFLATE/ZIP implementation - no filesystem access involved.
 */
@OptIn(ExperimentalCompressionApi::class)
fun readZipDatapack(bytes: ByteArray): InMemoryDatapack {
	val buffer = Buffer().apply { write(bytes) }
	val entries = buffer.unzip().use { it.extract() }

	val files = entries
		.filterNot { (entry, _) -> entry.name.endsWith("/") }
		.associate { (entry, content) ->
			entry.name.replace('\\', '/').trimStart('/') to content.readByteArray().decodeToString()
		}

	return InMemoryDatapack(files)
}
