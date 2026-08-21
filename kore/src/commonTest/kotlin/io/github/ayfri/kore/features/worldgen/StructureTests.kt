package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.biome.types.spawner
import io.github.ayfri.kore.features.worldgen.heightproviders.constantAbsolute
import io.github.ayfri.kore.features.worldgen.heightproviders.uniformHeightProvider
import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.features.worldgen.structures.types.*
import io.github.ayfri.kore.features.worldgen.structures.types.jigsaw.LiquidSettings
import io.github.ayfri.kore.features.worldgen.verticalanchors.aboveBottom
import io.github.ayfri.kore.features.worldgen.verticalanchors.belowTop
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.TemplatePools
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.structureTests() {
	structuresBuilder.desertPyramid("my_desert_pyramid") {
		biomes(Biomes.DESERT, Biomes.BADLANDS)
		spawnOverrides {
			monster(BoundingBox.FULL) {
				spawner(EntityTypes.ZOMBIE, 1, 4, 4)
			}
		}
		step = GenerationStep.TOP_LAYER_MODIFICATION
		terrainAdaptation = TerrainAdaptation.BEARD_BOX
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:desert_pyramid",
			"biomes": [
				"minecraft:desert",
				"minecraft:badlands"
			],
			"step": "top_layer_modification",
			"spawn_overrides": {
				"monster": {
					"bounding_box": "full",
					"spawns": [
						{
							"type": "minecraft:zombie",
							"weight": 1,
							"minCount": 4,
							"maxCount": 4
						}
					]
				}
			},
			"terrain_adaptation": "beard_box"
		}
	""".trimIndent()

	structuresBuilder.jigsaw("my_jigsaw", startPool = TemplatePools.Empty) {
		biomes(Biomes.DESERT, Biomes.BADLANDS)
		step = GenerationStep.TOP_LAYER_MODIFICATION
		startHeight = constantAbsolute(10)
		startJigsawName = "minecraft:empty_pool"
		projectStartToHeightmap = HeightMap.WORLD_SURFACE_WG
		maxDistanceFromCenter(80)
		useExpansionHack = false
		poolAliases {
			directPoolAlias(TemplatePools.Empty, TemplatePools.Empty)
			randomPoolAlias(TemplatePools.Empty) {
				weightedPoolEntry(1, TemplatePools.Empty)
			}
			randomGroupPoolAlias {
				weightedGroupEntry(1) {
					directPoolAlias(TemplatePools.Empty, TemplatePools.Empty)
					randomPoolAlias(TemplatePools.Empty) {
						weightedPoolEntry(1, TemplatePools.Empty)
					}
				}
			}
		}
		dimensionPadding(10, 20)
		liquidSettings = LiquidSettings.IGNORE_WATERLOGGING
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:jigsaw",
			"biomes": [
				"minecraft:desert",
				"minecraft:badlands"
			],
			"step": "top_layer_modification",
			"spawn_overrides": {},
			"start_pool": "minecraft:empty",
			"size": 0,
			"start_height": {
				"absolute": 10
			},
			"start_jigsaw_name": "minecraft:empty_pool",
			"project_start_to_heightmap": "WORLD_SURFACE_WG",
			"max_distance_from_center": 80,
			"use_expansion_hack": false,
			"pool_aliases": [
				{
					"type": "minecraft:direct",
					"alias": "minecraft:empty",
					"target": "minecraft:empty"
				},
				{
					"type": "minecraft:random",
					"alias": "minecraft:empty",
					"targets": [
						{
							"weight": 1,
							"data": "minecraft:empty"
						}
					]
				},
				{
					"type": "minecraft:random_group",
					"groups": [
						{
							"weight": 1,
							"data": [
								{
									"type": "minecraft:direct",
									"alias": "minecraft:empty",
									"target": "minecraft:empty"
								},
								{
									"type": "minecraft:random",
									"alias": "minecraft:empty",
									"targets": [
										{
											"weight": 1,
											"data": "minecraft:empty"
										}
									]
								}
							]
						}
					]
				}
			],
			"dimension_padding": {
				"top": 10,
				"bottom": 20
			},
			"liquid_settings": "ignore_waterlogging"
		}
	""".trimIndent()

	structuresBuilder.netherFossil("my_nether_fossil") {
		biomes(Biomes.SOUL_SAND_VALLEY)
		height = uniformHeightProvider(aboveBottom(32), belowTop(2))
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:nether_fossil",
			"biomes": "minecraft:soul_sand_valley",
			"step": "underground_decoration",
			"spawn_overrides": {},
			"height": {
				"type": "minecraft:uniform",
				"min_inclusive": {
					"above_bottom": 32
				},
				"max_inclusive": {
					"below_top": 2
				}
			}
		}
	""".trimIndent()
}

class StructureTests : FunSpec({
	test("structure") {
		dataPack("structure") {
			pretty()
			structureTests()
		}
	}
})
