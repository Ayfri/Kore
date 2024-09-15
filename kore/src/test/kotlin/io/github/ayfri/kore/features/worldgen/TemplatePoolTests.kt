package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldgen.structures.types.jigsaw.LiquidSettings
import io.github.ayfri.kore.features.worldgen.templatepool.Projection
import io.github.ayfri.kore.features.worldgen.templatepool.elements
import io.github.ayfri.kore.features.worldgen.templatepool.elements.empty
import io.github.ayfri.kore.features.worldgen.templatepool.elements.feature
import io.github.ayfri.kore.features.worldgen.templatepool.elements.list
import io.github.ayfri.kore.features.worldgen.templatepool.elements.single
import io.github.ayfri.kore.features.worldgen.templatepool.templatePool
import io.github.ayfri.kore.generated.PlacedFeatures
import io.github.ayfri.kore.generated.ProcessorLists
import io.github.ayfri.kore.generated.Structures

fun DataPack.templatePoolTests() {
	templatePool("test") {
		elements {
			empty(2)
			list(1) {
				single(
					Projection.TERRAIN_MATCHING,
					Structures.EndCity.BRIDGE_END,
					ProcessorLists.HOUSING,
					LiquidSettings.IGNORE_WATERLOGGING
				)
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
								"processors": "minecraft:housing",
								"override_liquid_settings": "ignore_waterlogging"
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
