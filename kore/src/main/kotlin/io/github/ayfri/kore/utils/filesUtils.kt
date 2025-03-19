package io.github.ayfri.kore.utils

import kotlinx.io.IOException
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemPathSeparator
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.readString
import java.io.File

val SystemPathSeparatorString = SystemPathSeparator.toString()

val Path.asInvariantPathSeparator get() = this.toStringWithSeperator("/")
val Path.nameWithoutExtension get() = this.name.substringBeforeLast('.')

fun Path(file: File) = Path(file.absolutePath)

fun Path.absolute() = SystemFileSystem.resolve(this)
fun Path.delete() = SystemFileSystem.delete(this)
fun Path.ensureParents() = this.parent?.makeDirectories()
fun Path.exists() = SystemFileSystem.metadataOrNull(this) != null
fun Path.isDirectory() = SystemFileSystem.metadataOrNull(this)?.isDirectory == true
fun Path.makeDirectories(force: Boolean = false) = SystemFileSystem.createDirectories(this, force)
fun Path.resolveSafe(vararg paths: Path) = SystemFileSystem.resolve(Path(this.toString(), *paths.map { it.toString() }.toTypedArray()))
fun Path.resolveSafe(vararg paths: String) = SystemFileSystem.resolve(Path(this.toString(), *paths))
fun Path.resolve(vararg paths: Path) = Path(paths.fold(this.toJavaFile()) { acc, path -> acc.resolve(path.toJavaFile()) })
fun Path.resolve(vararg paths: String) = Path(paths.fold(this.toJavaFile()) { acc, path -> acc.resolve(path) })
fun Path.toJavaFile() = File(this.toString())
fun Path.toSink() = SystemFileSystem.sink(this)
fun Path.toSource() = SystemFileSystem.source(this)
fun Path.toStringWithSeperator(seperator: String = SystemPathSeparatorString) =
	this.toString().replace(SystemPathSeparatorString, seperator)

fun Path.readText() = if (!this.isDirectory()) this.toSource().buffered().run {
	readString()
} else throw IOException("Cannot read directory as text")
fun Path.writeText(content: String) = if (!this.isDirectory()) this.toSink().buffered().apply {
	write(content.toByteArray())
	flush()
} else throw IOException("Cannot write to directory")
fun Path.write(array: ByteArray) = if (!this.isDirectory()) this.toSink().buffered().apply {
	write(array)
	flush()
} else throw IOException("Cannot write to directory")

data object TemporaryFiles {
	fun createTempFile(suffix: String = ".tmp"): Path {
		val tempDir = SystemTemporaryDirectory
		return tempDir.resolveSafe(System.nanoTime().toString() + suffix)
	}

	fun createTempDirectory(name: String): Path {
		val tempDir = SystemTemporaryDirectory
		return tempDir.resolveSafe(name)
	}
}


fun Path.relativeTo(base: Path): Path {
	val normPath = this.absolute().toString().trimEnd(SystemPathSeparator)
	val normBase = base.absolute().toString().trimEnd(SystemPathSeparator)
	val sep = SystemPathSeparator

	val pathParts = normPath.split(sep).filter { it.isNotEmpty() }
	val baseParts = normBase.split(sep).filter { it.isNotEmpty() }

	val common = pathParts.zip(baseParts).takeWhile { it.first == it.second }.count()
	if (common == 0)
		throw IllegalArgumentException("Paths have no common root:\nPath: $normPath\nBase: $normBase")

	val ups = List(baseParts.size - common) { ".." }
	val relative = (ups + pathParts.drop(common)).joinToString(sep.toString())
	return Path(relative)
}
