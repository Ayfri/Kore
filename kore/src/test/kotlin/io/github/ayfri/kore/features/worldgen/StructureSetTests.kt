package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldgen.structureset.*
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.ConfiguredStructures

fun DataPack.structureSetTests() {
	val set = structureSet("my_structure_set") {
		structure(ConfiguredStructures.MINESHAFT)
		randomSpreadPlacement {
			spreadType = SpreadType.LINEAR
			spacing = 2
			separation = 1
			frequency = 0.5
		}
	}

	structureSet("my_second_structure_set") {
		structure(ConfiguredStructures.MINESHAFT)
		concentricRingsPlacement {
			exclusionZone(set, 5)
			salt = 0
			distance = 2
			spread = 1
			count = 2
			frequency = 0.5
			preferredBiomes(Biomes.DESERT)
		}
	}

	structureSets.last() assertsIs """
		{
			"structures": [
				{
					"structure": "minecraft:mineshaft",
					"weight": 1
				}
			],
			"placement": {
				"type": "minecraft:concentric_rings",
				"salt": 0,
				"frequency": 0.5,
				"exclusion_zone": {
					"other_set": "$name:my_structure_set",
					"chunk_count": 5
				},
				"distance": 2,
				"spread": 1,
				"count": 2,
				"preferred_biomes": "minecraft:desert"
			}
		}
	""".trimIndent()
}
