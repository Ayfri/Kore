package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.dimension.biomesource.multiNoise
import io.github.ayfri.kore.features.worldgen.dimension.dimension
import io.github.ayfri.kore.features.worldgen.dimension.generator.noiseGenerator
import io.github.ayfri.kore.features.worldgen.multinoisebiomesourceparameterlist.multiNoiseBiomeSourceParameterList
import io.github.ayfri.kore.generated.BiomePresets
import io.github.ayfri.kore.generated.DimensionTypes
import io.github.ayfri.kore.generated.NoiseSettings
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.multiNoiseBiomeSourceParameterListTests() {
	val parameters = multiNoiseBiomeSourceParameterList("my_overworld", BiomePresets.OVERWORLD)

	multiNoiseBiomeSourceParameterLists.last() assertsIs """
		{
			"preset": "minecraft:overworld"
		}
	""".trimIndent()

	parameters assertsIs "multi_noise_biome_source_parameter_list:my_overworld"
	multiNoiseBiomeSourceParameterLists.last().resourceFolder assertsIs "worldgen/multi_noise_biome_source_parameter_list"

	dimension("my_dimension", DimensionTypes.OVERWORLD) {
		noiseGenerator(NoiseSettings.OVERWORLD, multiNoise(parameters))
	}

	dimensions.last() assertsIs """
		{
			"type": "minecraft:overworld",
			"generator": {
				"type": "minecraft:noise",
				"settings": "minecraft:overworld",
				"biome_source": {
					"type": "minecraft:multi_noise",
					"preset": "multi_noise_biome_source_parameter_list:my_overworld"
				}
			}
		}
	""".trimIndent()
}

class MultiNoiseBiomeSourceParameterListTests : FunSpec({
	test("multi noise biome source parameter list") {
		dataPack("multi_noise_biome_source_parameter_list") {
			pretty()
			multiNoiseBiomeSourceParameterListTests()
		}
	}
})
