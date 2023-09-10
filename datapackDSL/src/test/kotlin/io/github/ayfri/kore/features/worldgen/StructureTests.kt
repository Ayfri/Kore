package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldgen.biome.types.monster
import io.github.ayfri.kore.features.worldgen.biome.types.spawner
import io.github.ayfri.kore.features.worldgen.structures.GenerationStep
import io.github.ayfri.kore.features.worldgen.structures.TerrainAdaptation
import io.github.ayfri.kore.features.worldgen.structures.structuresBuilder
import io.github.ayfri.kore.features.worldgen.structures.types.biomes
import io.github.ayfri.kore.features.worldgen.structures.types.desertPyramid
import io.github.ayfri.kore.features.worldgen.structures.types.spawnOverrides
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Tags

fun DataPack.structureTests() {
	structuresBuilder.desertPyramid {
		biomes(io.github.ayfri.kore.generated.Biomes.DESERT, Tags.Worldgen.Biome.IS_BEACH)
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
