package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.dimension.biomesource.multiNoise
import io.github.ayfri.kore.features.worldgen.dimension.biomesource.theEnd
import io.github.ayfri.kore.features.worldgen.dimension.generator.noiseGenerator
import io.github.ayfri.kore.features.worldgen.worldpreset.dimension
import io.github.ayfri.kore.features.worldgen.worldpreset.worldPreset
import io.github.ayfri.kore.generated.BiomePresets
import io.github.ayfri.kore.generated.DimensionTypes
import io.github.ayfri.kore.generated.Dimensions
import io.github.ayfri.kore.generated.NoiseSettings
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import io.github.ayfri.kore.generated.arguments.types.DimensionTypeArgument
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.worldPresetTests() {
	worldPreset("my_world_preset") {
		dimension(Dimensions.OVERWORLD, DimensionTypes.OVERWORLD) {
			noiseGenerator(NoiseSettings.OVERWORLD, multiNoise(BiomePresets.OVERWORLD))
		}
		dimension(Dimensions.THE_NETHER, DimensionTypes.THE_NETHER) {
			noiseGenerator(NoiseSettings.NETHER, multiNoise(BiomePresets.NETHER))
		}
		dimension(Dimensions.THE_END, DimensionTypes.THE_END) {
			noiseGenerator(NoiseSettings.END, theEnd())
		}
	}

	worldPresets.last() assertsIs """
		{
			"dimensions": {
				"minecraft:overworld": {
					"type": "minecraft:overworld",
					"generator": {
						"type": "minecraft:noise",
						"settings": "minecraft:overworld",
						"biome_source": {
							"type": "minecraft:multi_noise",
							"preset": "minecraft:overworld"
						}
					}
				},
				"minecraft:the_nether": {
					"type": "minecraft:the_nether",
					"generator": {
						"type": "minecraft:noise",
						"settings": "minecraft:nether",
						"biome_source": {
							"type": "minecraft:multi_noise",
							"preset": "minecraft:nether"
						}
					}
				},
				"minecraft:the_end": {
					"type": "minecraft:the_end",
					"generator": {
						"type": "minecraft:noise",
						"settings": "minecraft:end",
						"biome_source": {
							"type": "minecraft:the_end"
						}
					}
				}
			}
		}
	""".trimIndent()

	worldPreset("my_custom_world_preset") {
		dimension(Dimensions.OVERWORLD, DimensionTypeArgument("skylands_type", "test")) {
			noiseGenerator(NoiseSettings.OVERWORLD, theEnd())
		}
		dimension(DimensionArgument("mining", "test"), DimensionTypes.OVERWORLD_CAVES) {
			noiseGenerator(NoiseSettings.CAVES, multiNoise(BiomePresets.OVERWORLD))
		}
	}

	worldPresets.last() assertsIs """
		{
			"dimensions": {
				"minecraft:overworld": {
					"type": "test:skylands_type",
					"generator": {
						"type": "minecraft:noise",
						"settings": "minecraft:overworld",
						"biome_source": {
							"type": "minecraft:the_end"
						}
					}
				},
				"test:mining": {
					"type": "minecraft:overworld_caves",
					"generator": {
						"type": "minecraft:noise",
						"settings": "minecraft:caves",
						"biome_source": {
							"type": "minecraft:multi_noise",
							"preset": "minecraft:overworld"
						}
					}
				}
			}
		}
	""".trimIndent()
}

class WorldPresetTests : FunSpec({
	test("world preset") {
		dataPack("worldPreset") {
			pretty()
			worldPresetTests()
		}
	}
})
