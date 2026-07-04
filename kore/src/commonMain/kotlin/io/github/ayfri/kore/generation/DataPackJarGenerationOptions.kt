package io.github.ayfri.kore.generation

import io.github.ayfri.kore.DataPack
import kotlinx.io.files.Path

abstract class DataPackJarGenerationProvider {
	abstract fun generateAdditionalFiles(generator: DataPackGenerator, options: DataPackJarGenerationOptions)
}

data class DataPackJarGenerationOptions(
	internal val datapack: DataPack,
	override var mergeWithPacks: List<Path> = emptyList(),
	var providers: List<DataPackJarGenerationProvider> = emptyList(),
) : DataPackGenerationCommonOptions()
