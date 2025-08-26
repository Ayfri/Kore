package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.spawncondition.structures
import io.github.ayfri.kore.features.chickenvariants.ChickenModel
import io.github.ayfri.kore.features.chickenvariants.chickenVariant
import io.github.ayfri.kore.features.chickenvariants.spawnConditions
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.Textures

fun DataPack.chickenVariantTests() {
	chickenVariant("test_chicken_variant", Textures.Entity.Chicken.TEMPERATE_CHICKEN, ChickenModel.NORMAL) {
		spawnConditions {
			structures(0, Tags.Worldgen.Structure.VILLAGE)
		}
	}

	chickenVariants.last() assertsIs """
		{
			"asset_id": "minecraft:entity/chicken/temperate_chicken",
			"model": "normal",
			"spawn_conditions": [
				{
					"priority": 0,
					"condition": {
						"type": "minecraft:structure",
						"structures": "#minecraft:village"
					}
				}
			]
		}
	""".trimIndent()
}
