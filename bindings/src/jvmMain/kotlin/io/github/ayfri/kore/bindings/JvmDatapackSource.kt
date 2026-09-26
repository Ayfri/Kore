package io.github.ayfri.kore.bindings

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import java.io.File

private fun Path.isZipFormat() = SystemFileSystem.metadataOrNull(this)?.isDirectory != true && File(toString()).extension == "zip"

private fun directoryToInMemoryDatapack(dirPath: Path): InMemoryDatapack {
	val root = File(dirPath.toString())
	val files = mutableMapOf<String, String>()

	root.walkTopDown().forEach { file ->
		if (file.isFile) {
			val relative = file.relativeTo(root).invariantSeparatorsPath
			files[relative] = file.readText()
		}
	}

	return InMemoryDatapack(files)
}

/**
 * Resolves a local path (zip file or directory, with fallbacks for a `.zip` reference
 * whose actual data lives in a sibling directory) and explores it.
 */
fun explore(inputPath: String): Datapack {
	val input = Path(inputPath)
	var path = input
	var isZip = input.isZipFormat()
	val displayName = File(input.toString()).name

	// Fallbacks when a .zip path is provided but the file does not exist
	if (isZip && !SystemFileSystem.exists(path)) {
		val baseName = displayName.removeSuffix(".zip")
		val parent = input.parent
		// 1) Try <dir>/<base>/<base>.zip
		val nestedZip = parent?.let { Path(Path(it, baseName), "$baseName.zip") }
		if (nestedZip != null && SystemFileSystem.exists(nestedZip)) {
			path = nestedZip
			isZip = true
		} else {
			// 2) Try sibling directory with same base name (possibly nested)
			val candidateDir = parent?.let { Path(it, baseName) }
			if (candidateDir != null && SystemFileSystem.metadataOrNull(candidateDir)?.isDirectory == true) {
				var chosen = candidateDir
				val nested = Path(candidateDir, baseName)
				if (SystemFileSystem.metadataOrNull(nested)?.isDirectory == true) chosen = nested
				path = chosen
				isZip = false
			}
		}
	}

	val inMemory = if (isZip) {
		readZipDatapack(File(path.toString()).readBytes())
	} else {
		directoryToInMemoryDatapack(path)
	}

	return explore(inMemory, displayName, path)
}
