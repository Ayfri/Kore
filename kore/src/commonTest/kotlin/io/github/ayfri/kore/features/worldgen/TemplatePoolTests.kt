package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.structures.types.jigsaw.LiquidSettings
import io.github.ayfri.kore.features.worldgen.templatepool.Projection
import io.github.ayfri.kore.features.worldgen.templatepool.elements.*
import io.github.ayfri.kore.features.worldgen.templatepool.templatePool
import io.github.ayfri.kore.generated.PlacedFeatures
import io.github.ayfri.kore.generated.ProcessorLists
import io.github.ayfri.kore.generated.Structures
import io.github.ayfri.kore.generated.TemplatePools
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.templatePoolTests() {
	templatePool("every_element") {
		fallback = TemplatePools.Village.Plains.TOWN_CENTERS

		empty()
		empty(4)
		feature(PlacedFeatures.ORE_DIAMOND)
		feature(PlacedFeatures.PILE_HAY, weight = 3, projection = Projection.TERRAIN_MATCHING)
		legacySingle(Structures.Village.Plains.TownCenters.PLAINS_FOUNTAIN_01, ProcessorLists.EMPTY)
		legacySingle(Structures.Village.Plains.TownCenters.PLAINS_MEETING_POINT_1, weight = 2) {
			overrideLiquidSettings = LiquidSettings.APPLY_WATERLOGGING
		}
		single(Structures.EndCity.BRIDGE_END)
		single(Structures.EndCity.BRIDGE_GENTLE_STAIRS, ProcessorLists.HOUSING, weight = 2) {
			projection = Projection.TERRAIN_MATCHING
			overrideLiquidSettings = LiquidSettings.IGNORE_WATERLOGGING
		}
		list(weight = 5, projection = Projection.TERRAIN_MATCHING) {
			empty()
			feature(PlacedFeatures.ORE_DIAMOND)
			legacySingle(Structures.Village.Plains.TownCenters.PLAINS_FOUNTAIN_01, ProcessorLists.EMPTY)
			single(Structures.EndCity.BRIDGE_END, ProcessorLists.HOUSING) {
				overrideLiquidSettings = LiquidSettings.APPLY_WATERLOGGING
			}
		}
	}

	templatePools.last() assertsIs """
		{
			"fallback": "minecraft:village/plains/town_centers",
			"elements": [
				{
					"weight": 1,
					"element": {
						"element_type": "minecraft:empty_pool_element"
					}
				},
				{
					"weight": 4,
					"element": {
						"element_type": "minecraft:empty_pool_element"
					}
				},
				{
					"weight": 1,
					"element": {
						"element_type": "minecraft:feature_pool_element",
						"projection": "rigid",
						"feature": "minecraft:ore_diamond"
					}
				},
				{
					"weight": 3,
					"element": {
						"element_type": "minecraft:feature_pool_element",
						"projection": "terrain_matching",
						"feature": "minecraft:pile_hay"
					}
				},
				{
					"weight": 1,
					"element": {
						"element_type": "minecraft:legacy_single_pool_element",
						"projection": "rigid",
						"location": "minecraft:village/plains/town_centers/plains_fountain_01",
						"processors": "minecraft:empty"
					}
				},
				{
					"weight": 2,
					"element": {
						"element_type": "minecraft:legacy_single_pool_element",
						"projection": "rigid",
						"location": "minecraft:village/plains/town_centers/plains_meeting_point_1",
						"processors": "minecraft:empty",
						"override_liquid_settings": "apply_waterlogging"
					}
				},
				{
					"weight": 1,
					"element": {
						"element_type": "minecraft:single_pool_element",
						"projection": "rigid",
						"location": "minecraft:end_city/bridge_end",
						"processors": "minecraft:empty"
					}
				},
				{
					"weight": 2,
					"element": {
						"element_type": "minecraft:single_pool_element",
						"projection": "terrain_matching",
						"location": "minecraft:end_city/bridge_gentle_stairs",
						"processors": "minecraft:housing",
						"override_liquid_settings": "ignore_waterlogging"
					}
				},
				{
					"weight": 5,
					"element": {
						"element_type": "minecraft:list_pool_element",
						"projection": "terrain_matching",
						"elements": [
							{
								"element_type": "minecraft:empty_pool_element"
							},
							{
								"element_type": "minecraft:feature_pool_element",
								"projection": "rigid",
								"feature": "minecraft:ore_diamond"
							},
							{
								"element_type": "minecraft:legacy_single_pool_element",
								"projection": "rigid",
								"location": "minecraft:village/plains/town_centers/plains_fountain_01",
								"processors": "minecraft:empty"
							},
							{
								"element_type": "minecraft:single_pool_element",
								"projection": "rigid",
								"location": "minecraft:end_city/bridge_end",
								"processors": "minecraft:housing",
								"override_liquid_settings": "apply_waterlogging"
							}
						]
					}
				}
			]
		}
	""".trimIndent()

	templatePool("defaults")

	templatePools.last() assertsIs """
		{
			"fallback": "minecraft:empty",
			"elements": []
		}
	""".trimIndent()
}

class TemplatePoolTests : FunSpec({
	test("template pool") {
		dataPack("templatePool") {
			pretty()
			templatePoolTests()
		}
	}
})
