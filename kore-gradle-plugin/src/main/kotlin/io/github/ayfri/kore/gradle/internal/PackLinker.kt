package io.github.ayfri.kore.gradle.internal

import io.github.ayfri.kore.gradle.LinkMode
import java.io.File
import java.io.IOException
import java.nio.file.Files

/** Result of linking one pack into one target, reported back so tasks can log what actually happened. */
data class LinkResult(
	val destination: File,
	val mode: LinkMode,
	/** Set when [LinkMode.SYMLINK] was requested but the filesystem refused it and a copy was made instead. */
	val fallbackReason: String? = null,
)

/** Places a generated pack inside a target folder, either as a copy or as a symlink. */
object PackLinker {
	fun link(source: File, destination: File, mode: LinkMode): LinkResult {
		check(source.isDirectory) { "No generated pack at $source, run the build task first." }

		delete(destination)
		destination.parentFile?.mkdirs()

		if (mode == LinkMode.COPY) {
			source.copyRecursively(destination, overwrite = true)
			return LinkResult(destination, LinkMode.COPY)
		}

		return try {
			Files.createSymbolicLink(destination.toPath(), source.toPath())
			LinkResult(destination, LinkMode.SYMLINK)
		} catch (exception: IOException) {
			source.copyRecursively(destination, overwrite = true)
			LinkResult(destination, LinkMode.COPY, exception.message ?: exception::class.simpleName)
		} catch (exception: UnsupportedOperationException) {
			source.copyRecursively(destination, overwrite = true)
			LinkResult(destination, LinkMode.COPY, exception.message ?: exception::class.simpleName)
		}
	}

	/**
	 * Removes a previously linked pack. A symlink is unlinked rather than walked, otherwise deleting a stale link
	 * would wipe the generated pack it points at.
	 */
	fun delete(destination: File): Boolean {
		val path = destination.toPath()
		if (Files.isSymbolicLink(path)) {
			Files.deleteIfExists(path)
			return true
		}

		if (!destination.exists()) return false
		return destination.deleteRecursively()
	}
}
