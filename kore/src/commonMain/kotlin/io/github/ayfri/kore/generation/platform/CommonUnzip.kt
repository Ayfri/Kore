package io.github.ayfri.kore.generation.platform

import io.github.ayfri.kore.generation.zip.readZipEntries
import io.github.ayfri.kore.utils.TemporaryFiles
import io.github.ayfri.kore.utils.ensureParents
import io.github.ayfri.kore.utils.makeDirectories
import io.github.ayfri.kore.utils.nameWithoutExtension
import io.github.ayfri.kore.utils.resolveSafe
import io.github.ayfri.kore.utils.toSource
import io.github.ayfri.kore.utils.write
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.readByteArray

/**
 * Unzips [zipFile] into a fresh temporary directory and returns its path, driven entirely by the pure-Kotlin
 * [readZipEntries]/[io.github.ayfri.kore.generation.zip.Inflate]. Shared between the JVM and Node.js
 * [platformUnzipToTempDir] implementations - both have a real filesystem to write extracted entries to; the
 * browser doesn't, so it keeps its own `actual` that throws.
 */
internal fun commonUnzipToTempDir(zipFile: Path): Path {
	val cleanName = zipFile.nameWithoutExtension.replace("[\\\\/:*?\"<>|]".toRegex(), "_")
	val tempDir = TemporaryFiles.createTempDirectory("kore_unzipped_datapack_$cleanName")

	val bytes = zipFile.toSource().buffered().readByteArray()
	for (entry in readZipEntries(bytes)) {
		val filePath = tempDir.resolveSafe(entry.name)
		if (entry.isDirectory) {
			filePath.makeDirectories()
		} else {
			filePath.ensureParents()
			filePath.write(entry.content)
		}
	}

	return tempDir
}
