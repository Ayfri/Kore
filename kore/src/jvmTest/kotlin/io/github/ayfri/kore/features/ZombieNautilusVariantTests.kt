package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.spawncondition.structures
import io.github.ayfri.kore.features.zombienautilusvariants.ZombieNautilusModel
import io.github.ayfri.kore.features.zombienautilusvariants.spawnConditions
import io.github.ayfri.kore.features.zombienautilusvariants.zombieNautilusVariant
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.Textures
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun DataPack.zombieNautilusVariantTests() {
	zombieNautilusVariant(
		"test_zombie_nautilus_variant",
		Textures.Entity.Nautilus.ZOMBIE_NAUTILUS_CORAL,
		ZombieNautilusModel.WARM
	) {
		spawnConditions {
			structures(0, Tags.Worldgen.Structure.ON_TREASURE_MAPS)
		}
	}

	zombieNautilusVariants.last() assertsIs """
		{
			"asset_id": "minecraft:entity/nautilus/zombie_nautilus_coral",
			"model": "warm",
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

class ZombieNautilusVariantTests : FunSpec({
	test("zombie nautilus variant") {
		testDataPack("zombieNautilusVariant") {
			pretty()
			zombieNautilusVariantTests()
		}.apply {
			assertGeneratorsGenerated()
			generate()
		}
	}
})
