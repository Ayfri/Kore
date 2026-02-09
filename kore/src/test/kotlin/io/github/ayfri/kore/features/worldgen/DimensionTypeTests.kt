package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldgen.dimensiontype.attributes
import io.github.ayfri.kore.features.worldgen.dimensiontype.dimensionType
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.cloudHeight
import io.github.ayfri.kore.features.worldgen.intproviders.*

fun DataPack.dimensionTypeTests() {
	dimensionType("my_dimension") {
		attributes {
			cloudHeight(128f)
		}
		height = 512
		logicalHeight = 512
		minY = 0
		monsterSpawnLightLevel = uniform(7, 15)
	}

	dimensionTypes.last() assertsIs """
		{
			"attributes": {
				"minecraft:visual/cloud_height": 128.0
			},
			"natural": true,
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
			"monster_spawn_block_light_limit": 0
		}
		""".trimIndent()

	dimensionType("my_dimension2") {
		monsterSpawnLightLevel = constant(2)
	}

	dimensionTypes.last() assertsIs """
		{
			"natural": true,
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
