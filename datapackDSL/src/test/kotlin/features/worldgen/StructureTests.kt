package features.worldgen

import DataPack
import features.worldgen.biome.types.monster
import features.worldgen.biome.types.spawner
import features.worldgen.structures.GenerationStep
import features.worldgen.structures.TerrainAdaptation
import features.worldgen.structures.structuresBuilder
import features.worldgen.structures.types.biomes
import features.worldgen.structures.types.desertPyramid
import features.worldgen.structures.types.spawnOverrides
import generated.Biomes
import generated.EntityTypes
import generated.Tags
import utils.assertsIs

fun DataPack.structureTests() {
	structuresBuilder.desertPyramid {
		biomes(Biomes.DESERT, Tags.Worldgen.Biome.IS_BEACH)
		spawnOverrides {
			monster {
				spawner(EntityTypes.ZOMBIE, 1, 4, 4)
			}
		}
		step = GenerationStep.TOP_LAYER_MODIFICATION
		terrainAdaptation = TerrainAdaptation.BEARD_BOX
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:desert_pyramid",
			"biomes": [
				"minecraft:desert",
				"#minecraft:is_beach"
			],
			"step": "top_layer_modification",
			"spawn_overrides": {
				"monster": [
					{
						"type": "minecraft:zombie",
						"weight": 1,
						"min_count": 4,
						"max_count": 4
					}
				]
			},
			"terrain_adaptation": "beard_box"
		}
	""".trimIndent()
}
