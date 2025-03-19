package io.github.ayfri.kore.generation

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.utils.absolute
import io.github.ayfri.kore.utils.makeDirectories
import io.github.ayfri.kore.utils.resolveSafe
import io.github.ayfri.kore.utils.resolve
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.files.Path

abstract class DataPackGenerationCommonOptions {
	open var mergeWithPacks: List<Path> = emptyList()
}

data class DataPackGenerationOptions(
	override var mergeWithPacks: List<Path> = emptyList(),
) : DataPackGenerationCommonOptions()

/**
 * Merge the current [DataPack] with the given [packs].
 * Will unzip any zip files in the [packs] and merge them.
 */
fun DataPackGenerationOptions.mergeWithPacks(vararg packs: Path) {
	mergeWithPacks += packs.map { it.absolute() }
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
		val tempPath = SystemTemporaryDirectory.resolve("datapack_${it.name}").apply { makeDirectories() }
		it.path = tempPath
		it.generate()
		it.path = previousPath
		tempPath.resolveSafe(it.name).absolute()
	}.toTypedArray())
}
