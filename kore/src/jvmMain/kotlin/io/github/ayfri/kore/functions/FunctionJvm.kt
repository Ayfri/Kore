package io.github.ayfri.kore.functions

import java.io.File

/**
 * Writes the function to disk under the given output directory.
 * Creates parent directories if required and injects debug markers
 * when debug mode is active.
 */
fun Function.generate(directory: File) {
	val file = File(directory, "${this.directory}/$name.mcfunction")
	file.parentFile.mkdirs()

	injectDebugMarkers()

	file.writeText(toString())
}
