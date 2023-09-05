package features.worldgen

import DataPack
import assertions.assertsIs
import features.worldgen.dimension.biomesource.multiNoise
import features.worldgen.dimension.biomesource.theEnd
import features.worldgen.dimension.generator.noiseGenerator
import features.worldgen.worldpreset.dimension
import features.worldgen.worldpreset.worldPreset
import generated.BiomePresets
import generated.DimensionTypes
import generated.NoiseSettings

fun DataPack.worldPresetTests() {
	worldPreset("my_world_preset") {
		dimension(DimensionTypes.OVERWORLD) {
			generator = noiseGenerator(NoiseSettings.OVERWORLD, multiNoise(BiomePresets.OVERWORLD))
		}
		dimension(DimensionTypes.THE_NETHER) {
			generator = noiseGenerator(NoiseSettings.NETHER, multiNoise(BiomePresets.NETHER))
		}
		dimension(DimensionTypes.THE_END) {
			generator = noiseGenerator(NoiseSettings.END, theEnd())
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
}
