package io.github.ayfri.kore.bindings.download

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

data object LocalDownloader : Downloader {
	override fun match(source: String) = true // Fallback

	override fun download(reference: String, skipCache: Boolean): Pair<Path, String> {
		val path = locateLocalDatapack(reference)
		return path to path.fileName.toString()
	}

	private fun locateLocalDatapack(source: String): Path {
		// Try direct path first
		val path = Path(source)
		if (Files.exists(path)) return path

		// Try with .zip extension
		val withZip = Path("$source.zip")
		if (Files.exists(withZip)) return withZip

		// Try looking in common datapack locations
		val commonLocations = listOf(
			Path("./$source"),
			Path("./$source.zip"),
		)

		for (location in commonLocations) {
			if (Files.exists(location)) {
				println("Found datapack at: $location")
				return location
			}
		}

		throw IllegalArgumentException(
			"Could not locate datapack '$source'. Tried:\n" +
				"  - $path\n" +
				"  - $withZip\n" +
				commonLocations.joinToString("\n") { "  - $it" }
		)
	}
}
