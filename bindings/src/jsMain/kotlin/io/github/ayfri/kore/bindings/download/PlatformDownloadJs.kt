package io.github.ayfri.kore.bindings.download

import io.github.ayfri.kore.utils.ensureParents
import io.github.ayfri.kore.utils.exists
import io.github.ayfri.kore.utils.isDirectory
import io.github.ayfri.kore.utils.makeDirectories
import io.github.ayfri.kore.utils.toSource
import io.github.ayfri.kore.utils.write
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.readByteArray

/**
 * Kotlin/JS compiles once for both `browser()` and `nodejs()`, so the two backends are picked at runtime rather
 * than via a separate `expect`/`actual` split - mirrors `kore`'s own `PlatformIOJs.kt`. Node.js reuses the exact
 * same `kotlinx.io.files.SystemFileSystem`-backed helpers as the JVM for its cache; the browser has no real
 * filesystem and goes through OPFS.
 */
private val isNode: Boolean =
	js("(typeof process !== 'undefined' && process.versions != null && process.versions.node != null)") as Boolean

internal actual val downloadRequiresSuspension: Boolean = !isNode

internal actual fun platformEnvVar(name: String): String? {
	if (!isNode) return null
	val processEnv: dynamic = js("process.env")
	return processEnv[name] as? String
}

internal actual fun platformSystemProperty(name: String): String? = null

internal actual suspend fun httpRequest(
	url: String,
	method: String,
	headers: Map<String, String>,
	body: ByteArray?,
): HttpResponse {
	val response = jsFetch(url, method, headers, body).await()
	val bytes = if (method == "HEAD") ByteArray(0) else response.arrayBuffer().await().toByteArray()
	return HttpResponse(response.status, bytes)
}

private val nodeCacheDir: Path by lazy {
	val base = platformEnvVar("KORE_CACHE_HOME")
		?: platformEnvVar("XDG_CACHE_HOME")
		?: "${platformEnvVar("HOME") ?: platformEnvVar("USERPROFILE") ?: "."}/.cache/kore"
	Path(base, "datapacks").also { it.makeDirectories() }
}

private val browserCacheDir = Path("kore-cache", "datapacks")

internal actual suspend fun cacheDir(): Path = if (isNode) nodeCacheDir else browserCacheDir

internal actual suspend fun cacheReadFile(path: Path): ByteArray? {
	if (isNode) {
		if (!path.exists() || path.isDirectory()) return null
		return path.toSource().buffered().readByteArray()
	}
	return Opfs.readFile(path)
}

internal actual suspend fun cacheWriteFile(path: Path, content: ByteArray) {
	if (isNode) {
		path.ensureParents()
		path.write(content)
	} else {
		Opfs.writeFile(path, content)
	}
}

internal actual suspend fun cacheFileExists(path: Path): Boolean = if (isNode) path.exists() else Opfs.exists(path)

/** Origin Private File System (OPFS) access for the browser cache. */
private object Opfs {
	private fun segments(path: Path) = path.toString().replace('\\', '/').split('/').filter { it.isNotEmpty() }

	private suspend fun root(): FileSystemDirectoryHandle = opfsNavigator().storage.getDirectory().await()

	private suspend fun directory(segments: List<String>, create: Boolean): FileSystemDirectoryHandle {
		var handle = root()
		for (segment in segments) handle = handle.getDirectoryHandle(segment, fileSystemHandleOptions(create)).await()
		return handle
	}

	suspend fun writeFile(path: Path, content: ByteArray) {
		val parts = segments(path)
		val parent = directory(parts.dropLast(1), create = true)
		val fileHandle = parent.getFileHandle(parts.last(), fileSystemHandleOptions(true)).await()
		val writable = fileHandle.createWritable().await()
		writable.write(content.toInt8Array()).await()
		writable.close().await()
	}

	suspend fun readFile(path: Path): ByteArray? {
		val parts = segments(path)
		return try {
			val parent = directory(parts.dropLast(1), create = false)
			val fileHandle = parent.getFileHandle(parts.last(), fileSystemHandleOptions(false)).await()
			val file = fileHandle.getFile().await()
			file.arrayBuffer().await().toByteArray()
		} catch (_: Throwable) {
			null
		}
	}

	suspend fun exists(path: Path): Boolean {
		val parts = segments(path)
		if (parts.isEmpty()) return true

		return try {
			val parent = directory(parts.dropLast(1), create = false)
			try {
				parent.getFileHandle(parts.last(), fileSystemHandleOptions(false)).await()
				true
			} catch (_: Throwable) {
				parent.getDirectoryHandle(parts.last(), fileSystemHandleOptions(false)).await()
				true
			}
		} catch (_: Throwable) {
			false
		}
	}
}
