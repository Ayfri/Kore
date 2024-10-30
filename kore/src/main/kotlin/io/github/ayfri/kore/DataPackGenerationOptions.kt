package io.github.ayfri.kore

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolute

data class DataPackGenerationOptions(
	var mergeWithPacks: List<Path> = emptyList(),
)

/**
 * Merge the current [DataPack] with the given [packs].
 * Will unzip any zip files in the [packs] and merge them.
 */
fun DataPackGenerationOptions.mergeWithPacks(vararg packs: Path) {
	mergeWithPacks += packs.map { it.normalize().absolute() }
}

/**
 * Merge the current [DataPack] with the given [packs].
 * Also, checks if the versions match and prints out warning if they don't.
 *
 * **Note:** This function will generate the given [packs] in temporary folders before merging them.
 */
fun DataPackGenerationOptions.mergeWithPacks(vararg packs: DataPack) {
	mergeWithPacks(*packs.map {
		val previousPath = it.path
		val tempPath = Files.createTempDirectory("datapack_${it.name}")
		it.path = tempPath
		it.generate()
		it.path = previousPath
		tempPath.resolve(it.name).normalize().absolute()
	}.toTypedArray())
}
