package io.github.ayfri.kore.gradle.tasks

import io.github.ayfri.kore.gradle.internal.MinecraftLocator
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

/** Lists the worlds found in the configured Minecraft directory, so `kore.worlds` can be filled without guessing. */
@UntrackedTask(because = "Only reports what is currently on disk.")
abstract class KoreWorldsTask : DefaultTask() {
	@get:Internal
	abstract val minecraftDirectory: DirectoryProperty

	@TaskAction
	fun list() {
		val directory = minecraftDirectory.get().asFile
		logger.lifecycle("Minecraft directory: $directory")

		if (!directory.isDirectory) {
			logger.lifecycle("This directory does not exist, set `kore.minecraftDirectory` or the ${MinecraftLocator.DIRECTORY_ENVIRONMENT_VARIABLE} environment variable.")
			return
		}

		val worlds = MinecraftLocator.worlds(directory)
		if (worlds.isEmpty()) {
			logger.lifecycle("No world found under ${directory.resolve("saves")}.")
			return
		}

		logger.lifecycle("Worlds (${worlds.size}):")
		worlds.forEach { logger.lifecycle("  $it -> ${MinecraftLocator.dataPacksDirectory(directory, it)}") }
	}
}
