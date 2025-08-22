package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.spawncondition.add
import io.github.ayfri.kore.data.spawncondition.biome
import io.github.ayfri.kore.data.spawncondition.moonBrightness
import io.github.ayfri.kore.data.spawncondition.structures
import io.github.ayfri.kore.features.frogvariants.frogVariant
import io.github.ayfri.kore.features.frogvariants.spawnConditions
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.ConfiguredStructures
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.Textures

fun DataPack.frogVariantTests() {
	frogVariant("test_frog_variant", Textures.Entity.Frog.TEMPERATE_FROG) {
		spawnConditions {
			add(10)
			biome(5, Biomes.SNOWY_PLAINS)
			biome(2, Tags.Worldgen.Biome.SPAWNS_COLD_VARIANT_FROGS)
			moonBrightness(1, 1.0)
			moonBrightness(1, 0.5, 0.75)
			structures(0, ConfiguredStructures.END_CITY)
		}
	}

	frogVariants.last() assertsIs """
		{
			"asset_id": "minecraft:entity/frog/temperate_frog",
			"spawn_conditions": [
				{
					"priority": 10
				},
				{
					"priority": 5,
					"condition": {
						"type": "minecraft:biome",
						"biomes": "minecraft:snowy_plains"
					}
				},
				{
					"priority": 2,
					"condition": {
						"type": "minecraft:biome",
						"biomes": "#minecraft:spawns_cold_variant_frogs"
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
						"structures": "minecraft:end_city"
					}
				}
			]
		}
	""".trimIndent()
}
