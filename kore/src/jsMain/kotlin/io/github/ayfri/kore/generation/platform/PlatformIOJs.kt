package io.github.ayfri.kore.generation.platform

import io.github.ayfri.kore.utils.*
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray

/**
 * Kotlin/JS compiles once for both `browser()` and `nodejs()`, so the two backends are picked at runtime rather
 * than via a separate `expect`/`actual` split. Node.js reuses the exact same `kotlinx.io.files.SystemFileSystem`-
 * backed helpers as the JVM (it already works there); the browser has no real filesystem and goes through OPFS.
 */
private val isNode: Boolean =
	js("(typeof process !== 'undefined' && process.versions != null && process.versions.node != null)") as Boolean

internal actual val platformRequiresSuspension: Boolean = !isNode

internal actual suspend fun platformWriteFile(path: Path, content: ByteArray) {
	if (isNode) {
		path.ensureParents()
		path.write(content)
	} else {
		Opfs.writeFile(path, content)
	}
}

internal actual suspend fun platformCreateDirectories(path: Path) {
	if (isNode) path.makeDirectories() else Opfs.createDirectories(path)
}

internal actual suspend fun platformReadFile(path: Path): ByteArray? {
	if (isNode) {
		if (!path.exists() || path.isDirectory()) return null
		return path.toSource().buffered().readByteArray()
	}
	return Opfs.readFile(path)
}

internal actual suspend fun platformExists(path: Path): Boolean = if (isNode) path.exists() else Opfs.exists(path)

internal actual suspend fun platformWalk(directory: Path): List<Path> {
	if (isNode) {
		val result = mutableListOf<Path>()
		fun recurse(dir: Path) {
			SystemFileSystem.list(dir).forEach { child ->
				if (SystemFileSystem.metadataOrNull(child)?.isDirectory == true) recurse(child) else result += child
			}
		}
		recurse(directory)
		return result
	}

	throw UnsupportedOperationException("Merging with other datapacks isn't supported in the browser yet.")
}

internal actual suspend fun platformUnzipToTempDir(zipFile: Path): Path {
	if (isNode) return commonUnzipToTempDir(zipFile)
	throw UnsupportedOperationException("Merging with an existing .zip datapack isn't supported in the browser yet.")
}
