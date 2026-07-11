package io.github.ayfri.kore.generation.platform

import kotlinx.io.files.Path
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

/**
 * The only filesystem primitives the datapack generator needs per platform: JVM and Node.js both delegate
 * to the existing `kotlinx.io.files.SystemFileSystem`-based helpers in `utils/filesUtils.kt` (it already
 * supports both synchronously). The browser has no real filesystem and implements these against OPFS
 * (Origin Private File System), which is why the signatures are `suspend`.
 */
internal expect suspend fun platformWriteFile(path: Path, content: ByteArray)
internal expect suspend fun platformCreateDirectories(path: Path)
internal expect suspend fun platformReadFile(path: Path): ByteArray?
internal expect suspend fun platformExists(path: Path): Boolean

/** Recursively lists every regular file under [directory]. Used only by the merge-with-other-packs feature. */
internal expect suspend fun platformWalk(directory: Path): List<Path>

/**
 * Unzips [zipFile] into a fresh temporary directory and returns its path, so an existing `.zip` datapack can be
 * merged into the one being generated. Only supported where reading arbitrary third-party archives (which may use
 * DEFLATE compression, unlike this library's own STORE-only [io.github.ayfri.kore.generation.zip.ZipWriter]) is
 * feasible without a zip library: currently JVM only.
 */
internal expect suspend fun platformUnzipToTempDir(zipFile: Path): Path

/** True only when generation must go through a real async filesystem API (currently: the browser/OPFS). */
internal expect val platformRequiresSuspension: Boolean

/**
 * Runs a `suspend` block that is known not to truly suspend (JVM/Node.js file I/O never does) to completion,
 * synchronously, with zero coroutine library dependency. Throws if the block actually suspends across a real
 * async boundary (e.g. this is called on the browser, where [platformRequiresSuspension] is `true`).
 */
internal fun <T> runSuspendBlocking(block: suspend () -> T): T {
	check(!platformRequiresSuspension) {
		"This operation requires a real asynchronous filesystem on this platform; use the suspend entry point instead."
	}

	var result: Result<T>? = null
	block.startCoroutine(Continuation(EmptyCoroutineContext) { result = it })
	val completed =
		result ?: error("Suspended across a real async boundary despite platformRequiresSuspension being false.")
	return completed.getOrThrow()
}
