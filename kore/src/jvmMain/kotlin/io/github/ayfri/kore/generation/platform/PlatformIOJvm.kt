package io.github.ayfri.kore.generation.platform

import io.github.ayfri.kore.utils.*
import kotlinx.io.files.Path

internal actual val platformRequiresSuspension: Boolean = false

internal actual suspend fun platformWriteFile(path: Path, content: ByteArray) {
	path.ensureParents()
	path.write(content)
}

internal actual suspend fun platformCreateDirectories(path: Path) {
	path.makeDirectories()
}

internal actual suspend fun platformReadFile(path: Path): ByteArray? {
	if (!path.exists() || path.isDirectory()) return null
	return path.readBytes()
}

internal actual suspend fun platformExists(path: Path): Boolean = path.exists()

internal actual suspend fun platformWalk(directory: Path): List<Path> =
	directory.toJavaFile().walkTopDown().filter { it.isFile }.map { Path(it.absolutePath) }.toList()

internal actual suspend fun platformUnzipToTempDir(zipFile: Path): Path = commonUnzipToTempDir(zipFile)
