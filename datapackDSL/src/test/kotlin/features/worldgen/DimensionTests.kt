package features.worldgen

import DataPack
import commands.execute.execute
import commands.execute.run
import commands.say
import features.worldgen.dimension.biomesource.multiNoise
import features.worldgen.dimension.biomesource.multinoise.doubleOrPair
import features.worldgen.dimension.biomesource.multinoise.multiNoiseEntry
import features.worldgen.dimension.dimension
import features.worldgen.dimension.generator.flatGenerator
import features.worldgen.dimension.generator.layer
import features.worldgen.dimension.generator.noiseGenerator
import features.worldgen.noisesettings.noiseSettings
import functions.load
import generated.Biomes
import generated.Blocks
import generated.DimensionTypes
import generated.StructureSets
import utils.assertsIs

fun DataPack.dimensionTests() {
	val dimension = dimension("my_dimension", DimensionTypes.OVERWORLD) {
		noiseGenerator(
			settings = noiseSettings("default_noise_settings") {},
			biomeSource = multiNoise {
				this += multiNoiseEntry(Biomes.BEACH) {
					weirdness = doubleOrPair(0.5)
				}
			}
		)
	}

	dimensions.last() assertsIs """
		{
			"type": "minecraft:overworld",
			"generator": {
				"type": "minecraft:noise",
				"settings": "$name:default_noise_settings",
				"biome_source": {
					"type": "minecraft:multi_noise",
					"biomes": [
						{
							"biome": "minecraft:beach",
							"parameters": {
								"temperature": 0.0,
								"humidity": 0.0,
								"continentalness": 0.0,
								"erosion": 0.0,
								"weirdness": 0.5,
								"depth": 0.0,
								"offset": 0.0
							}
						}
					]
				}
			}
		}
	""".trimIndent()

	dimension("my_flat_nether_dimension", DimensionTypes.THE_NETHER) {
		flatGenerator(Biomes.NETHER_WASTES) {
			features = true
			structureOverrides = listOf(StructureSets.NETHER_COMPLEXES)

			layer(Blocks.NETHERRACK, 1)
			layer(Blocks.LAVA, 2)
			layer(Blocks.NETHERRACK, 10)
		}
	}

	dimensions.last() assertsIs """
		{
			"type": "minecraft:the_nether",
			"generator": {
				"type": "minecraft:flat",
				"settings": {
					"biome": "minecraft:nether_wastes",
					"features": true,
					"layers": [
						{
							"block": "minecraft:netherrack",
							"height": 1
						},
						{
							"block": "minecraft:lava",
							"height": 2
						},
						{
							"block": "minecraft:netherrack",
							"height": 10
						}
					],
					"structure_overrides": "minecraft:nether_complexes"
				}
			}
		}
	""".trimIndent()

	load {
		execute {
			inDimension(dimension)

			run {
				say("Hello World!")
			}
		}
	}
}
