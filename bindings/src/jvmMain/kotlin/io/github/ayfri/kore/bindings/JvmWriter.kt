package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.bindings.api.RemappingState
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString

/**
 * Main entry point for generating Kotlin bindings from a datapack.
 * Renders the source via [renderDatapackFile] and writes it under [outputPath].
 */
fun writeFiles(datapack: Datapack, outputPath: Path, remappings: RemappingState = RemappingState()) {
	generateDatapackFile(datapack, outputPath, remappings = remappings)
}

/**
 * Generates the main datapack Kotlin file with all resources and writes it to disk.
 */
fun generateDatapackFile(
	datapack: Datapack,
	outputDir: Path,
	packageNameOverride: String? = null,
	remappings: RemappingState = RemappingState(),
) {
	val startTime = kotlin.time.TimeSource.Monotonic.markNow()
	val (datapackObjectName, source) = renderDatapackFile(datapack, packageNameOverride, remappings)

	SystemFileSystem.createDirectories(outputDir, mustCreate = false)
	val targetFile = Path(outputDir, "$datapackObjectName.kt")
	SystemFileSystem.sink(targetFile).buffered().use { it.writeString(source) }

	val elapsedTime = startTime.elapsedNow()
	println("Generated bindings '$datapackObjectName' in $elapsedTime in: $targetFile")
}
