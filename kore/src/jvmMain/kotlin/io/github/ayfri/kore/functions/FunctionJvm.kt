package io.github.ayfri.kore.functions

import java.io.File

/**
 * Writes the function to disk under the given output directory.
 * Creates parent directories if required and wraps the file in debug markers while debug mode is on.
 */
fun Function.generate(directory: File) {
	val file = File(directory, "${this.directory}/$name.mcfunction")
	file.parentFile.mkdirs()
	file.writeText(fileContent())
}
