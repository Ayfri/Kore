package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.spawncondition.structures
import io.github.ayfri.kore.features.cowvariants.CowModel
import io.github.ayfri.kore.features.cowvariants.cowVariant
import io.github.ayfri.kore.features.cowvariants.spawnConditions
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.Textures

fun DataPack.cowVariantTests() {
	cowVariant("test_cow_variant", Textures.Entity.Cow.COLD_COW, CowModel.COLD) {
		spawnConditions {
			structures(0, Tags.Worldgen.Structure.MINESHAFT)
		}
	}

	cowVariants.last() assertsIs """
		{
			"asset_id": "minecraft:entity/cow/cold_cow",
			"model": "cold",
			"spawn_conditions": [
				{
					"priority": 0,
					"condition": {
						"type": "minecraft:structure",
						"structures": "#minecraft:mineshaft"
					}
				}
			]
		}
	""".trimIndent()
}
