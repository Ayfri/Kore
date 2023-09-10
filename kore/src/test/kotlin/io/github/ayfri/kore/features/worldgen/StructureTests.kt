package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldgen.biome.types.spawner
import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.features.worldgen.structures.types.biomes
import io.github.ayfri.kore.features.worldgen.structures.types.desertPyramid
import io.github.ayfri.kore.features.worldgen.structures.types.spawnOverrides
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.EntityTypes

fun DataPack.structureTests() {
	structuresBuilder.desertPyramid("my_desert_pyramid") {
		biomes(Biomes.DESERT, Biomes.BADLANDS)
		spawnOverrides {
			monster(BoundingBox.FULL) {
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
				"minecraft:badlands"
			],
			"step": "top_layer_modification",
			"spawn_overrides": {
				"monster": {
					"bounding_box": "full",
					"spawns": [
						{
							"type": "minecraft:zombie",
							"weight": 1,
							"minCount": 4,
							"maxCount": 4
						}
					]
				}
			},
			"terrain_adaptation": "beard_box"
		}
	""".trimIndent()
}
