package io.github.ayfri.kore

import kotlin.io.path.createParentDirectories
import kotlin.io.path.invariantSeparatorsPathString
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class Generator(@Transient val resourceFolder: String = error("Generator must have a resource folder")) {
	@Transient
	var namespace: String? = null

	abstract var fileName: String

	abstract fun generateJson(dataPack: DataPack): String

	open fun getFinalPath(dataPack: DataPack): Path {
		val dataFolder = dataPack.path.resolve(dataPack.name).resolve("data")
		val namespace = namespace ?: dataPack.name
		return dataFolder.resolve(namespace).resolve(resourceFolder).resolve("$fileName.json")
	}

	fun generateFile(dataPack: DataPack) {
		val path = getFinalPath(dataPack)
		path.createParentDirectories()
		path.toFile().writeText(generateJson(dataPack))
	}

	fun generateZipEntry(dataPack: DataPack, zipOutputStream: ZipOutputStream) {
		val path = getFinalPath(dataPack)
		val zippedPath = path.invariantSeparatorsPathString.removePrefix("${dataPack.path.invariantSeparatorsPathString}/${dataPack.name}/")
		zipOutputStream.putNextEntry(ZipEntry(zippedPath))
		zipOutputStream.write(generateJson(dataPack).toByteArray())
		zipOutputStream.closeEntry()
	}
}
