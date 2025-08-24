package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.spawncondition.structures
import io.github.ayfri.kore.features.pigvariants.PigModel
import io.github.ayfri.kore.features.pigvariants.pigVariant
import io.github.ayfri.kore.features.pigvariants.spawnConditions
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.Textures

fun DataPack.pigVariantTests() {
	pigVariant("test_pig_variant", Textures.Entity.Pig.COLD_PIG, PigModel.COLD) {
		spawnConditions {
			structures(0, Tags.Worldgen.Structure.ON_TREASURE_MAPS)
		}
	}

	pigVariants.last() assertsIs """
		{
			"asset_id": "minecraft:entity/pig/cold_pig",
			"model": "cold",
			"spawn_conditions": [
				{
					"priority": 0,
					"condition": {
						"type": "minecraft:structure",
						"structures": "#minecraft:on_treasure_maps"
					}
				}
			]
		}
	""".trimIndent()
}
