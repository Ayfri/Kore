package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldgen.dimensiontype.dimensionType
import io.github.ayfri.kore.features.worldgen.intproviders.*

fun DataPack.dimensionTypeTests() {
	dimensionType("my_dimension") {
		cloudHeight = 128
		height = 512
		logicalHeight = 512
		minY = 0
		monsterSpawnLightLevel = uniform(7, 15)
	}

	dimensionTypes.last() assertsIs """
		{
			"ultrawarm": false,
			"natural": true,
			"piglin_safe": false,
			"respawn_anchor_works": false,
			"bed_works": true,
			"has_raids": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 512,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 512,
			"monster_spawn_light_level": {
				"type": "minecraft:uniform",
				"min_inclusive": 7,
				"max_inclusive": 15
			},
			"monster_spawn_block_light_limit": 0,
			"cloud_height": 128
		}
		""".trimIndent()

	dimensionType("my_dimension2") {
		monsterSpawnLightLevel = constant(2)
	}

	dimensionTypes.last() assertsIs """
		{
			"ultrawarm": false,
			"natural": true,
			"piglin_safe": false,
			"respawn_anchor_works": false,
			"bed_works": true,
			"has_raids": true,
			"has_skylight": true,
			"has_ceiling": false,
			"coordinate_scale": 1.0,
			"ambient_light": 0.0,
			"logical_height": 0,
			"infiniburn": "#minecraft:infiniburn_overworld",
			"min_y": 0,
			"height": 16,
			"monster_spawn_light_level": 2,
			"monster_spawn_block_light_limit": 0
		}
	""".trimIndent()

	dimensionType("my_dimension3") {
		monsterSpawnLightLevel = weightedList {
			this += entry(1, constant(2))
			add(entry(1, clampedNormal(0, 10, 5f, 2f)))
		}
	}
}
