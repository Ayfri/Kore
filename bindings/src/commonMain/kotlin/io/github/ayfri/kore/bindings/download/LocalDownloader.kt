package io.github.ayfri.kore.bindings.download

import io.github.ayfri.kore.utils.exists
import kotlinx.io.files.Path

internal data object LocalDownloader : Downloader {
	override fun match(source: String) = true // Fallback

	override suspend fun download(reference: String, skipCache: Boolean): Pair<Path, String> {
		val path = locateLocalDatapack(reference)
		return path to path.name
	}

	private fun locateLocalDatapack(source: String): Path {
		// Try direct path first
		val path = Path(source)
		if (path.exists()) return path

		// Try with .zip extension
		val withZip = Path("$source.zip")
		if (withZip.exists()) return withZip

		// Try looking in common datapack locations
		val commonLocations = listOf(
			Path("./$source"),
			Path("./$source.zip"),
		)

		for (location in commonLocations) {
			if (location.exists()) {
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
