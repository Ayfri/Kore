package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.StructureRotation
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.blockpredicate.*
import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.*
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.*
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.cherryFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.darkOakFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize.threeLayersFeatureSize
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.alterGround
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.attachedToLeaves
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.cherryTrunkPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.darkOakTrunkPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeaturesBuilder
import io.github.ayfri.kore.features.worldgen.configuredfeature.target
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.intproviders.uniform
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.Surface
import io.github.ayfri.kore.features.worldgen.ruletest.randomBlockMatch
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec
import io.github.ayfri.kore.features.worldgen.floatproviders.constant as constantFloat

fun DataPack.configuredFeatureTests() {
	configuredFeaturesBuilder.endPlatform("end_platform")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:end_platform",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.bamboo("test_bamboo", probability = 0.5)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:bamboo",
			"config": {
				"probability": 0.5
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.basaltColumns("test_basalt_columns", reach = 3, height = 6)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:basalt_columns",
			"config": {
				"reach": 3,
				"height": 6
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.basaltPillar("test_basalt_pillar")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:basalt_pillar",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.blockBlob("test_block_blob", canPlaceOn = Solid, state = blockStateStone())

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:block_blob",
			"config": {
				"can_place_on": {
					"type": "minecraft:solid"
				},
				"state": {
					"Name": "minecraft:stone"
				}
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.blockPile("test_block_pile", simpleStateProvider(Blocks.GRAVEL))

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:block_pile",
			"config": {
				"state_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:gravel"
					}
				}
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.blockColumn("test_block_predicates_and_complex_provider", direction = Direction.DOWN) {
		allowedPlacement = allOf {
			not {
				matchingBlocks(
					blocks = listOf(
						Blocks.DIRT,
						Blocks.STONE
					)
				)
			}

			solid()
		}

		layers {
			layer(provider = dualNoiseProvider {
				slowNoise {
					firstOctave = 1
					amplitudes = listOf(1.2)
				}

				variety(1, 2)
				scale = 1.0
				slowScale = 1.0

				states = listOf(
					blockState(Blocks.STONE),
					blockState(Blocks.DIRT)
				)
			})
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:block_column",
			"config": {
				"direction": "down",
				"allowed_placement": {
					"type": "minecraft:all_of",
					"predicates": [
						{
							"type": "minecraft:not",
							"predicate": {
								"type": "minecraft:matching_blocks",
								"blocks": [
									"minecraft:dirt",
									"minecraft:stone"
								]
							}
						},
						{
							"type": "minecraft:solid"
						}
					]
				},
				"prioritize_tip": false,
				"layers": [
					{
						"height": 0,
						"provider": {
							"type": "minecraft:dual_noise_provider",
							"seed": 0,
							"noise": {
								"firstOctave": 0,
								"amplitudes": []
							},
							"scale": 1.0,
							"variety": {
								"min_inclusive": 1,
								"max_inclusive": 2
							},
							"slow_noise": {
								"firstOctave": 1,
								"amplitudes": [
									1.2
								]
							},
							"slow_scale": 1.0,
							"states": [
								{
									"Name": "minecraft:stone"
								},
								{
									"Name": "minecraft:dirt"
								}
							]
						}
					}
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.blueIce("test_blue_ice")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:blue_ice",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.bonusChest("test_bonus_chest")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:bonus_chest",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.chorusPlant("test_chorus_plant")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:chorus_plant",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.coralClaw("test_coral_claw")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:coral_claw",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.coralMushroom("test_coral_mushroom")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:coral_mushroom",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.coralTree("test_coral_tree")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:coral_tree",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("test_dark_oak_tree") {
		ignoreVines = true
		minimumSize = threeLayersFeatureSize {
			limit = 1
			upperLimit = 1
			lowerSize = 0
			middleSize = 1
			upperSize = 2
		}
		trunkProvider = simpleStateProvider(blockState(Blocks.DARK_OAK_LOG, "axis" to "y"))
		foliageProvider = simpleStateProvider(
			blockState(
				Blocks.DARK_OAK_LEAVES,
				"distance" to "7",
				"persistent" to "false",
				"waterlogged" to "false"
			)
		)
		darkOakTrunkPlacer(baseHeight = 6, heightRandA = 2, heightRandB = 1)
		darkOakFoliagePlacer()
		belowTrunkProvider = ruleBasedStateProvider {
			rule {
				ifTrue {
					not {
						matchingBlockTag(tag = Tags.Block.CANNOT_REPLACE_BELOW_TREE_TRUNK)
					}
				}
				then(simpleStateProvider(Blocks.DIRT))
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"config": {
				"ignore_vines": true,
				"below_trunk_provider": {
					"type": "minecraft:rule_based_state_provider",
					"rules": [
						{
							"if_true": {
								"type": "minecraft:not",
								"predicate": {
									"type": "minecraft:matching_block_tag",
									"tag": "minecraft:cannot_replace_below_tree_trunk"
								}
							},
							"then": {
								"type": "minecraft:simple_state_provider",
								"state": {
									"Name": "minecraft:dirt"
								}
							}
						}
					]
				},
				"minimum_size": {
					"type": "minecraft:three_layers_feature_size",
					"limit": 1,
					"upper_limit": 1,
					"lower_size": 0,
					"middle_size": 1,
					"upper_size": 2
				},
				"trunk_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:dark_oak_log",
						"Properties": {
							"axis": "y"
						}
					}
				},
				"foliage_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:dark_oak_leaves",
						"Properties": {
							"distance": "7",
							"persistent": "false",
							"waterlogged": "false"
						}
					}
				},
				"trunk_placer": {
					"type": "minecraft:dark_oak_trunk_placer",
					"base_height": 6,
					"height_rand_a": 2,
					"height_rand_b": 1
				},
				"foliage_placer": {
					"type": "minecraft:dark_oak_foliage_placer",
					"radius": 0,
					"offset": 0
				},
				"decorators": []
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.deltaFeature("test_delta_feature", size = 3, rimSize = 1)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:delta_feature",
			"config": {
				"content": {
					"Name": "minecraft:stone"
				},
				"rim": {
					"Name": "minecraft:stone"
				},
				"size": 3,
				"rim_size": 1
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.desertWell("test_desert_well")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:desert_well",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.disk(
		"test_disk",
		stateProvider = simpleStateProvider(Blocks.GRAVEL),
		target = Solid,
		radius = 3,
		halfHeight = 1
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:disk",
			"config": {
				"state_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:gravel"
					}
				},
				"target": {
					"type": "minecraft:solid"
				},
				"radius": 3,
				"half_height": 1
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.speleothemCluster("test_speleothem_cluster") {
		floorToCeilingSearchRange = 30
		height = constant(12)
		radius = constant(4)
		maxStalagmiteStalactiteHeightDiff = 3
		heightDeviation = 2
		speleothemBlockLayerThickness = constant(1)
		density = constant(2)
		wetness = constant(1)
		chanceOfSpeleothemAtMaxDistanceFromCenter = 2
		maxDistanceFromEdgeAffectingChanceOfSpeleothem = 4
		maxDistanceFromCenterAffectingHeightBias = 6
		baseBlock = blockState(Blocks.DRIPSTONE_BLOCK)
		pointedBlock = blockState(Blocks.POINTED_DRIPSTONE)
		replaceableBlocks = listOf(Blocks.STONE, Blocks.DIRT)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:speleothem_cluster",
			"config": {
				"floor_to_ceiling_search_range": 30,
				"height": 12,
				"radius": 4,
				"max_stalagmite_stalactite_height_diff": 3,
				"height_deviation": 2,
				"speleothem_block_layer_thickness": 1,
				"density": 2,
				"wetness": 1,
				"chance_of_speleothem_at_max_distance_from_center": 2,
				"max_distance_from_edge_affecting_chance_of_speleothem": 4,
				"max_distance_from_center_affecting_height_bias": 6,
				"base_block": {
					"Name": "minecraft:dripstone_block"
				},
				"pointed_block": {
					"Name": "minecraft:pointed_dripstone"
				},
				"replaceable_blocks": [
					"minecraft:stone",
					"minecraft:dirt"
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.endIsland("test_end_island")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:end_island",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.endSpike("test_end_spike") {
		crystalInvulnerable = true
		spikes += EndSpikeEntry(radius = 5, height = 60, guarded = false)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:end_spike",
			"config": {
				"crystal_invulnerable": true,
				"crystal_beam_target": [
					0,
					0,
					0
				],
				"spikes": [
					{
						"radius": 5,
						"height": 60,
						"guarded": false
					}
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.fillLayer("test_fill_layer", height = 32)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:fill_layer",
			"config": {
				"state": {
					"Name": "minecraft:stone"
				},
				"height": 32
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.fossil(
		"test_fossil", maxEmptyCornersAllowed = 4,
		fossilStructures = listOf(StructureArgument("fossil/spine_1")),
		overlayStructures = listOf(StructureArgument("fossil/overlay/coal_0")),
		fossilProcessors = ProcessorLists.FOSSIL_COAL,
		overlayProcessors = ProcessorLists.FOSSIL_ROT,
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:fossil",
			"config": {
				"max_empty_corners_allowed": 4,
				"fossil_structures": [
					"minecraft:fossil/spine_1"
				],
				"overlay_structures": [
					"minecraft:fossil/overlay/coal_0"
				],
				"fossil_processors": "minecraft:fossil_coal",
				"overlay_processors": "minecraft:fossil_rot"
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.freezeTopLayer("test_freeze_top_layer")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:freeze_top_layer",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.geode("test_geode") {
		layers {
			filling = 1.0
			innerLayer = 2.0
			middleLayer = 3.0
			outerLayer = 4.0
		}
		crack {
			generateCrackChance = 0.5
			baseCrackSize = 2.0
			crackPointOffset = 2.0
		}
		invalidBlocksTreshold = 1
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:geode",
			"config": {
				"blocks": {
					"filling": {
						"type": "minecraft:simple_state_provider",
						"state": {
							"Name": "minecraft:stone"
						}
					},
					"inner_layer": {
						"type": "minecraft:simple_state_provider",
						"state": {
							"Name": "minecraft:stone"
						}
					},
					"alternate_inner_layer": {
						"type": "minecraft:simple_state_provider",
						"state": {
							"Name": "minecraft:stone"
						}
					},
					"middle_layer": {
						"type": "minecraft:simple_state_provider",
						"state": {
							"Name": "minecraft:stone"
						}
					},
					"outer_layer": {
						"type": "minecraft:simple_state_provider",
						"state": {
							"Name": "minecraft:stone"
						}
					},
					"inner_placements": [],
					"cannot_replace": "#minecraft:features_cannot_replace",
					"invalid_blocks": "#minecraft:geode_invalid_blocks"
				},
				"layers": {
					"filling": 1.0,
					"inner_layer": 2.0,
					"middle_layer": 3.0,
					"outer_layer": 4.0
				},
				"crack": {
					"generate_crack_chance": 0.5,
					"base_crack_size": 2.0,
					"crack_point_offset": 2.0
				},
				"invalid_blocks_treshold": 1
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.glowstoneBlob("test_glowstone_blob")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:glowstone_blob",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.hugeBrownMushroom("test_huge_brown_mushroom", canPlaceOn = Solid)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:huge_brown_mushroom",
			"config": {
				"can_place_on": {
					"type": "minecraft:solid"
				},
				"cap_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"stem_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				}
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.hugeFungus("test_huge_fungus") {
		planted = true
		hatState = simpleStateProvider(Blocks.DIRT)
		stemState = simpleStateProvider(Blocks.STONE)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:huge_fungus",
			"config": {
				"hat_state": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:dirt"
					}
				},
				"decor_state": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"stem_state": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"valid_base_block": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"replaceable_blocks": {
					"type": "minecraft:true"
				},
				"planted": true
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.hugeRedMushroom("test_huge_red_mushroom", canPlaceOn = Solid)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:huge_red_mushroom",
			"config": {
				"can_place_on": {
					"type": "minecraft:solid"
				},
				"cap_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"stem_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				}
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.iceberg("test_iceberg", state = blockState(Blocks.DIRT))

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:iceberg",
			"config": {
				"state": {
					"Name": "minecraft:dirt"
				}
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.kelp("test_kelp")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:kelp",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.lake(
		"test_lake", fluid = blockState(Blocks.STONE),
		barrier = blockState(Blocks.GRAVEL),
		canPlaceFeature = matchingBlocks(block = Blocks.STONE),
		canReplaceWithAirOrFluid = Solid,
		canReplaceWithBarrier = matchingFluids(fluids = listOf(Fluids.WATER)),
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:lake",
			"config": {
				"fluid": {
					"Name": "minecraft:stone"
				},
				"barrier": {
					"Name": "minecraft:gravel"
				},
				"can_place_feature": {
					"type": "minecraft:matching_blocks",
					"blocks": "minecraft:stone"
				},
				"can_replace_with_air_or_fluid": {
					"type": "minecraft:solid"
				},
				"can_replace_with_barrier": {
					"type": "minecraft:matching_fluids",
					"fluids": "minecraft:water"
				}
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.largeDripstone("test_large_dripstone") {
		floorToCeilingSearchRange = 30
		columnRadius = constant(3)
		heightScale = constantFloat(2.0f)
		maxColumnRadiusToCaveHeightRatio = 0.5f
		stalactiteBluntness = constantFloat(0.5f)
		stalagmiteBluntness = constantFloat(0.5f)
		windSpeed = constantFloat(0.0f)
		minRadiusForWind = 3
		minBluntnessForWind = 0.5f
		replaceableBlocks = listOf(Blocks.STONE, Blocks.DIRT)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:large_dripstone",
			"config": {
				"floor_to_ceiling_search_range": 30,
				"column_radius": 3,
				"height_scale": 2.0,
				"max_column_radius_to_cave_height_ratio": 0.5,
				"stalactite_bluntness": 0.5,
				"stalagmite_bluntness": 0.5,
				"wind_speed": 0.0,
				"min_radius_for_wind": 3,
				"min_bluntness_for_wind": 0.5,
				"replaceable_blocks": [
					"minecraft:stone",
					"minecraft:dirt"
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.monsterRoom("test_monster_room")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:monster_room",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.multifaceGrowth("test_multiface_growth", Blocks.STONE) {
		searchRange = 10
		chanceOfSpreading = 0.5
		canPlaceOnFloor = true
		canPlaceOnCeiling = true
		canPlaceOnWall = true
		canBePlacedOn = listOf(Blocks.STONE, Blocks.DIRT)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:multiface_growth",
			"config": {
				"block": "minecraft:stone",
				"search_range": 10,
				"chance_of_spreading": 0.5,
				"can_place_on_floor": true,
				"can_place_on_ceiling": true,
				"can_place_on_wall": true,
				"can_be_placed_on": [
					"minecraft:stone",
					"minecraft:dirt"
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.netherForestVegetation(
		"test_nether_forest_vegetation",
		stateProvider = simpleStateProvider(Blocks.STONE),
		spreadWidth = 8,
		spreadHeight = 4
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:nether_forest_vegetation",
			"config": {
				"state_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"spread_width": 8,
				"spread_height": 4
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.netherrackReplaceBlobs(
		"test_netherrack_replace_blobs",
		blockState(Blocks.GRAVEL),
		blockStateStone(),
		constant(3)
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:netherrack_replace_blobs",
			"config": {
				"state": {
					"Name": "minecraft:gravel"
				},
				"target": {
					"Name": "minecraft:stone"
				},
				"radius": 3
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.noOp("test_no_op")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:no_op",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.ore("test_ore") {
		targets {
			target(state = blockState(Blocks.STONE), target = randomBlockMatch(Blocks.STONE))
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:ore",
			"config": {
				"size": 0,
				"discard_chance_on_air_exposure": 0.0,
				"targets": [
					{
						"target": {
							"predicate_type": "minecraft:random_block_match",
							"block": "minecraft:stone",
							"probability": 0.0
						},
						"state": {
							"Name": "minecraft:stone"
						}
					}
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.speleothem(
		"test_speleothem", chanceOfTallerGeneration = 0.5,
		chanceOfDirectionalSpread = 0.5,
		chanceOfSpreadRadius2 = 0.5,
		chanceOfSpreadRadius3 = 0.5,
	) {
		baseBlock = blockState(Blocks.DRIPSTONE_BLOCK)
		pointedBlock = blockState(Blocks.POINTED_DRIPSTONE)
		replaceableBlocks = listOf(Blocks.STONE, Blocks.DIRT)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:speleothem",
			"config": {
				"chance_of_taller_generation": 0.5,
				"chance_of_directional_spread": 0.5,
				"chance_of_spread_radius2": 0.5,
				"chance_of_spread_radius3": 0.5,
				"base_block": {
					"Name": "minecraft:dripstone_block"
				},
				"pointed_block": {
					"Name": "minecraft:pointed_dripstone"
				},
				"replaceable_blocks": [
					"minecraft:stone",
					"minecraft:dirt"
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.randomBooleanSelector(
		"test_random_boolean_selector",
		featureFalse = PlacedFeatures.FOSSIL_LOWER,
		featureTrue = PlacedFeatures.FOSSIL_UPPER
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:random_boolean_selector",
			"config": {
				"feature_false": "minecraft:fossil_lower",
				"feature_true": "minecraft:fossil_upper"
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.randomSelector("test_random_selector", default = PlacedFeatures.ACACIA) {
		feature(PlacedFeatures.ACACIA_CHECKED, 0.5f)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:random_selector",
			"config": {
				"features": [
					{
						"chance": 0.5,
						"feature": "minecraft:acacia_checked"
					}
				],
				"default": "minecraft:acacia"
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.replaceSingleBlock(
		"test_replace_single_block",
		target(randomBlockMatch(Blocks.STONE), blockState(Blocks.STONE))
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:replace_single_block",
			"config": {
				"targets": [
					{
						"target": {
							"predicate_type": "minecraft:random_block_match",
							"block": "minecraft:stone",
							"probability": 0.0
						},
						"state": {
							"Name": "minecraft:stone"
						}
					}
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.rootSystem("test_root_system", feature = PlacedFeatures.ACACIA) {
		requiredVerticalSpaceForTree = 2
		rootRadius = 3
		rootReplaceable = listOf(Blocks.DIRT)
		rootPlacementAttempts = 6
		rootColumnMaxHeight = 3
		hangingRootRadius = 2
		hangingRootsVerticalSpan = 2
		hangingRootPlacementAttempts = 4
		allowedVerticalWaterForTree = 0
		rootStateProvider = simpleStateProvider(Blocks.DIRT)
		hangingRootStateProvider = simpleStateProvider(Blocks.DIRT)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:root_system",
			"config": {
				"required_vertical_space_for_tree": 2,
				"root_radius": 3,
				"root_replaceable": "minecraft:dirt",
				"root_placement_attempts": 6,
				"root_column_max_height": 3,
				"hanging_root_radius": 2,
				"hanging_roots_vertical_span": 2,
				"hanging_root_placement_attempts": 4,
				"allowed_vertical_water_for_tree": 0,
				"root_state_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:dirt"
					}
				},
				"hanging_root_state_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:dirt"
					}
				},
				"allowed_tree_position": {
					"type": "minecraft:true"
				},
				"feature": "minecraft:acacia"
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.simpleBlock("test_rule_based_provider_list_block", ruleBasedStateProvider {
		fallback = simpleStateProvider(Blocks.STONE)
		rule(
			ifTrue = Solid,
			then = simpleStateProvider(Blocks.GRAVEL),
		)
		rule(then = simpleStateProvider(Blocks.SAND)) {
			solid()
		}
	})

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:simple_block",
			"config": {
				"to_place": {
					"type": "minecraft:rule_based_state_provider",
					"fallback": {
						"type": "minecraft:simple_state_provider",
						"state": {
							"Name": "minecraft:stone"
						}
					},
					"rules": [
						{
							"if_true": {
								"type": "minecraft:solid"
							},
							"then": {
								"type": "minecraft:simple_state_provider",
								"state": {
									"Name": "minecraft:gravel"
								}
							}
						},
						{
							"if_true": {
								"type": "minecraft:solid"
							},
							"then": {
								"type": "minecraft:simple_state_provider",
								"state": {
									"Name": "minecraft:sand"
								}
							}
						}
					]
				},
				"schedule_tick": false
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.simpleBlock("test_rule_based_provider_unified", ruleBasedStateProvider {
		fallback = simpleStateProvider(Blocks.STONE)
		rule {
			ifTrue {
				solid()
			}
			then(simpleStateProvider(Blocks.DIRT))
		}
		rule(
			ifTrue = hasSturdyFace(direction = Direction.DOWN),
			then = simpleStateProvider(Blocks.GRAVEL),
		)
		rule(then = simpleStateProvider(Blocks.SAND)) {
			not {
				matchingBlocks(block = Blocks.STONE)
			}
		}
	})

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:simple_block",
			"config": {
				"to_place": {
					"type": "minecraft:rule_based_state_provider",
					"fallback": {
						"type": "minecraft:simple_state_provider",
						"state": {
							"Name": "minecraft:stone"
						}
					},
					"rules": [
						{
							"if_true": {
								"type": "minecraft:solid"
							},
							"then": {
								"type": "minecraft:simple_state_provider",
								"state": {
									"Name": "minecraft:dirt"
								}
							}
						},
						{
							"if_true": {
								"type": "minecraft:has_sturdy_face",
								"direction": "down"
							},
							"then": {
								"type": "minecraft:simple_state_provider",
								"state": {
									"Name": "minecraft:gravel"
								}
							}
						},
						{
							"if_true": {
								"type": "minecraft:not",
								"predicate": {
									"type": "minecraft:matching_blocks",
									"blocks": "minecraft:stone"
								}
							},
							"then": {
								"type": "minecraft:simple_state_provider",
								"state": {
									"Name": "minecraft:sand"
								}
							}
						}
					]
				},
				"schedule_tick": false
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.scatteredOre(
		"test_scattered_ore",
		5,
		0.5,
		target(randomBlockMatch(Blocks.STONE), blockState(Blocks.STONE))
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:scattered_ore",
			"config": {
				"size": 5,
				"discard_chance_on_air_exposure": 0.5,
				"targets": [
					{
						"target": {
							"predicate_type": "minecraft:random_block_match",
							"block": "minecraft:stone",
							"probability": 0.0
						},
						"state": {
							"Name": "minecraft:stone"
						}
					}
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.sculkPatch("test_sculk_patch") {
		chargeCount = 10
		amountPerCharge = 2
		spreadAttempts = 45
		growthRounds = 1
		spreadRounds = 2
		extraRateGrowths = constant(0)
		catalystChance = 0.5
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:sculk_patch",
			"config": {
				"charge_count": 10,
				"amount_per_charge": 2,
				"spread_attempts": 45,
				"growth_rounds": 1,
				"spread_rounds": 2,
				"extra_rate_growths": 0,
				"catalyst_chance": 0.5
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.seaPickle("test_sea_pickle", count = constant(3))

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:sea_pickle",
			"config": {
				"count": 3
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.seagrass("test_seagrass", probability = 0.5)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:seagrass",
			"config": {
				"probability": 0.5
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.sequence("test_sequence", PlacedFeatures.ACACIA, PlacedFeatures.ACACIA_CHECKED)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"config": {
				"features": [
					"minecraft:acacia",
					"minecraft:acacia_checked"
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.simpleBlock("test_simple_block", simpleStateProvider(Blocks.STONE), scheduleTick = true)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:simple_block",
			"config": {
				"to_place": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"schedule_tick": true
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.simpleRandomSelector(
		"test_simple_random_selector",
		PlacedFeatures.ACACIA,
		PlacedFeatures.ACACIA_CHECKED
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:simple_random_selector",
			"config": {
				"features": [
					"minecraft:acacia",
					"minecraft:acacia_checked"
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.spike("test_spike", canPlaceOn = Solid, canReplace = Solid, state = blockStateStone())

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:spike",
			"config": {
				"can_place_on": {
					"type": "minecraft:solid"
				},
				"can_replace": {
					"type": "minecraft:solid"
				},
				"state": {
					"Name": "minecraft:stone"
				}
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.springFeature("test_spring_feature") {
		state = blockState(Blocks.STONE)
		rockCount = 4
		holeCount = 1
		requiresBlockBelow = true
		validBlocks = listOf(Blocks.STONE, Blocks.GRAVEL)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:spring_feature",
			"config": {
				"state": {
					"Name": "minecraft:stone"
				},
				"rock_count": 4,
				"hole_count": 1,
				"requires_block_below": true,
				"valid_blocks": [
					"minecraft:stone",
					"minecraft:gravel"
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.template(
		"test_template",
		structureTemplate(
			Structures.EndCity.BRIDGE_END,
			weight = 2,
			rotations = listOf(StructureRotation.CLOCKWISE_90)
		),
		structureTemplate(Structures.EndCity.BRIDGE_GENTLE_STAIRS),
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:template",
			"config": {
				"templates": [
					{
						"data": {
							"id": "minecraft:end_city/bridge_end",
							"rotations": [
								"clockwise_90"
							]
						},
						"weight": 2
					},
					{
						"data": {
							"id": "minecraft:end_city/bridge_gentle_stairs"
						},
						"weight": 1
					}
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("test_tree") {
		minimumSize = threeLayersFeatureSize {
			limit = 5
			upperLimit = 2
			lowerSize = 1
			middleSize = 2
			upperSize = 3
		}

		cherryTrunkPlacer {
			branchCount = constant(2)
			branchHorizontalLength = constant(6)
			branchStartOffsetFromTop = uniform(-10, -5)
		}

		cherryFoliagePlacer {
			height = constant(5)
		}

		decorators {
			attachedToLeaves {
				blockProvider = simpleStateProvider(Blocks.DIRT)
				directions = listOf(Direction.DOWN)
				requiredEmptyBlocks = 3
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"config": {
				"below_trunk_provider": {
					"type": "minecraft:rule_based_state_provider",
					"rules": []
				},
				"minimum_size": {
					"type": "minecraft:three_layers_feature_size",
					"limit": 5,
					"upper_limit": 2,
					"lower_size": 1,
					"middle_size": 2,
					"upper_size": 3
				},
				"trunk_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"foliage_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"trunk_placer": {
					"type": "minecraft:cherry_trunk_placer",
					"base_height": 0,
					"height_rand_a": 0,
					"height_rand_b": 0,
					"branch_count": 2,
					"branch_horizontal_length": 6,
					"branch_start_offset_from_top": {
						"min_inclusive": -10,
						"max_inclusive": -5
					},
					"branch_end_offset_from_top": 0
				},
				"foliage_placer": {
					"type": "minecraft:cherry_foliage_placer",
					"radius": 0,
					"offset": 0,
					"height": 5,
					"wide_bottom_layer_hole_chance": 0.0,
					"corner_hole_chance": 0.0,
					"hanging_leaves_chance": 0.0,
					"hanging_leaves_extension_chance": 0.0
				},
				"decorators": [
					{
						"type": "minecraft:attached_to_leaves",
						"probability": 0.0,
						"exclusion_radius_xz": 0,
						"exclusion_radius_y": 0,
						"required_empty_blocks": 3,
						"block_provider": {
							"type": "minecraft:simple_state_provider",
							"state": {
								"Name": "minecraft:dirt"
							}
						},
						"directions": [
							"down"
						]
					}
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("test_tree_alter_ground") {
		cherryTrunkPlacer {
			branchCount = constant(1)
			branchHorizontalLength = constant(2)
			branchStartOffsetFromTop = uniform(-3, -1)
		}
		cherryFoliagePlacer { height = constant(2) }
		decorators {
			alterGround {
				fallback = simpleStateProvider(Blocks.DIRT)
				rule {
					ifTrue { hasSturdyFace(direction = Direction.DOWN) }
					then(simpleStateProvider(Blocks.GRASS_BLOCK))
				}
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"config": {
				"below_trunk_provider": {
					"type": "minecraft:rule_based_state_provider",
					"rules": []
				},
				"minimum_size": {
					"type": "minecraft:two_layers_feature_size"
				},
				"trunk_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"foliage_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"trunk_placer": {
					"type": "minecraft:cherry_trunk_placer",
					"base_height": 0,
					"height_rand_a": 0,
					"height_rand_b": 0,
					"branch_count": 1,
					"branch_horizontal_length": 2,
					"branch_start_offset_from_top": {
						"min_inclusive": -3,
						"max_inclusive": -1
					},
					"branch_end_offset_from_top": 0
				},
				"foliage_placer": {
					"type": "minecraft:cherry_foliage_placer",
					"radius": 0,
					"offset": 0,
					"height": 2,
					"wide_bottom_layer_hole_chance": 0.0,
					"corner_hole_chance": 0.0,
					"hanging_leaves_chance": 0.0,
					"hanging_leaves_extension_chance": 0.0
				},
				"decorators": [
					{
						"type": "minecraft:alter_ground",
						"provider": {
							"type": "minecraft:rule_based_state_provider",
							"fallback": {
								"type": "minecraft:simple_state_provider",
								"state": {
									"Name": "minecraft:dirt"
								}
							},
							"rules": [
								{
									"if_true": {
										"type": "minecraft:has_sturdy_face",
										"direction": "down"
									},
									"then": {
										"type": "minecraft:simple_state_provider",
										"state": {
											"Name": "minecraft:grass_block"
										}
									}
								}
							]
						}
					}
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("test_tree_below_trunk") {
		cherryTrunkPlacer {
			branchCount = constant(1)
			branchHorizontalLength = constant(2)
			branchStartOffsetFromTop = uniform(-5, -2)
		}
		cherryFoliagePlacer { height = constant(3) }
		belowTrunkProvider = ruleBasedStateProvider {
			fallback = simpleStateProvider(Blocks.DIRT)
			rule {
				ifTrue {
					hasSturdyFace(direction = Direction.DOWN)
				}
				then(simpleStateProvider(Blocks.GRASS_BLOCK))
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"config": {
				"below_trunk_provider": {
					"type": "minecraft:rule_based_state_provider",
					"fallback": {
						"type": "minecraft:simple_state_provider",
						"state": {
							"Name": "minecraft:dirt"
						}
					},
					"rules": [
						{
							"if_true": {
								"type": "minecraft:has_sturdy_face",
								"direction": "down"
							},
							"then": {
								"type": "minecraft:simple_state_provider",
								"state": {
									"Name": "minecraft:grass_block"
								}
							}
						}
					]
				},
				"minimum_size": {
					"type": "minecraft:two_layers_feature_size"
				},
				"trunk_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"foliage_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"trunk_placer": {
					"type": "minecraft:cherry_trunk_placer",
					"base_height": 0,
					"height_rand_a": 0,
					"height_rand_b": 0,
					"branch_count": 1,
					"branch_horizontal_length": 2,
					"branch_start_offset_from_top": {
						"min_inclusive": -5,
						"max_inclusive": -2
					},
					"branch_end_offset_from_top": 0
				},
				"foliage_placer": {
					"type": "minecraft:cherry_foliage_placer",
					"radius": 0,
					"offset": 0,
					"height": 3,
					"wide_bottom_layer_hole_chance": 0.0,
					"corner_hole_chance": 0.0,
					"hanging_leaves_chance": 0.0,
					"hanging_leaves_extension_chance": 0.0
				},
				"decorators": []
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.endGateway("test_triple_as_array", exact = true)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:end_gateway",
			"config": {
				"exact": true
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.twistingVines("test_twisting_vines", spreadWidth = 8, spreadHeight = 4, maxHeight = 16)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:twisting_vines",
			"config": {
				"spread_width": 8,
				"spread_height": 4,
				"max_height": 16
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.underwaterMagma(
		"test_underwater_magma", floorSearchRange = 3,
		placementRadiusAroundFloor = 1,
		placementProbabilityPerValidPosition = 0.5
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:underwater_magma",
			"config": {
				"floor_search_range": 3,
				"placement_radius_around_floor": 1,
				"placement_probability_per_valid_position": 0.5
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.vegetationPatch(
		"test_vegetation_patch", surface = Surface.FLOOR,
		replaceable = listOf(Tags.Block.DIRT),
		vegetationFeature = PlacedFeatures.ACACIA
	) {
		depth = constant(3)
		verticalRange = 3
		vegetationChance = 0.5
		xzRadius = 2
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:vegetation_patch",
			"config": {
				"surface": "floor",
				"depth": 3,
				"vertical_range": 3,
				"extra_bottom_block_chance": 0.0,
				"extra_edge_column_chance": 0.0,
				"vegetation_chance": 0.5,
				"xz_radius": 2,
				"replaceable": "#minecraft:dirt",
				"ground_state": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"vegetation_feature": "minecraft:acacia"
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.vines("test_vines")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:vines",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.voidStartPlatform("test_void_start_platform")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:void_start_platform",
			"config": {}
		}
	""".trimIndent()

	configuredFeaturesBuilder.waterloggedVegetationPatch(
		"test_waterlogged_vegetation_patch", surface = Surface.CEILING,
		replaceable = listOf(Tags.Block.DIRT),
		vegetationFeature = PlacedFeatures.ACACIA
	) {
		depth = constant(2)
		verticalRange = 8
		vegetationChance = 0.5
		xzRadius = 3
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:waterlogged_vegetation_patch",
			"config": {
				"surface": "ceiling",
				"depth": 2,
				"vertical_range": 8,
				"extra_bottom_block_chance": 0.0,
				"extra_edge_column_chance": 0.0,
				"vegetation_chance": 0.5,
				"xz_radius": 3,
				"replaceable": "#minecraft:dirt",
				"ground_state": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
				},
				"vegetation_feature": "minecraft:acacia"
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.weepingVines("test_weeping_vines")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:weeping_vines",
			"config": {}
		}
	""".trimIndent()
}

class ConfiguredFeatureTests : FunSpec({
	test("configured feature") {
		dataPack("configuredFeature") {
			pretty()
			configuredFeatureTests()
		}
	}
})
