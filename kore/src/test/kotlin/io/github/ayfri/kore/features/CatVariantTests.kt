package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.spawncondition.add
import io.github.ayfri.kore.data.spawncondition.biome
import io.github.ayfri.kore.data.spawncondition.moonBrightness
import io.github.ayfri.kore.data.spawncondition.structures
import io.github.ayfri.kore.features.catvariants.catVariant
import io.github.ayfri.kore.features.catvariants.spawnConditions
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.ConfiguredStructures
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.Textures

fun DataPack.catVariantTests() {
	catVariant("test_cat_variant", Textures.Entity.Cat.TABBY) {
		spawnConditions {
			add(10)
			biome(5, Biomes.PLAINS)
			biome(2, Tags.Worldgen.Biome.IS_OVERWORLD)
			moonBrightness(1, 1.0)
			moonBrightness(1, 0.5, 0.75)
			structures(0, ConfiguredStructures.VILLAGE_PLAINS)
		}
	}

	catVariants.last() assertsIs """
		{
			"asset_id": "minecraft:entity/cat/tabby",
			"spawn_conditions": [
				{
					"priority": 10
				},
				{
					"priority": 5,
					"condition": {
						"type": "minecraft:biome",
						"biomes": "minecraft:plains"
					}
				},
				{
					"priority": 2,
					"condition": {
						"type": "minecraft:biome",
						"biomes": "#minecraft:is_overworld"
					}
				},
				{
					"priority": 1,
					"condition": {
						"type": "minecraft:moon_brightness",
						"range": 1.0
					}
				},
				{
					"priority": 1,
					"condition": {
						"type": "minecraft:moon_brightness",
						"range": {
							"min": 0.5,
							"max": 0.75
						}
					}
				},
				{
					"priority": 0,
					"condition": {
						"type": "minecraft:structure",
						"structures": "minecraft:village_plains"
					}
				}
			]
		}
	""".trimIndent()
}
