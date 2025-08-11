package io.github.ayfri.kore

import io.github.ayfri.kore.utils.resolveSafe
import io.github.ayfri.kore.utils.resolve
import kotlinx.io.files.Path
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
* Base class for all generators.
*
* A generator is a class that can generate a JSON file.
*
* @param resourceFolder the folder where the generated file is stored
*/
@Serializable
abstract class Generator(@Transient val resourceFolder: String = error("Generator must have a resource folder")) {
	@Transient
	var namespace: String? = null

	abstract var fileName: String

	/** Generates the JSON content for the generator. */
	abstract fun generateJson(dataPack: DataPack): String

	/** Returns the final path of the generated file, relative to the datapack output directory. */
	fun getFinalPath(dataPack: DataPack): Path {
		val dataFolder = dataPack.cleanPath.resolve(dataPack.name, "data")
		val namespace = namespace ?: dataPack.name
		return getPathFromDataDir(dataFolder, namespace)
	}

	/** Returns the path of the generated file, relative to the datapack output directory, is open because some generators have a different path. */
	open fun getPathFromDataDir(dir: Path, namespace: String): Path = dir.resolve(namespace, resourceFolder, "$fileName.json")
}
