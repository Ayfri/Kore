package io.github.ayfri.kore.utils

import kotlinx.io.IOException
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemPathSeparator
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.readString
import kotlin.random.Random

val SystemPathSeparatorString = SystemPathSeparator.toString()

val Path.asInvariantPathSeparator get() = this.toStringWithSeparator("/")
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

fun Path.readText() = if (!this.isDirectory()) this.toSource().buffered().run {
	readString()
} else throw IOException("Cannot read directory as text")
fun Path.writeText(content: String) = if (!this.isDirectory()) this.toSink().buffered().apply {
	write(content.encodeToByteArray())
	flush()
} else throw IOException("Cannot write to directory")
fun Path.write(array: ByteArray) = if (!this.isDirectory()) this.toSink().buffered().apply {
	write(array)
	flush()
} else throw IOException("Cannot write to directory")

data object TemporaryFiles {
	fun createTempFile(suffix: String = ".tmp"): Path {
		val tempDir = SystemTemporaryDirectory
		val tempFile = tempDir.resolve(Random.nextLong().toString() + suffix)
		tempFile.ensureParents()
		tempFile.createIfNotExists()
		return tempFile
	}

	fun createTempDirectory(name: String): Path {
		val tempDir = SystemTemporaryDirectory
		return tempDir.resolve(name).apply { makeDirectories() }
	}
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
