package io.github.ayfri.kore.gradle.tasks

import io.github.ayfri.kore.gradle.internal.PackLinker
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import org.gradle.api.tasks.UntrackedTask

/** Removes the generated output and every pack previously linked into a target folder. */
@UntrackedTask(because = "Deletes files outside the project, there is no output to track.")
abstract class KoreCleanTask : DefaultTask() {
	@get:Internal
	abstract val outputDirectory: DirectoryProperty

	/** Absolute paths of the linked packs to remove, already including the pack folder name. */
	@get:Internal
	abstract val linkedPacks: ListProperty<String>

	@TaskAction
	fun clean() {
		linkedPacks.get().map(::File).filter { PackLinker.delete(it) }.forEach { logger.lifecycle("Unlinked $it") }

		val output = outputDirectory.get().asFile
		if (output.deleteRecursively() && output.exists().not()) logger.lifecycle("Deleted $output")
	}
}
