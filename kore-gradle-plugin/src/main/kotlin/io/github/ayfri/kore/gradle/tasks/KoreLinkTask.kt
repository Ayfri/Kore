package io.github.ayfri.kore.gradle.tasks

import io.github.ayfri.kore.gradle.LinkMode
import io.github.ayfri.kore.gradle.internal.PackLinker
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

/** Places a generated pack into every configured target folder. */
@DisableCachingByDefault(because = "Linking writes into absolute paths outside the project, which the build cache cannot relocate.")
abstract class KoreLinkTask : DefaultTask() {
	@get:InputDirectory
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val source: DirectoryProperty

	/**
	 * The linked copies, one per target, each already including the pack folder name.
	 *
	 * Declared as outputs rather than plain inputs so a second run is up to date, and so deleting a linked pack from
	 * the game folder is enough to have the next build put it back.
	 */
	@get:OutputDirectories
	abstract val destinations: ConfigurableFileCollection

	@get:Input
	abstract val linkMode: Property<LinkMode>

	@TaskAction
	fun link() {
		val source = source.get().asFile
		val destinations = destinations.files

		if (destinations.isEmpty()) {
			logger.lifecycle("No link target configured, set `kore.worlds`, `kore.linkToAllWorlds` or `kore.dataPackTargets`.")
			return
		}

		destinations.forEach { destination ->
			val result = PackLinker.link(source, destination, linkMode.get())
			val suffix = result.fallbackReason?.let { " (copied, symlink refused: $it)" } ?: ""
			logger.lifecycle("Linked ${source.name} -> ${result.destination}$suffix")
		}
	}
}
