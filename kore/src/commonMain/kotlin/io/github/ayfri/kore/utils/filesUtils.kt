package io.github.ayfri.kore.utils

import kotlinx.io.IOException
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemPathSeparator
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.readByteArray
import kotlinx.io.readString
import kotlin.random.Random

/**
 * Lazy on purpose: as an eager file-level property it made *any* access to this file initialize
 * [SystemPathSeparator], which needs Node's `path` module and throws in the browser.
 */
val SystemPathSeparatorString by lazy { SystemPathSeparator.toString() }

val Path.nameWithoutExtension get() = this.name.substringBeforeLast('.')

fun Path.absolute() = SystemFileSystem.resolve(this)
fun Path.delete() = SystemFileSystem.delete(this)
fun Path.create() = this.toSink().buffered().close()
fun Path.createIfNotExists() = if (!this.exists()) this.create() else Unit
fun Path.ensureParents() = this.parent?.makeDirectories()
fun Path.exists() = SystemFileSystem.metadataOrNull(this) != null
fun Path.isDirectory() = SystemFileSystem.metadataOrNull(this)?.isDirectory == true
fun Path.isRegularFile() = SystemFileSystem.metadataOrNull(this)?.isRegularFile == true
fun Path.makeDirectories(force: Boolean = false) = SystemFileSystem.createDirectories(this, force)
fun Path.resolveSafe(vararg paths: Path) = SystemFileSystem.resolve(Path(this.toString(), *paths.map { it.toString() }.toTypedArray()))
fun Path.resolveSafe(vararg paths: String) = SystemFileSystem.resolve(Path(this.toString(), *paths))
fun Path.toSink() = SystemFileSystem.sink(this)
fun Path.toSource() = SystemFileSystem.source(this)
fun Path.toStringWithSeparator(separator: String = SystemPathSeparatorString) =
	this.toString().replace(SystemPathSeparatorString, separator)

fun Path.readBytes() = toSource().buffered().use { it.readByteArray() }
fun Path.readText() = if (!this.isDirectory()) this.toSource().buffered().use { it.readString() }
else throw IOException("Cannot read directory as text")
fun Path.writeText(content: String) = write(content.encodeToByteArray())
fun Path.write(array: ByteArray) = this.toSink().buffered().use { it.write(array) }

/** Writes through a sibling `.part` file then moves it in place, so a crash never leaves a truncated file behind. */
fun Path.writeAtomically(array: ByteArray) {
	val partial = Path("$this.part")
	partial.write(array)
	SystemFileSystem.atomicMove(partial, this)
}

/** Deletes a directory tree. Follows symbolic links, so only use it on directories Kore created itself. */
internal fun Path.deleteRecursively() {
	if (isDirectory()) SystemFileSystem.list(this).forEach { it.deleteRecursively() }
	SystemFileSystem.delete(this, mustExist = false)
}

data object TemporaryFiles {
	fun createTempFile(suffix: String = ".tmp"): Path {
		val tempDir = SystemTemporaryDirectory
		val tempFile = tempDir.resolve(Random.nextLong().toString() + suffix)
		tempFile.ensureParents()
		tempFile.createIfNotExists()
		return tempFile
	}

	/**
	 * Creates a fresh directory named [name] plus a random suffix, failing if it already exists so a directory
	 * planted in a shared temp folder is never reused.
	 */
	fun createTempDirectory(name: String): Path =
		SystemTemporaryDirectory.resolve("${name}_${Random.nextLong().toULong().toString(16)}").apply { makeDirectories(force = true) }
}


fun Path.relativeTo(base: Path): Path {
	val normPath = this.absolute().toString().trimEnd(SystemPathSeparator)
	val normBase = base.absolute().toString().trimEnd(SystemPathSeparator)
	val sep = SystemPathSeparator

	val pathParts = normPath.split(sep).filter { it.isNotEmpty() }
	val baseParts = normBase.split(sep).filter { it.isNotEmpty() }

	val common = pathParts.zip(baseParts).takeWhile { it.first == it.second }.size
	require(common != 0) { "Paths have no common root:\nPath: $normPath\nBase: $normBase" }

	val ups = List(baseParts.size - common) { ".." }
	val relative = (ups + pathParts.drop(common)).joinToString(sep.toString())
	return Path(relative)
}
