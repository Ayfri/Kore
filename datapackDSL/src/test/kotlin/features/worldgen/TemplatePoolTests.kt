package features.worldgen

import DataPack
import features.worldgen.templatepool.Projection
import features.worldgen.templatepool.elements
import features.worldgen.templatepool.elements.empty
import features.worldgen.templatepool.elements.feature
import features.worldgen.templatepool.elements.list
import features.worldgen.templatepool.elements.single
import features.worldgen.templatepool.templatePool
import generated.PlacedFeatures
import generated.ProcessorLists
import generated.Structures
import utils.assertsIs

fun DataPack.templatePoolTests() {
	templatePool("test") {
		elements {
			empty(2)
			list(1) {
				single(Projection.TERRAIN_MATCHING, Structures.EndCity.BRIDGE_END, ProcessorLists.HOUSING)
				feature(feature = PlacedFeatures.ORE_DIAMOND)
			}
		}
	}

	templatePools.last() assertsIs """
		{
			"fallback": "minecraft:empty",
			"elements": [
				{
					"weight": 2,
					"element": {
						"element_type": "minecraft:empty_pool_element"
					}
				},
				{
					"weight": 1,
					"element": {
						"element_type": "minecraft:list_pool_element",
						"projection": "rigid",
						"elements": [
							{
								"element_type": "minecraft:single_pool_element",
								"projection": "terrain_matching",
								"location": "minecraft:end_city/bridge_end",
								"processors": "minecraft:housing"
							},
							{
								"element_type": "minecraft:feature_pool_element",
								"projection": "rigid",
								"feature": "minecraft:ore_diamond"
							}
						]
					}
				}
			]
		}
	""".trimIndent()
}
