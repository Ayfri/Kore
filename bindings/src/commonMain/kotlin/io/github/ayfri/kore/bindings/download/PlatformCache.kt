package io.github.ayfri.kore.bindings.download

import kotlinx.io.files.Path

/**
 * Directory downloaded datapacks are cached under. An OS cache dir (`kotlinx.io.files.SystemFileSystem`-backed)
 * on the JVM and Node.js, an OPFS directory in the browser.
 */
internal expect suspend fun cacheDir(): Path

internal expect suspend fun cacheReadFile(path: Path): ByteArray?
internal expect suspend fun cacheWriteFile(path: Path, content: ByteArray)
internal expect suspend fun cacheFileExists(path: Path): Boolean
