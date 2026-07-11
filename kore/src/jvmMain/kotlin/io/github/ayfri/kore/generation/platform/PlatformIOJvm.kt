package io.github.ayfri.kore.generation.platform

import io.github.ayfri.kore.utils.*
import kotlinx.io.asInputStream
import kotlinx.io.asOutputStream
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.readByteArray
import java.util.zip.ZipInputStream

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
	return path.toSource().buffered().readByteArray()
}

internal actual suspend fun platformExists(path: Path): Boolean = path.exists()

internal actual suspend fun platformWalk(directory: Path): List<Path> =
	directory.toJavaFile().walkTopDown().filter { it.isFile }.map { Path(it.absolutePath) }.toList()

internal actual suspend fun platformUnzipToTempDir(zipFile: Path): Path {
	val cleanName = zipFile.nameWithoutExtension.replace("[\\\\/:*?\"<>|]".toRegex(), "_")
	val tempDir = TemporaryFiles.createTempDirectory("kore_unzipped_datapack_$cleanName")

	ZipInputStream(zipFile.toSource().buffered().asInputStream()).use { zipInputStream ->
		var entry = zipInputStream.nextEntry
		while (entry != null) {
			val filePath = tempDir.resolveSafe(entry.name)
			if (entry.isDirectory) {
				filePath.makeDirectories()
			} else {
				filePath.ensureParents()
				filePath.toSink().buffered().asOutputStream().use(zipInputStream::copyTo)
			}
			zipInputStream.closeEntry()
			entry = zipInputStream.nextEntry
		}
	}

	return tempDir
}
