package io.github.ayfri.kore

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.nio.file.Path

@Serializable
abstract class Generator(@Transient val resourceFolder: String = error("Generator must have a resource folder")) {
	@Transient
	var namespace: String? = null

	abstract var fileName: String

	abstract fun generateJson(dataPack: DataPack): String

	fun getFinalPath(dataPack: DataPack): Path {
		val dataFolder = dataPack.cleanPath.resolve(dataPack.name).resolve("data")
		val namespace = namespace ?: dataPack.name
		return getPathFromDataDir(dataFolder, namespace)
	}

	open fun getPathFromDataDir(dir: Path, namespace: String): Path = dir.resolve(namespace).resolve(resourceFolder).resolve("$fileName.json")
}
