package io.github.ayfri.kore.generation.platform

import io.github.ayfri.kore.generation.zip.readZipEntries
import io.github.ayfri.kore.utils.TemporaryFiles
import io.github.ayfri.kore.utils.deleteRecursively
import io.github.ayfri.kore.utils.ensureParents
import io.github.ayfri.kore.utils.makeDirectories
import io.github.ayfri.kore.utils.nameWithoutExtension
import io.github.ayfri.kore.utils.readBytes
import io.github.ayfri.kore.utils.resolve
import io.github.ayfri.kore.utils.write
import kotlinx.io.files.Path

/**
 * Unzips [zipFile] into a fresh temporary directory and returns its path, driven by [readZipEntries]. Shared between the JVM and Node.js
 * [platformUnzipToTempDir] implementations - both have a real filesystem to write extracted entries to; the
 * browser doesn't, so it keeps its own `actual` that throws.
 */
internal fun commonUnzipToTempDir(zipFile: Path): Path {
	val cleanName = zipFile.nameWithoutExtension.replace("[\\\\/:*?\"<>|]".toRegex(), "_")
	val tempDir = TemporaryFiles.createTempDirectory("kore_unzipped_datapack_$cleanName")

	runCatching {
		for (entry in readZipEntries(zipFile.readBytes())) {
			val filePath = tempDir.resolve(safeEntryName(entry.name))
			if (entry.isDirectory) {
				filePath.makeDirectories()
			} else {
				filePath.ensureParents()
				filePath.write(entry.content)
			}
		}
	}.onFailure { tempDir.deleteRecursively() }.getOrThrow()

	return tempDir
}

/** Rejects absolute names and `..` segments, so an entry can never be written outside the extraction directory (Zip Slip). */
private fun safeEntryName(name: String): String {
	val normalized = name.replace('\\', '/')
	val segments = normalized.split('/')
	require(!normalized.startsWith('/') && ':' !in normalized && segments.none { it == ".." }) {
		"Refusing to extract zip entry '$name': it points outside the extraction directory."
	}
	return segments.filter { it.isNotEmpty() && it != "." }.joinToString("/")
}
