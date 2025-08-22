package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.spawncondition.biome
import io.github.ayfri.kore.features.wolfvariants.assets
import io.github.ayfri.kore.features.wolfvariants.spawnConditions
import io.github.ayfri.kore.features.wolfvariants.wolfVariant
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.Textures

fun DataPack.wolfVariantTests() {
	wolfVariant("test_wolf_variant") {
		assets(
			angry = Textures.Entity.Wolf.WOLF_STRIPED,
			tame = Textures.Entity.Wolf.WOLF_RUSTY_ANGRY,
			wild = Textures.Entity.Wolf.WOLF_BLACK,
		)
		spawnConditions {
			biome(5, Biomes.OCEAN, Biomes.SNOWY_SLOPES)
		}
	}

	wolfVariants.last() assertsIs """
		{
			"assets": {
				"angry": "minecraft:entity/wolf/wolf_striped",
				"tame": "minecraft:entity/wolf/wolf_rusty_angry",
				"wild": "minecraft:entity/wolf/wolf_black"
			},
			"spawn_conditions": [
				{
					"priority": 5,
					"condition": {
						"type": "minecraft:biome",
						"biomes": [
							"minecraft:ocean",
							"minecraft:snowy_slopes"
						]
					}
				}
			]
		}
	""".trimIndent()
}
