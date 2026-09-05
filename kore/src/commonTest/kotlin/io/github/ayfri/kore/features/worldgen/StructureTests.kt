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
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.generated.TemplatePools
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.structureTests() {
	structuresBuilder.buriedTreasure("my_buried_treasure") {
		biomes(Tags.Worldgen.Biome.IS_BEACH)
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:buried_treasure",
			"biomes": "#minecraft:is_beach",
			"step": "underground_structures",
			"spawn_overrides": {}
		}
	""".trimIndent()

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

	structuresBuilder.endCity("my_end_city") {
		biomes(Biomes.END_HIGHLANDS)
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:end_city",
			"biomes": "minecraft:end_highlands",
			"step": "surface_structures",
			"spawn_overrides": {}
		}
	""".trimIndent()

	structuresBuilder.fortress("my_fortress") {
		biomes(Biomes.NETHER_WASTES)
		spawnOverrides {
			monster {
				spawner(EntityTypes.BLAZE, 10, 2, 3)
			}
		}
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:fortress",
			"biomes": "minecraft:nether_wastes",
			"step": "underground_decoration",
			"spawn_overrides": {
				"monster": {
					"bounding_box": "piece",
					"spawns": [
						{
							"type": "minecraft:blaze",
							"weight": 10,
							"minCount": 2,
							"maxCount": 3
						}
					]
				}
			}
		}
	""".trimIndent()

	structuresBuilder.igloo("my_igloo") {
		biomes(Biomes.SNOWY_PLAINS)
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:igloo",
			"biomes": "minecraft:snowy_plains",
			"step": "surface_structures",
			"spawn_overrides": {}
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
			"size": 1,
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

	structuresBuilder.jigsaw("my_padded_jigsaw", startPool = TemplatePools.Empty) {
		biomes(Biomes.DEEP_DARK)
		size = 7
		dimensionPadding(10)
		maxDistanceFromCenter(116, 80)
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:jigsaw",
			"biomes": "minecraft:deep_dark",
			"step": "surface_structures",
			"spawn_overrides": {},
			"start_pool": "minecraft:empty",
			"size": 7,
			"start_height": {
				"absolute": 0
			},
			"max_distance_from_center": {
				"horizontal": 116,
				"vertical": 80
			},
			"use_expansion_hack": false,
			"dimension_padding": 10
		}
	""".trimIndent()

	structuresBuilder.jungleTemple("my_jungle_temple") {
		biomes(Biomes.BAMBOO_JUNGLE)
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:jungle_temple",
			"biomes": "minecraft:bamboo_jungle",
			"step": "surface_structures",
			"spawn_overrides": {}
		}
	""".trimIndent()

	structuresBuilder.mineshaft("my_mineshaft") {
		biomes(Tags.Worldgen.Biome.IS_BADLANDS)
		mineshaftType = MineshaftType.MESA
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:mineshaft",
			"biomes": "#minecraft:is_badlands",
			"step": "underground_structures",
			"spawn_overrides": {},
			"mineshaft_type": "mesa"
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

	structuresBuilder.oceanMonument("my_ocean_monument") {
		biomes(Biomes.DEEP_OCEAN)
		spawnOverrides {
			waterCreature(BoundingBox.FULL)
			undergroundWaterCreature(BoundingBox.FULL)
			axolotls(BoundingBox.FULL)
			monster(BoundingBox.FULL) {
				spawner(EntityTypes.GUARDIAN, 20, 2, 4)
			}
		}
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:ocean_monument",
			"biomes": "minecraft:deep_ocean",
			"step": "surface_structures",
			"spawn_overrides": {
				"axolotls": {
					"bounding_box": "full",
					"spawns": []
				},
				"monster": {
					"bounding_box": "full",
					"spawns": [
						{
							"type": "minecraft:guardian",
							"weight": 20,
							"minCount": 2,
							"maxCount": 4
						}
					]
				},
				"underground_water_creature": {
					"bounding_box": "full",
					"spawns": []
				},
				"water_creature": {
					"bounding_box": "full",
					"spawns": []
				}
			}
		}
	""".trimIndent()

	structuresBuilder.oceanRuin("my_ocean_ruin") {
		biomes(Biomes.WARM_OCEAN)
		biomeTemp = BiomeTemperature.WARM
		largeProbability = 0.4f
		clusterProbability = 0.8f
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:ocean_ruin",
			"biomes": "minecraft:warm_ocean",
			"step": "surface_structures",
			"spawn_overrides": {},
			"biome_temp": "warm",
			"large_probability": 0.4,
			"cluster_probability": 0.8
		}
	""".trimIndent()

	structuresBuilder.ruinedPortal("my_ruined_portal") {
		biomes(Biomes.DESERT)
		setup(RuinedPortalPlacement.PARTLY_BURIED, mossiness = 0.0f, weight = 1f)
		setup(RuinedPortalPlacement.ON_LAND_SURFACE, overgrown = true, vines = true, weight = 0.5f) {
			canBeCold = true
			replaceWithBlackstone = true
		}
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:ruined_portal",
			"biomes": "minecraft:desert",
			"step": "surface_structures",
			"spawn_overrides": {},
			"setups": [
				{
					"placement": "partly_buried",
					"air_pocket_probability": 0.5,
					"mossiness": 0.0,
					"overgrown": false,
					"vines": false,
					"can_be_cold": false,
					"replace_with_blackstone": false,
					"weight": 1.0
				},
				{
					"placement": "on_land_surface",
					"air_pocket_probability": 0.5,
					"mossiness": 0.2,
					"overgrown": true,
					"vines": true,
					"can_be_cold": true,
					"replace_with_blackstone": true,
					"weight": 0.5
				}
			]
		}
	""".trimIndent()

	structuresBuilder.shipwreck("my_shipwreck") {
		biomes(Tags.Worldgen.Biome.IS_BEACH)
		isBeached = true
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:shipwreck",
			"biomes": "#minecraft:is_beach",
			"step": "surface_structures",
			"spawn_overrides": {},
			"is_beached": true
		}
	""".trimIndent()

	structuresBuilder.stronghold("my_stronghold") {
		biomes(Tags.Worldgen.Biome.IS_OVERWORLD)
		terrainAdaptation = TerrainAdaptation.BURY
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:stronghold",
			"biomes": "#minecraft:is_overworld",
			"step": "surface_structures",
			"spawn_overrides": {},
			"terrain_adaptation": "bury"
		}
	""".trimIndent()

	structuresBuilder.swampHut("my_swamp_hut") {
		biomes(Biomes.SWAMP)
		spawnOverrides {
			monster {
				spawner(EntityTypes.WITCH, 1, 1, 1)
			}
			creature {
				spawner(EntityTypes.CAT, 1, 1, 1)
			}
		}
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:swamp_hut",
			"biomes": "minecraft:swamp",
			"step": "surface_structures",
			"spawn_overrides": {
				"creature": {
					"bounding_box": "piece",
					"spawns": [
						{
							"type": "minecraft:cat",
							"weight": 1,
							"minCount": 1,
							"maxCount": 1
						}
					]
				},
				"monster": {
					"bounding_box": "piece",
					"spawns": [
						{
							"type": "minecraft:witch",
							"weight": 1,
							"minCount": 1,
							"maxCount": 1
						}
					]
				}
			}
		}
	""".trimIndent()

	structuresBuilder.woodlandMansion("my_woodland_mansion") {
		biomes(Biomes.DARK_FOREST)
		spawnOverrides {
			monster(BoundingBox.PIECE)
		}
	}

	structures.last() assertsIs """
		{
			"type": "minecraft:woodland_mansion",
			"biomes": "minecraft:dark_forest",
			"step": "surface_structures",
			"spawn_overrides": {
				"monster": {
					"bounding_box": "piece",
					"spawns": []
				}
			}
		}
	""".trimIndent()

	structure("my_namespaced_structure", DesertPyramid().apply { biomes(Biomes.DESERT) }) {
		namespace = "other"
	} assertsIs "other:my_namespaced_structure"

	structures.last() assertsIs """
		{
			"type": "minecraft:desert_pyramid",
			"biomes": "minecraft:desert",
			"step": "surface_structures",
			"spawn_overrides": {}
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
