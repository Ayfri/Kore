package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.StructureRotation
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsIsJson
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.blockpredicate.*
import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.VerticalDirection
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.*
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.*
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.acaciaFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.blobFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.bushFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.cherryFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.darkOakFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.fancyFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.height
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.jungleFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.megaPineFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.pineFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.poplarFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.radius
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.randomSpreadFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.spruceFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize.threeLayersFeatureSize
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize.twoLayersFeatureSize
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.alterGround
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.rootprovider.aboveRootPlacement
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.rootprovider.mangroveRootPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.rootprovider.mangroveRootPlacement
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.rootprovider.trunkOffsetY
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.attachedToLeaves
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.attachedToLogs
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.beehive
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.creakingHeart
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.directions
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.leaveVine
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.paleMoss
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.placeOnGround
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.shelfMushroom
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.trunkVine
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.bendingTrunkPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.branchCount
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.branchEndOffsetFromTop
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.branchHorizontalLength
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.cherryTrunkPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.darkOakTrunkPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.extraBranchLength
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.extraBranchSteps
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.poplarTrunkPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.trunkHeightAboveBranches
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.upwardsBranchingTrunkPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeaturesBuilder
import io.github.ayfri.kore.features.worldgen.configuredfeature.target
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.intproviders.uniform
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.Surface
import io.github.ayfri.kore.features.worldgen.placedfeature.feature
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.Offset
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.offset
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.randomlySelected
import io.github.ayfri.kore.features.worldgen.placedfeature.placed
import io.github.ayfri.kore.features.worldgen.processorlist.processorListOrInline
import io.github.ayfri.kore.features.worldgen.processorlist.types.blockRot
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier.appendLoot
import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.rule
import io.github.ayfri.kore.features.worldgen.processorlist.types.rules
import io.github.ayfri.kore.features.worldgen.ruletest.allOf
import io.github.ayfri.kore.features.worldgen.ruletest.alwaysTrue
import io.github.ayfri.kore.features.worldgen.ruletest.anyOf
import io.github.ayfri.kore.features.worldgen.ruletest.blockMatch
import io.github.ayfri.kore.features.worldgen.ruletest.heightMatch
import io.github.ayfri.kore.features.worldgen.ruletest.not
import io.github.ayfri.kore.features.worldgen.ruletest.randomBlockMatch
import io.github.ayfri.kore.features.worldgen.ruletest.tagMatch
import io.github.ayfri.kore.generated.*
import io.github.ayfri.kore.generated.arguments.worldgen.tagged.PlacedFeatureTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import io.github.ayfri.kore.utils.pretty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import io.github.ayfri.kore.features.worldgen.floatproviders.clampedNormal
import io.github.ayfri.kore.features.worldgen.floatproviders.uniform as uniformFloat

fun DataPack.configuredFeatureTests() {
	configuredFeaturesBuilder.endPlatform("end_platform")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:end_platform"
		}
	""".trimIndent()

	configuredFeaturesBuilder.bamboo("test_bamboo", probability = 0.5)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:bamboo",
			"probability": 0.5
		}
	""".trimIndent()

	configuredFeaturesBuilder.blockBlob("test_block_blob", state = blockStateStone()) {
		canPlaceOn { solid() }
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:block_blob",
			"can_place_on": {
				"type": "minecraft:solid"
			},
			"state": "minecraft:stone"
		}
	""".trimIndent()

	configuredFeaturesBuilder.blockPile("test_block_pile") {
		stateProvider = simpleStateProvider(Blocks.GRAVEL)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:block_pile",
			"state_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:gravel"
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.blockColumn("test_block_predicates_and_complex_provider", direction = Direction.DOWN) {
		allowedPlacement {
			not {
				matchingBlocks(Blocks.DIRT, Blocks.STONE)
			}

			solid()
		}

		layers {
			layer {
				provider = dualNoiseProvider {
					slowNoise(baseOctave = 1, 1.2)
					variety(1, 2)
					scale = 1.0
					slowScale = 1.0
					states(Blocks.STONE, Blocks.DIRT)
				}
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:block_column",
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
						"type": "minecraft:dual_noise",
						"seed": 0,
						"noise": {
							"base_octave": 0
						},
						"scale": 1.0,
						"variety": {
							"type": "minecraft:uniform",
							"min_inclusive": 1,
							"max_inclusive": 2
						},
						"slow_noise": {
							"amplitude_modifiers": [
								1.2
							],
							"base_octave": 1,
							"octave_count": 1
						},
						"slow_scale": 1.0,
						"states": [
							"minecraft:stone",
							"minecraft:dirt"
						]
					}
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.blueIce("test_blue_ice")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:blue_ice"
		}
	""".trimIndent()

	configuredFeaturesBuilder.bonusChest("test_bonus_chest")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:bonus_chest"
		}
	""".trimIndent()

	configuredFeaturesBuilder.chorusPlant("test_chorus_plant")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:chorus_plant"
		}
	""".trimIndent()

	configuredFeaturesBuilder.coralClaw("test_coral_claw", PlacedFeatureArgument("coral_overlay", "test"))

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:coral_claw",
			"feature": "test:coral_overlay"
		}
	""".trimIndent()

	configuredFeaturesBuilder.coralTree("test_coral_tree", PlacedFeatureArgument("coral_overlay", "test"))

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:coral_tree",
			"feature": "test:coral_overlay"
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
				then = simpleStateProvider(Blocks.DIRT)
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"ignore_vines": true,
			"below_trunk_provider": {
				"type": "minecraft:rule_based",
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
							"type": "minecraft:simple",
							"state": "minecraft:dirt"
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
				"type": "minecraft:simple",
				"state": {
					"id": "minecraft:dark_oak_log",
					"properties": {
						"axis": "y"
					}
				}
			},
			"foliage_provider": {
				"type": "minecraft:simple",
				"state": {
					"id": "minecraft:dark_oak_leaves",
					"properties": {
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
	""".trimIndent()

	configuredFeaturesBuilder.deltaFeature("test_delta_feature", size = 3, rimSize = 1)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:delta_feature",
			"contents": "minecraft:stone",
			"rim": "minecraft:stone",
			"size": 3,
			"rim_size": 1
		}
	""".trimIndent()

	configuredFeaturesBuilder.disk("test_disk", radius = 3, halfHeight = 1) {
		stateProvider = simpleStateProvider(Blocks.GRAVEL)
		target { solid() }
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:disk",
			"state_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:gravel"
			},
			"target": {
				"type": "minecraft:solid"
			},
			"radius": 3,
			"half_height": 1
		}
	""".trimIndent()

	configuredFeaturesBuilder.speleothemCluster("test_speleothem_cluster") {
		floorToCeilingSearchRange = 30
		height(12)
		radius(4)
		maxStalagmiteStalactiteHeightDiff = 3
		heightDeviation = 2
		speleothemBlockLayerThickness(1)
		density = uniformFloat(0.3f, 0.7f)
		wetness = clampedNormal(mean = 0.1f, deviation = 0.3f, min = 0.1f, max = 0.9f)
		chanceOfSpeleothemAtMaxDistanceFromCenter = 0.1f
		maxDistanceFromEdgeAffectingChanceOfSpeleothem = 4
		maxDistanceFromCenterAffectingHeightBias = 6
		baseBlock = blockState(Blocks.DRIPSTONE_BLOCK)
		pointedBlock = blockState(Blocks.POINTED_DRIPSTONE)
		replaceableBlocks = listOf(Blocks.STONE, Blocks.DIRT)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:speleothem_cluster",
			"floor_to_ceiling_search_range": 30,
			"height": 12,
			"radius": 4,
			"max_stalagmite_stalactite_height_diff": 3,
			"height_deviation": 2,
			"speleothem_block_layer_thickness": 1,
			"density": {
				"type": "minecraft:uniform",
				"min_inclusive": 0.3,
				"max_exclusive": 0.7
			},
			"wetness": {
				"type": "minecraft:clamped_normal",
				"mean": 0.1,
				"deviation": 0.3,
				"min": 0.1,
				"max": 0.9
			},
			"chance_of_speleothem_at_max_distance_from_center": 0.1,
			"max_distance_from_edge_affecting_chance_of_speleothem": 4,
			"max_distance_from_center_affecting_height_bias": 6,
			"base_block": "minecraft:dripstone_block",
			"pointed_block": "minecraft:pointed_dripstone",
			"replaceable_blocks": [
				"minecraft:stone",
				"minecraft:dirt"
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.endIsland("test_end_island")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:end_island"
		}
	""".trimIndent()

	configuredFeaturesBuilder.endSpike("test_end_spike_default")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:end_spike",
			"spikes": []
		}
	""".trimIndent()

	configuredFeaturesBuilder.endSpike("test_end_spike") {
		crystalInvulnerable = true
		crystalBeamTarget = Triple(0, 80, 0)
		spike {
			centerX = 12
			centerZ = -34
			radius = 5
			height = 60
			guarded = false
		}
		spike {
			radius = 3
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:end_spike",
			"crystal_beam_target": [
				0,
				80,
				0
			],
			"crystal_invulnerable": true,
			"spikes": [
				{
					"centerX": 12,
					"centerZ": -34,
					"guarded": false,
					"height": 60,
					"radius": 5
				},
				{
					"radius": 3
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.fillLayer("test_fill_layer", height = 32)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:fill_layer",
			"state": "minecraft:stone",
			"height": 32
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
	""".trimIndent()

	configuredFeaturesBuilder.fossil(
		"test_fossil_inline",
		fossilStructures = listOf(StructureArgument("fossil/spine_1")),
		overlayStructures = listOf(StructureArgument("fossil/overlay/coal_0")),
		fossilProcessors = processorListOrInline { blockRot(0.9) },
		overlayProcessors = processorListOrInline(ProcessorLists.FOSSIL_ROT),
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:fossil",
			"max_empty_corners_allowed": 0,
			"fossil_structures": [
				"minecraft:fossil/spine_1"
			],
			"overlay_structures": [
				"minecraft:fossil/overlay/coal_0"
			],
			"fossil_processors": {
				"processors": [
					{
						"processor_type": "minecraft:block_rot",
						"integrity": 0.9
					}
				]
			},
			"overlay_processors": "minecraft:fossil_rot"
		}
	""".trimIndent()

	configuredFeaturesBuilder.freezeTopLayer("test_freeze_top_layer")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:freeze_top_layer"
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
			crackPointOffset = 2
		}
		invalidBlocksThreshold = 1
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:geode",
			"blocks": {
				"filling": {
					"type": "minecraft:simple",
					"state": "minecraft:stone"
				},
				"inner_layer": {
					"type": "minecraft:simple",
					"state": "minecraft:stone"
				},
				"alternate_inner_layer": {
					"type": "minecraft:simple",
					"state": "minecraft:stone"
				},
				"middle_layer": {
					"type": "minecraft:simple",
					"state": "minecraft:stone"
				},
				"outer_layer": {
					"type": "minecraft:simple",
					"state": "minecraft:stone"
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
				"crack_point_offset": 2
			},
			"invalid_blocks_threshold": 1
		}
	""".trimIndent()

	configuredFeaturesBuilder.hugeBrownMushroom("test_huge_brown_mushroom") {
		canPlaceOn { solid() }
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:huge_brown_mushroom",
			"can_place_on": {
				"type": "minecraft:solid"
			},
			"cap_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"stem_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.hugeFungus("test_huge_fungus") {
		planted = true
		hatState = blockState(Blocks.DIRT)
		stemState = blockState(Blocks.CRIMSON_STEM, "axis" to "y")
		validBaseBlock = blockState(Blocks.CRIMSON_NYLIUM)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:huge_fungus",
			"hat_state": "minecraft:dirt",
			"decor_state": "minecraft:stone",
			"stem_state": {
				"id": "minecraft:crimson_stem",
				"properties": {
					"axis": "y"
				}
			},
			"valid_base_block": "minecraft:crimson_nylium",
			"replaceable_blocks": {
				"type": "minecraft:true"
			},
			"planted": true
		}
	""".trimIndent()

	configuredFeaturesBuilder.hugeRedMushroom("test_huge_red_mushroom") {
		canPlaceOn { solid() }
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:huge_red_mushroom",
			"can_place_on": {
				"type": "minecraft:solid"
			},
			"cap_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"stem_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.iceberg("test_iceberg", state = blockState(Blocks.DIRT))

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:iceberg",
			"state": "minecraft:dirt"
		}
	""".trimIndent()

	configuredFeaturesBuilder.lake(
		"test_lake",
		fluid = blockState(Blocks.STONE),
		barrier = blockState(Blocks.GRAVEL),
	) {
		canPlaceFeature { matchingBlocks(Blocks.STONE) }
		canReplaceWithAirOrFluid { solid() }
		canReplaceWithBarrier { matchingFluids(Fluids.WATER) }
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:lake",
			"fluid": "minecraft:stone",
			"barrier": "minecraft:gravel",
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
	""".trimIndent()

	configuredFeaturesBuilder.blockColumn("test_block_column_fixed_layers", direction = Direction.UP) {
		layers {
			layer(3) { provider = simpleStateProvider(Blocks.STONE) }
			layer(1) { provider = simpleStateProvider(Blocks.DIRT) }
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:block_column",
			"direction": "up",
			"allowed_placement": {
				"type": "minecraft:true"
			},
			"prioritize_tip": false,
			"layers": [
				{
					"height": 3,
					"provider": {
						"type": "minecraft:simple",
						"state": "minecraft:stone"
					}
				},
				{
					"height": 1,
					"provider": {
						"type": "minecraft:simple",
						"state": "minecraft:dirt"
					}
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.largeDripstone("test_large_dripstone") {
		floorToCeilingSearchRange = 30
		columnRadius(3)
		heightScale(2f)
		maxColumnRadiusToCaveHeightRatio = 0.5f
		stalactiteBluntness(0.5f)
		stalagmiteBluntness(0.5f)
		windSpeed(0f)
		minRadiusForWind = 3
		minBluntnessForWind = 0.5f
		replaceableBlocks = listOf(Blocks.STONE, Blocks.DIRT)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:large_dripstone",
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
	""".trimIndent()

	configuredFeaturesBuilder.monsterRoom("test_monster_room")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:monster_room"
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
	""".trimIndent()

	configuredFeatures {
		netherrackReplaceBlobs(
			"test_netherrack_replace_blobs",
			blockState(Blocks.GRAVEL),
			blockStateStone(),
			radius = 3
		)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:netherrack_replace_blobs",
			"state": "minecraft:gravel",
			"target": "minecraft:stone",
			"radius": 3
		}
	""".trimIndent()

	configuredFeaturesBuilder.noOp("test_no_op")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:no_op"
		}
	""".trimIndent()

	configuredFeaturesBuilder.ore("test_ore") {
		targets {
			target(blockState(Blocks.STONE)) {
				target = randomBlockMatch(Blocks.STONE, 0.5)
			}

			target(blockState(Blocks.DEEPSLATE)) {
				target = tagMatch(Tags.Block.DEEPSLATE_ORE_REPLACEABLES)
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:ore",
			"size": 0,
			"discard_chance_on_air_exposure": 0.0,
			"targets": [
				{
					"target": {
						"predicate_type": "minecraft:random_block_match",
						"block": "minecraft:stone",
						"probability": 0.5
					},
					"state": "minecraft:stone"
				},
				{
					"target": {
						"predicate_type": "minecraft:tag_match",
						"tag": "minecraft:deepslate_ore_replaceables"
					},
					"state": "minecraft:deepslate"
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.ore("test_ore_height", size = 9) {
		targets {
			target(blockState(Blocks.IRON_ORE)) {
				target = allOf(tagMatch(Tags.Block.STONE_ORE_REPLACEABLES), heightMatch(minInclusive = 0, maxInclusive = 2031))
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:ore",
			"size": 9,
			"discard_chance_on_air_exposure": 0.0,
			"targets": [
				{
					"target": {
						"predicate_type": "minecraft:all_of",
						"rules": [
							{
								"predicate_type": "minecraft:tag_match",
								"tag": "minecraft:stone_ore_replaceables"
							},
							{
								"predicate_type": "minecraft:height_match",
								"max_inclusive": 2031,
								"min_inclusive": 0
							}
						]
					},
					"state": "minecraft:iron_ore"
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.ore("test_ore_any_of", size = 9) {
		targets {
			target(blockState(Blocks.IRON_ORE)) {
				target = anyOf(
					tagMatch(Tags.Block.STONE_ORE_REPLACEABLES),
					tagMatch(Tags.Block.DEEPSLATE_ORE_REPLACEABLES)
				)
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:ore",
			"size": 9,
			"discard_chance_on_air_exposure": 0.0,
			"targets": [
				{
					"target": {
						"predicate_type": "minecraft:any_of",
						"rules": [
							{
								"predicate_type": "minecraft:tag_match",
								"tag": "minecraft:stone_ore_replaceables"
							},
							{
								"predicate_type": "minecraft:tag_match",
								"tag": "minecraft:deepslate_ore_replaceables"
							}
						]
					},
					"state": "minecraft:iron_ore"
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.ore("test_ore_not", size = 9) {
		targets {
			target(blockState(Blocks.IRON_ORE)) {
				target = not(blockMatch(Blocks.BEDROCK))
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:ore",
			"size": 9,
			"discard_chance_on_air_exposure": 0.0,
			"targets": [
				{
					"target": {
						"predicate_type": "minecraft:not",
						"rule": {
							"predicate_type": "minecraft:block_match",
							"block": "minecraft:bedrock"
						}
					},
					"state": "minecraft:iron_ore"
				}
			]
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
			"chance_of_taller_generation": 0.5,
			"chance_of_directional_spread": 0.5,
			"chance_of_spread_radius2": 0.5,
			"chance_of_spread_radius3": 0.5,
			"base_block": "minecraft:dripstone_block",
			"pointed_block": "minecraft:pointed_dripstone",
			"replaceable_blocks": [
				"minecraft:stone",
				"minecraft:dirt"
			]
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
			"feature_false": "minecraft:fossil_lower",
			"feature_true": "minecraft:fossil_upper"
		}
	""".trimIndent()

	configuredFeaturesBuilder.overlay("test_overlay", PlacedFeatures.ACACIA, PlacedFeatures.ACACIA_CHECKED)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:overlay",
			"features": [
				"minecraft:acacia",
				"minecraft:acacia_checked"
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.overlay("test_overlay_single", PlacedFeatures.ACACIA)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:overlay",
			"features": "minecraft:acacia"
		}
	""".trimIndent()

	configuredFeaturesBuilder.overlay("test_overlay_tag", PlacedFeatureTagArgument("my_features", "test"))

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:overlay",
			"features": "#test:my_features"
		}
	""".trimIndent()

	configuredFeaturesBuilder.overlay("test_overlay_inline") {
		feature(PlacedFeatures.BASALT_BLOBS)
		placed(Features.BASALT_PILLAR) {
			offset(y = -2)
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:overlay",
			"features": [
				"minecraft:basalt_blobs",
				{
					"feature": "minecraft:basalt_pillar",
					"placement": [
						{
							"type": "minecraft:offset",
							"x": 0,
							"y": -2,
							"z": 0
						}
					]
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.overlay("test_overlay_inline_single") {
		placed(Features.DESERT_WELL)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:overlay",
			"features": {
				"feature": "minecraft:desert_well",
				"placement": []
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.overlay("test_overlay_randomly_selected") {
		placed(Features.DESERT_WELL) {
			randomlySelected {
				offset(y = -1)
				offset(x = 1, y = -1)
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:overlay",
			"features": {
				"feature": "minecraft:desert_well",
				"placement": [
					{
						"type": "minecraft:randomly_selected",
						"placements": [
							{
								"type": "minecraft:offset",
								"x": 0,
								"y": -1,
								"z": 0
							},
							{
								"type": "minecraft:offset",
								"x": 1,
								"y": -1,
								"z": 0
							}
						]
					}
				]
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.projectedRandomPatchySquare("test_projected_random_patchy_square") {
		block = simpleStateProvider(Blocks.BASALT)
		projectThrough { matchingBlocks(Blocks.AIR) }
		maxProjectionHeight = 3
		size(3)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:projected_random_patchy_square",
			"block": {
				"type": "minecraft:simple",
				"state": "minecraft:basalt"
			},
			"max_projection_height": 3,
			"project_through": {
				"type": "minecraft:matching_blocks",
				"blocks": "minecraft:air"
			},
			"size": 3
		}
	""".trimIndent()

	configuredFeaturesBuilder.randomNeighborSpread("test_random_neighbor_spread") {
		block = simpleStateProvider(Blocks.GLOWSTONE)
		acceptedNeighbors(Blocks.GLOWSTONE, Blocks.NETHERRACK)
		canReplace { matchingBlocks(Blocks.AIR) }
		attempts(1500)
		xzOffset = uniform(-7, 7)
		yOffset = uniform(-11, 0)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:random_neighbor_spread",
			"accepted_neighbors": [
				"minecraft:glowstone",
				"minecraft:netherrack"
			],
			"attempts": 1500,
			"block": {
				"type": "minecraft:simple",
				"state": "minecraft:glowstone"
			},
			"can_replace": {
				"type": "minecraft:matching_blocks",
				"blocks": "minecraft:air"
			},
			"xz_offset": {
				"type": "minecraft:uniform",
				"min_inclusive": -7,
				"max_inclusive": 7
			},
			"y_offset": {
				"type": "minecraft:uniform",
				"min_inclusive": -11,
				"max_inclusive": 0
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.randomSelector("test_random_selector", default = PlacedFeatures.ACACIA) {
		feature(PlacedFeatures.ACACIA_CHECKED, 0.5f)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:random_selector",
			"features": [
				{
					"chance": 0.5,
					"feature": "minecraft:acacia_checked"
				}
			],
			"default": "minecraft:acacia"
		}
	""".trimIndent()

	configuredFeaturesBuilder.replaceSingleBlock("test_replace_single_block") {
		targets {
			target(blockState(Blocks.STONE)) {
				target = randomBlockMatch(Blocks.STONE, 0.5)
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:replace_single_block",
			"targets": [
				{
					"target": {
						"predicate_type": "minecraft:random_block_match",
						"block": "minecraft:stone",
						"probability": 0.5
					},
					"state": "minecraft:stone"
				}
			]
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
		allowedVerticalWaterForTree = 1
		rootStateProvider = simpleStateProvider(Blocks.DIRT)
		hangingRootStateProvider = simpleStateProvider(Blocks.DIRT)
		levelTestDistance = 5
		maxLevelDeviation = 1
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:root_system",
			"required_vertical_space_for_tree": 2,
			"root_radius": 3,
			"root_replaceable": "minecraft:dirt",
			"root_placement_attempts": 6,
			"root_column_max_height": 3,
			"hanging_root_radius": 2,
			"hanging_roots_vertical_span": 2,
			"hanging_root_placement_attempts": 4,
			"allowed_vertical_water_for_tree": 1,
			"root_state_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:dirt"
			},
			"hanging_root_state_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:dirt"
			},
			"allowed_tree_position": {
				"type": "minecraft:true"
			},
			"level_test_distance": 5,
			"max_level_deviation": 1,
			"feature": "minecraft:acacia"
		}
	""".trimIndent()

	configuredFeaturesBuilder.rootSystem("test_root_system_default", feature = PlacedFeatures.ACACIA)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:root_system",
			"required_vertical_space_for_tree": 1,
			"root_radius": 1,
			"root_replaceable": [],
			"root_placement_attempts": 1,
			"root_column_max_height": 1,
			"hanging_root_radius": 1,
			"hanging_roots_vertical_span": 1,
			"hanging_root_placement_attempts": 0,
			"allowed_vertical_water_for_tree": 1,
			"root_state_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"hanging_root_state_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"allowed_tree_position": {
				"type": "minecraft:true"
			},
			"level_test_distance": 0,
			"max_level_deviation": 0,
			"feature": "minecraft:acacia"
		}
	""".trimIndent()

	configuredFeaturesBuilder.simpleBlock("test_rule_based_provider_list_block") {
		toPlace = ruleBasedStateProvider {
			fallback = simpleStateProvider(Blocks.STONE)
			rule(simpleStateProvider(Blocks.GRAVEL)) {
				solid()
			}
			rule(simpleStateProvider(Blocks.SAND)) {
				solid()
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:simple_block",
			"to_place": {
				"type": "minecraft:rule_based",
				"fallback": {
					"type": "minecraft:simple",
					"state": "minecraft:stone"
				},
				"rules": [
					{
						"if_true": {
							"type": "minecraft:solid"
						},
						"then": {
							"type": "minecraft:simple",
							"state": "minecraft:gravel"
						}
					},
					{
						"if_true": {
							"type": "minecraft:solid"
						},
						"then": {
							"type": "minecraft:simple",
							"state": "minecraft:sand"
						}
					}
				]
			},
			"schedule_tick": false
		}
	""".trimIndent()

	configuredFeaturesBuilder.simpleBlock("test_rule_based_provider_unified") {
		toPlace = ruleBasedStateProvider {
			fallback = simpleStateProvider(Blocks.STONE)
			rule {
				ifTrue {
					solid()
				}
				then = simpleStateProvider(Blocks.DIRT)
			}
			rule(simpleStateProvider(Blocks.GRAVEL)) {
				hasSturdyFace(Direction.DOWN)
			}
			rule(simpleStateProvider(Blocks.SAND)) {
				not {
					matchingBlocks(Blocks.STONE)
				}
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:simple_block",
			"to_place": {
				"type": "minecraft:rule_based",
				"fallback": {
					"type": "minecraft:simple",
					"state": "minecraft:stone"
				},
				"rules": [
					{
						"if_true": {
							"type": "minecraft:solid"
						},
						"then": {
							"type": "minecraft:simple",
							"state": "minecraft:dirt"
						}
					},
					{
						"if_true": {
							"type": "minecraft:has_sturdy_face",
							"direction": "down"
						},
						"then": {
							"type": "minecraft:simple",
							"state": "minecraft:gravel"
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
							"type": "minecraft:simple",
							"state": "minecraft:sand"
						}
					}
				]
			},
			"schedule_tick": false
		}
	""".trimIndent()

	configuredFeaturesBuilder.scatteredOre("test_scattered_ore", 5, 0.5) {
		targets {
			target(blockState(Blocks.STONE)) {
				target = randomBlockMatch(Blocks.STONE, 0.5)
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:scattered_ore",
			"size": 5,
			"discard_chance_on_air_exposure": 0.5,
			"targets": [
				{
					"target": {
						"predicate_type": "minecraft:random_block_match",
						"block": "minecraft:stone",
						"probability": 0.5
					},
					"state": "minecraft:stone"
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.sculkPatch("test_sculk_patch") {
		chargeCount = 10
		amountPerCharge = 2
		spreadAttempts = 45
		growthRounds = 1
		spreadRounds = 2
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:sculk_patch",
			"charge_count": 10,
			"amount_per_charge": 2,
			"spread_attempts": 45,
			"growth_rounds": 1,
			"spread_rounds": 2
		}
	""".trimIndent()

	configuredFeaturesBuilder.sequence("test_sequence", PlacedFeatures.ACACIA, PlacedFeatures.ACACIA_CHECKED)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"features": [
				"minecraft:acacia",
				"minecraft:acacia_checked"
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.sequence("test_sequence_inline") {
		feature(PlacedFeatures.ACACIA)
		placed(Features.FLOWER_DEFAULT) {
			offset(y = 1)
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"features": [
				"minecraft:acacia",
				{
					"feature": "minecraft:flower_default",
					"placement": [
						{
							"type": "minecraft:offset",
							"x": 0,
							"y": 1,
							"z": 0
						}
					]
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.simpleBlock("test_simple_block", scheduleTick = true) {
		toPlace = simpleStateProvider(Blocks.STONE)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:simple_block",
			"to_place": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"schedule_tick": true
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
			"features": [
				"minecraft:acacia",
				"minecraft:acacia_checked"
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.simpleRandomSelector("test_simple_random_selector_inline") {
		feature(PlacedFeatures.BIRCH_CHECKED)
		placed(Features.BIRCH) {
			offset(y = 1)
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:simple_random_selector",
			"features": [
				"minecraft:birch_checked",
				{
					"feature": "minecraft:birch",
					"placement": [
						{
							"type": "minecraft:offset",
							"x": 0,
							"y": 1,
							"z": 0
						}
					]
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.singleBlockPillar("test_single_block_pillar_defaults")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:single_block_pillar",
			"block": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"direction": "up"
		}
	""".trimIndent()

	configuredFeaturesBuilder.singleBlockPillar("test_single_block_pillar", VerticalDirection.DOWN) {
		block = simpleStateProvider(Blocks.BASALT)
		canReplace { replaceable() }
		capFeature = PlacedFeatures.ACACIA
		chanceToContinue = 0.5f
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:single_block_pillar",
			"block": {
				"type": "minecraft:simple",
				"state": "minecraft:basalt"
			},
			"can_replace": {
				"type": "minecraft:replaceable"
			},
			"cap_feature": "minecraft:acacia",
			"chance_to_continue": 0.5,
			"direction": "down"
		}
	""".trimIndent()

	configuredFeaturesBuilder.spike("test_spike", state = blockStateStone()) {
		canPlaceOn { solid() }
		canReplace { solid() }
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:spike",
			"can_place_on": {
				"type": "minecraft:solid"
			},
			"can_replace": {
				"type": "minecraft:solid"
			},
			"state": "minecraft:stone"
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
			"state": "minecraft:stone",
			"rock_count": 4,
			"hole_count": 1,
			"requires_block_below": true,
			"valid_blocks": [
				"minecraft:stone",
				"minecraft:gravel"
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.steppedColumnCluster("test_stepped_column_cluster") {
		block = simpleStateProvider(Blocks.BASALT)
		canReplace { matchingBlocks(Blocks.LAVA) }
		continueThrough { replaceable() }
		cannotPlaceOn(Blocks.LAVA)
		columnReach(1)
		columnCount = uniform(1, 4)
		height = uniform(2, 5)
		clusterReach(3)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:stepped_column_cluster",
			"block": {
				"type": "minecraft:simple",
				"state": "minecraft:basalt"
			},
			"can_replace": {
				"type": "minecraft:matching_blocks",
				"blocks": "minecraft:lava"
			},
			"cannot_place_on": "minecraft:lava",
			"cluster_reach": 3,
			"column_count": {
				"type": "minecraft:uniform",
				"min_inclusive": 1,
				"max_inclusive": 4
			},
			"column_reach": 1,
			"continue_through": {
				"type": "minecraft:replaceable"
			},
			"height": {
				"type": "minecraft:uniform",
				"min_inclusive": 2,
				"max_inclusive": 5
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
	""".trimIndent()

	configuredFeaturesBuilder.template(
		"test_template_processors",
		structureTemplate(Structures.DesertWell.SUSPICIOUS_SAND),
		processors = processorListOrInline {
			rules {
				rule {
					inputPredicate = blockMatch(Blocks.SUSPICIOUS_SAND)
					locationPredicate = alwaysTrue()
					outputState = blockState(Blocks.SUSPICIOUS_SAND)
					blockEntityModifier = appendLoot(LootTables.Archaeology.DESERT_WELL)
				}
			}
		},
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:template",
			"processors": {
				"processors": [
					{
						"processor_type": "minecraft:rule",
						"rules": [
							{
								"location_predicate": {
									"predicate_type": "minecraft:always_true"
								},
								"input_predicate": {
									"predicate_type": "minecraft:block_match",
									"block": "minecraft:suspicious_sand"
								},
								"output_state": "minecraft:suspicious_sand",
								"block_entity_modifier": {
									"type": "minecraft:append_loot",
									"loot_table": "minecraft:archaeology/desert_well"
								}
							}
						]
					}
				]
			},
			"templates": [
				{
					"data": {
						"id": "minecraft:desert_well/suspicious_sand"
					},
					"weight": 1
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.template(
		"test_template_processor_list",
		structureTemplate(Structures.DesertWell.WELL),
		processors = processorListOrInline(ProcessorLists.EMPTY),
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:template",
			"processors": "minecraft:empty",
			"templates": [
				{
					"data": {
						"id": "minecraft:desert_well/well"
					},
					"weight": 1
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("test_tree_foliage_plain") {
		blobFoliagePlacer(radius = 2, offset = 0, height = 3)
	}
	val plainFoliageJson = configuredFeatures.last().generateJson(this)

	configuredFeaturesBuilder.tree("test_tree_foliage_providers") {
		blobFoliagePlacer(radius = constant(2), offset = constant(0), height = 3)
	}
	configuredFeatures.last().generateJson(this) assertsIs plainFoliageJson

	configuredFeaturesBuilder.tree("test_tree_foliage_setter") {
		blobFoliagePlacer(height = 3) { radius(2) }
	}
	configuredFeatures.last().generateJson(this) assertsIs plainFoliageJson

	configuredFeaturesBuilder.tree("test_tree_foliage_setter_provider") {
		blobFoliagePlacer(height = 3) { radius(uniform(2, 3)) }
	}
	val setterProviderJson = configuredFeatures.last().generateJson(this)

	configuredFeaturesBuilder.tree("test_tree_foliage_provider_argument") {
		blobFoliagePlacer(radius = uniform(2, 3), height = 3)
	}
	configuredFeatures.last().generateJson(this) assertsIs setterProviderJson

	configuredFeaturesBuilder.geode("test_geode_plain_setters") {
		distributionPoints(3)
		outerWallDistance(5)
		pointOffset(2)
	}
	val plainGeodeJson = configuredFeatures.last().generateJson(this)

	configuredFeaturesBuilder.geode("test_geode_provider_setters") {
		distributionPoints = constant(3)
		outerWallDistance = constant(5)
		pointOffset = constant(2)
	}
	configuredFeatures.last().generateJson(this) assertsIs plainGeodeJson

	configuredFeaturesBuilder.fallenTree("test_fallen_tree_plain_log_length") {
		logLength(5)
	}
	val plainFallenTreeJson = configuredFeatures.last().generateJson(this)

	configuredFeaturesBuilder.fallenTree("test_fallen_tree_provider_log_length") {
		logLength = constant(5)
	}
	configuredFeatures.last().generateJson(this) assertsIs plainFallenTreeJson

	treeJson("test_tree_acacia_plain") { acaciaFoliagePlacer(radius = 2, offset = 1) } assertsIs
		treeJson("test_tree_acacia_providers") { acaciaFoliagePlacer(radius = constant(2), offset = constant(1)) }

	treeJson("test_tree_bush_plain") { bushFoliagePlacer(radius = 2, offset = 1, height = 2) } assertsIs
		treeJson("test_tree_bush_providers") { bushFoliagePlacer(radius = constant(2), offset = constant(1), height = 2) }

	treeJson("test_tree_dark_oak_plain") { darkOakFoliagePlacer(radius = 2, offset = 1) } assertsIs
		treeJson("test_tree_dark_oak_providers") { darkOakFoliagePlacer(radius = constant(2), offset = constant(1)) }

	treeJson("test_tree_fancy_plain") { fancyFoliagePlacer(radius = 2, offset = 4, height = 4) } assertsIs
		treeJson("test_tree_fancy_providers") { fancyFoliagePlacer(radius = constant(2), offset = constant(4), height = 4) }

	treeJson("test_tree_jungle_plain") { jungleFoliagePlacer(radius = 2, offset = 1, height = 2) } assertsIs
		treeJson("test_tree_jungle_providers") { jungleFoliagePlacer(radius = constant(2), offset = constant(1), height = 2) }

	treeJson("test_tree_mega_pine_plain") { megaPineFoliagePlacer(radius = 2, offset = 1, crownHeight = 3) } assertsIs
		treeJson("test_tree_mega_pine_providers") {
			megaPineFoliagePlacer(radius = constant(2), offset = constant(1), crownHeight = constant(3))
		}

	treeJson("test_tree_pine_plain") { pineFoliagePlacer(radius = 2, offset = 1, height = 3) } assertsIs
		treeJson("test_tree_pine_providers") {
			pineFoliagePlacer(radius = constant(2), offset = constant(1), height = constant(3))
		}

	treeJson("test_tree_spruce_plain") { spruceFoliagePlacer(radius = 2, offset = 1, trunkHeight = 3) } assertsIs
		treeJson("test_tree_spruce_providers") {
			spruceFoliagePlacer(radius = constant(2), offset = constant(1), trunkHeight = constant(3))
		}

	treeJson("test_tree_bending_plain") { bendingTrunkPlacer(baseHeight = 4, heightRandA = 2, heightRandB = 1, bendLength = 3) } assertsIs
		treeJson("test_tree_bending_providers") {
			bendingTrunkPlacer(baseHeight = 4, heightRandA = 2, heightRandB = 1, bendLength = constant(3))
		}

	treeJson("test_tree_cherry_trunk_plain") { cherryTrunkPlacer { branchEndOffsetFromTop(-2) } } assertsIs
		treeJson("test_tree_cherry_trunk_providers") { cherryTrunkPlacer { branchEndOffsetFromTop = constant(-2) } }

	treeJson("test_tree_upwards_branching_plain") {
		upwardsBranchingTrunkPlacer {
			extraBranchLength(2)
			extraBranchSteps(3)
		}
	} assertsIs treeJson("test_tree_upwards_branching_providers") {
		upwardsBranchingTrunkPlacer {
			extraBranchLength = constant(2)
			extraBranchSteps = constant(3)
		}
	}

	treeJson("test_tree_root_plain") { rootPlacer = mangroveRootPlacer { trunkOffsetY(2) } } assertsIs
		treeJson("test_tree_root_providers") { rootPlacer = mangroveRootPlacer { trunkOffsetY = constant(2) } }

	configuredFeaturesBuilder.tree("test_tree") {
		minimumSize = threeLayersFeatureSize {
			limit = 5
			upperLimit = 2
			lowerSize = 1
			middleSize = 2
			upperSize = 3
		}

		cherryTrunkPlacer {
			branchCount(2)
			branchHorizontalLength(6)
			branchStartOffsetFromTop = uniform(-10, -5)
		}

		cherryFoliagePlacer {
			height(5)
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
			"below_trunk_provider": {
				"type": "minecraft:rule_based",
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
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"foliage_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
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
						"type": "minecraft:simple",
						"state": "minecraft:dirt"
					},
					"directions": [
						"down"
					]
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("test_tree_alter_ground") {
		cherryTrunkPlacer {
			branchCount(1)
			branchHorizontalLength(2)
			branchStartOffsetFromTop = uniform(-3, -1)
		}
		cherryFoliagePlacer { height(2) }
		decorators {
			alterGround {
				fallback = simpleStateProvider(Blocks.DIRT)
				rule(simpleStateProvider(Blocks.GRASS_BLOCK)) {
					hasSturdyFace(Direction.DOWN)
				}
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"below_trunk_provider": {
				"type": "minecraft:rule_based",
				"rules": []
			},
			"minimum_size": {
				"type": "minecraft:two_layers_feature_size"
			},
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"foliage_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
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
						"type": "minecraft:rule_based",
						"fallback": {
							"type": "minecraft:simple",
							"state": "minecraft:dirt"
						},
						"rules": [
							{
								"if_true": {
									"type": "minecraft:has_sturdy_face",
									"direction": "down"
								},
								"then": {
									"type": "minecraft:simple",
									"state": "minecraft:grass_block"
								}
							}
						]
					}
				}
			]
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
			rule(simpleStateProvider(Blocks.GRASS_BLOCK)) {
				hasSturdyFace(Direction.DOWN)
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"below_trunk_provider": {
				"type": "minecraft:rule_based",
				"fallback": {
					"type": "minecraft:simple",
					"state": "minecraft:dirt"
				},
				"rules": [
					{
						"if_true": {
							"type": "minecraft:has_sturdy_face",
							"direction": "down"
						},
						"then": {
							"type": "minecraft:simple",
							"state": "minecraft:grass_block"
						}
					}
				]
			},
			"minimum_size": {
				"type": "minecraft:two_layers_feature_size"
			},
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"foliage_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
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
	""".trimIndent()

	configuredFeaturesBuilder.tree("test_upwards_branching_tree") {
		upwardsBranchingTrunkPlacer {
			baseHeight = 2
			heightRandA = 1
			heightRandB = 4
			extraBranchSteps = uniform(1, 4)
			extraBranchLength = uniform(0, 1)
			placeBranchPerLogProbability = 0.5
			canGrowThrough = listOf(Tags.Block.MANGROVE_LOGS_CAN_GROW_THROUGH)
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"below_trunk_provider": {
				"type": "minecraft:rule_based",
				"rules": []
			},
			"minimum_size": {
				"type": "minecraft:two_layers_feature_size"
			},
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"foliage_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"trunk_placer": {
				"type": "minecraft:upwards_branching_trunk_placer",
				"base_height": 2,
				"height_rand_a": 1,
				"height_rand_b": 4,
				"extra_branch_steps": {
					"type": "minecraft:uniform",
					"min_inclusive": 1,
					"max_inclusive": 4
				},
				"extra_branch_length": {
					"type": "minecraft:uniform",
					"min_inclusive": 0,
					"max_inclusive": 1
				},
				"place_branch_per_log_probability": 0.5,
				"can_grow_through": "#minecraft:mangrove_logs_can_grow_through"
			},
			"foliage_placer": {
				"type": "minecraft:fancy_foliage_placer",
				"radius": 0,
				"offset": 0,
				"height": 0
			},
			"decorators": []
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("mangrove") {
		ignoreVines = true
		belowTrunkProvider = ruleBasedStateProvider {
			rule {
				ifTrue {
					not {
						matchingBlockTag(tag = Tags.Block.CANNOT_REPLACE_BELOW_TREE_TRUNK)
					}
				}
				then = simpleStateProvider(Blocks.DIRT)
			}
		}
		minimumSize = twoLayersFeatureSize(limit = 2, upperSize = 2)
		trunkProvider = simpleStateProvider(blockState(Blocks.MANGROVE_LOG, "axis" to "y"))
		foliageProvider = simpleStateProvider(
			blockState(
				Blocks.MANGROVE_LEAVES,
				"distance" to "7",
				"persistent" to "false",
				"waterlogged" to "false"
			)
		)
		rootPlacer = mangroveRootPlacer {
			rootProvider = simpleStateProvider(blockState(Blocks.MANGROVE_ROOTS, "waterlogged" to "false"))
			trunkOffsetY = uniform(1, 3)
			aboveRootPlacement(chance = 0.5) {
				aboveRootProvider = simpleStateProvider(Blocks.MOSS_CARPET)
			}
			mangroveRootPlacement {
				maxRootWidth = 8
				maxRootLength = 15
				randomSkewChance = 0.2
				canGrowThrough = listOf(Tags.Block.MANGROVE_ROOTS_CAN_GROW_THROUGH)
				muddyRootsIn = listOf(Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS)
				muddyRootsProvider = simpleStateProvider(blockState(Blocks.MUDDY_MANGROVE_ROOTS, "axis" to "y"))
			}
		}
		upwardsBranchingTrunkPlacer {
			baseHeight = 2
			heightRandA = 1
			heightRandB = 4
			extraBranchSteps = uniform(1, 4)
			extraBranchLength = uniform(0, 1)
			placeBranchPerLogProbability = 0.5
			canGrowThrough = listOf(Tags.Block.MANGROVE_LOGS_CAN_GROW_THROUGH)
		}
		randomSpreadFoliagePlacer(
			radius = 3,
			offset = 0,
			foliageHeight = 2,
			leafPlacementAttempts = 70
		)
		decorators {
			leaveVine(0.125)
			attachedToLeaves {
				probability = 0.14
				exclusionRadiusXZ = 1
				exclusionRadiusY = 0
				requiredEmptyBlocks = 2
				blockProvider = randomizedIntStateProvider("age") {
					values(0, 4)
					source = simpleStateProvider(
						blockState(
							Blocks.MANGROVE_PROPAGULE,
							"age" to "0",
							"hanging" to "true",
							"stage" to "0",
							"waterlogged" to "false"
						)
					)
				}
				directions(Direction.DOWN)
			}
			beehive(0.01)
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"ignore_vines": true,
			"below_trunk_provider": {
				"type": "minecraft:rule_based",
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
							"type": "minecraft:simple",
							"state": "minecraft:dirt"
						}
					}
				]
			},
			"minimum_size": {
				"type": "minecraft:two_layers_feature_size",
				"limit": 2,
				"upper_size": 2
			},
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": {
					"id": "minecraft:mangrove_log",
					"properties": {
						"axis": "y"
					}
				}
			},
			"foliage_provider": {
				"type": "minecraft:simple",
				"state": {
					"id": "minecraft:mangrove_leaves",
					"properties": {
						"distance": "7",
						"persistent": "false",
						"waterlogged": "false"
					}
				}
			},
			"root_placer": {
				"type": "minecraft:mangrove_root_placer",
				"root_provider": {
					"type": "minecraft:simple",
					"state": {
						"id": "minecraft:mangrove_roots",
						"properties": {
							"waterlogged": "false"
						}
					}
				},
				"trunk_offset_y": {
					"type": "minecraft:uniform",
					"min_inclusive": 1,
					"max_inclusive": 3
				},
				"above_root_placement": {
					"above_root_provider": {
						"type": "minecraft:simple",
						"state": "minecraft:moss_carpet"
					},
					"above_root_placement_chance": 0.5
				},
				"mangrove_root_placement": {
					"max_root_width": 8,
					"max_root_length": 15,
					"random_skew_chance": 0.2,
					"can_grow_through": "#minecraft:mangrove_roots_can_grow_through",
					"muddy_roots_in": [
						"minecraft:mud",
						"minecraft:muddy_mangrove_roots"
					],
					"muddy_roots_provider": {
						"type": "minecraft:simple",
						"state": {
							"id": "minecraft:muddy_mangrove_roots",
							"properties": {
								"axis": "y"
							}
						}
					}
				}
			},
			"trunk_placer": {
				"type": "minecraft:upwards_branching_trunk_placer",
				"base_height": 2,
				"height_rand_a": 1,
				"height_rand_b": 4,
				"extra_branch_steps": {
					"type": "minecraft:uniform",
					"min_inclusive": 1,
					"max_inclusive": 4
				},
				"extra_branch_length": {
					"type": "minecraft:uniform",
					"min_inclusive": 0,
					"max_inclusive": 1
				},
				"place_branch_per_log_probability": 0.5,
				"can_grow_through": "#minecraft:mangrove_logs_can_grow_through"
			},
			"foliage_placer": {
				"type": "minecraft:random_spread_foliage_placer",
				"radius": 3,
				"offset": 0,
				"foliage_height": 2,
				"leaf_placement_attempts": 70
			},
			"decorators": [
				{
					"type": "minecraft:leave_vine",
					"probability": 0.125
				},
				{
					"type": "minecraft:attached_to_leaves",
					"probability": 0.14,
					"exclusion_radius_xz": 1,
					"exclusion_radius_y": 0,
					"required_empty_blocks": 2,
					"block_provider": {
						"type": "minecraft:randomized_int",
						"property": "age",
						"values": {
							"type": "minecraft:uniform",
							"min_inclusive": 0,
							"max_inclusive": 4
						},
						"source": {
							"type": "minecraft:simple",
							"state": {
								"id": "minecraft:mangrove_propagule",
								"properties": {
									"age": "0",
									"hanging": "true",
									"stage": "0",
									"waterlogged": "false"
								}
							}
						}
					},
					"directions": [
						"down"
					]
				},
				{
					"type": "minecraft:beehive",
					"probability": 0.01
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("pale_oak") {
		ignoreVines = true
		belowTrunkProvider = ruleBasedStateProvider {
			rule {
				ifTrue {
					not {
						matchingBlockTag(tag = Tags.Block.CANNOT_REPLACE_BELOW_TREE_TRUNK)
					}
				}
				then = simpleStateProvider(Blocks.DIRT)
			}
		}
		minimumSize = threeLayersFeatureSize { upperSize = 2 }
		trunkProvider = simpleStateProvider(blockState(Blocks.PALE_OAK_LOG, "axis" to "y"))
		foliageProvider = simpleStateProvider(
			blockState(
				Blocks.PALE_OAK_LEAVES,
				"distance" to "7",
				"persistent" to "false",
				"waterlogged" to "false"
			)
		)
		darkOakTrunkPlacer(baseHeight = 6, heightRandA = 2, heightRandB = 1)
		darkOakFoliagePlacer()
		decorators(paleMoss(groundProbability = 0.8, leavesProbability = 0.15, trunkProbability = 0.4))
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"ignore_vines": true,
			"below_trunk_provider": {
				"type": "minecraft:rule_based",
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
							"type": "minecraft:simple",
							"state": "minecraft:dirt"
						}
					}
				]
			},
			"minimum_size": {
				"type": "minecraft:three_layers_feature_size",
				"upper_size": 2
			},
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": {
					"id": "minecraft:pale_oak_log",
					"properties": {
						"axis": "y"
					}
				}
			},
			"foliage_provider": {
				"type": "minecraft:simple",
				"state": {
					"id": "minecraft:pale_oak_leaves",
					"properties": {
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
			"decorators": [
				{
					"type": "minecraft:pale_moss",
					"ground_probability": 0.8,
					"leaves_probability": 0.15,
					"trunk_probability": 0.4
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("pale_oak_creaking") {
		minimumSize = threeLayersFeatureSize { upperSize = 2 }
		darkOakTrunkPlacer(baseHeight = 6, heightRandA = 2, heightRandB = 1)
		darkOakFoliagePlacer()
		decorators {
			paleMoss(groundProbability = 0.8, leavesProbability = 0.15, trunkProbability = 0.4)
			creakingHeart(1.0)
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"below_trunk_provider": {
				"type": "minecraft:rule_based",
				"rules": []
			},
			"minimum_size": {
				"type": "minecraft:three_layers_feature_size",
				"upper_size": 2
			},
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"foliage_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
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
			"decorators": [
				{
					"type": "minecraft:pale_moss",
					"ground_probability": 0.8,
					"leaves_probability": 0.15,
					"trunk_probability": 0.4
				},
				{
					"type": "minecraft:creaking_heart",
					"probability": 1.0
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("oak_leaf_litter") {
		decorators {
			placeOnGround {
				blockStateProvider = weightedStateProvider {
					for (facing in listOf("north", "east", "south", "west")) {
						entry(blockState(Blocks.LEAF_LITTER, "facing" to facing, "segment_amount" to "1"))
					}
				}
				height = 2
				radius = 4
				tries = 96
			}
			placeOnGround {
				blockStateProvider = weightedStateProvider {
					entry(blockState(Blocks.LEAF_LITTER, "facing" to "north", "segment_amount" to "4"))
				}
				height = 2
				tries = 150
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"below_trunk_provider": {
				"type": "minecraft:rule_based",
				"rules": []
			},
			"minimum_size": {
				"type": "minecraft:two_layers_feature_size"
			},
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"foliage_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"trunk_placer": {
				"type": "minecraft:fancy_trunk_placer",
				"base_height": 0,
				"height_rand_a": 0,
				"height_rand_b": 0
			},
			"foliage_placer": {
				"type": "minecraft:fancy_foliage_placer",
				"radius": 0,
				"offset": 0,
				"height": 0
			},
			"decorators": [
				{
					"type": "minecraft:place_on_ground",
					"block_state_provider": {
						"type": "minecraft:weighted",
						"entries": [
							{
								"weight": 1,
								"data": {
									"type": "minecraft:simple",
									"state": {
										"id": "minecraft:leaf_litter",
										"properties": {
											"facing": "north",
											"segment_amount": "1"
										}
									}
								}
							},
							{
								"weight": 1,
								"data": {
									"type": "minecraft:simple",
									"state": {
										"id": "minecraft:leaf_litter",
										"properties": {
											"facing": "east",
											"segment_amount": "1"
										}
									}
								}
							},
							{
								"weight": 1,
								"data": {
									"type": "minecraft:simple",
									"state": {
										"id": "minecraft:leaf_litter",
										"properties": {
											"facing": "south",
											"segment_amount": "1"
										}
									}
								}
							},
							{
								"weight": 1,
								"data": {
									"type": "minecraft:simple",
									"state": {
										"id": "minecraft:leaf_litter",
										"properties": {
											"facing": "west",
											"segment_amount": "1"
										}
									}
								}
							}
						]
					},
					"height": 2,
					"radius": 4,
					"tries": 96
				},
				{
					"type": "minecraft:place_on_ground",
					"block_state_provider": {
						"type": "minecraft:weighted",
						"entries": [
							{
								"weight": 1,
								"data": {
									"type": "minecraft:simple",
									"state": {
										"id": "minecraft:leaf_litter",
										"properties": {
											"facing": "north",
											"segment_amount": "4"
										}
									}
								}
							}
						]
					},
					"height": 2,
					"tries": 150
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("test_place_on_ground_default") {
		decorators { placeOnGround() }
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"below_trunk_provider": {
				"type": "minecraft:rule_based",
				"rules": []
			},
			"minimum_size": {
				"type": "minecraft:two_layers_feature_size"
			},
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"foliage_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"trunk_placer": {
				"type": "minecraft:fancy_trunk_placer",
				"base_height": 0,
				"height_rand_a": 0,
				"height_rand_b": 0
			},
			"foliage_placer": {
				"type": "minecraft:fancy_foliage_placer",
				"radius": 0,
				"offset": 0,
				"height": 0
			},
			"decorators": [
				{
					"type": "minecraft:place_on_ground",
					"block_state_provider": {
						"type": "minecraft:simple",
						"state": "minecraft:stone"
					}
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("test_attached_to_logs") {
		decorators {
			attachedToLogs(Direction.UP) {
				probability = 0.1
				blockProvider = weightedStateProvider {
					entry(Blocks.RED_MUSHROOM, weight = 2)
					entry(Blocks.BROWN_MUSHROOM, weight = 1)
				}
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"below_trunk_provider": {
				"type": "minecraft:rule_based",
				"rules": []
			},
			"minimum_size": {
				"type": "minecraft:two_layers_feature_size"
			},
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"foliage_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"trunk_placer": {
				"type": "minecraft:fancy_trunk_placer",
				"base_height": 0,
				"height_rand_a": 0,
				"height_rand_b": 0
			},
			"foliage_placer": {
				"type": "minecraft:fancy_foliage_placer",
				"radius": 0,
				"offset": 0,
				"height": 0
			},
			"decorators": [
				{
					"type": "minecraft:attached_to_logs",
					"block_provider": {
						"type": "minecraft:weighted",
						"entries": [
							{
								"weight": 2,
								"data": {
									"type": "minecraft:simple",
									"state": "minecraft:red_mushroom"
								}
							},
							{
								"weight": 1,
								"data": {
									"type": "minecraft:simple",
									"state": "minecraft:brown_mushroom"
								}
							}
						]
					},
					"directions": [
						"up"
					],
					"probability": 0.1
				}
			]
		}
	""".trimIndent()

	configuredFeaturesBuilder.fallenTree("fallen_oak_tree") {
		trunkProvider = simpleStateProvider(blockState(Blocks.OAK_LOG, "axis" to "y"))
		logLength = uniform(4, 7)
		stumpDecorators { trunkVine() }
		logDecorators {
			attachedToLogs(Direction.UP) {
				probability = 0.1
				blockProvider = weightedStateProvider {
					entry(Blocks.RED_MUSHROOM, weight = 2)
					entry(Blocks.BROWN_MUSHROOM, weight = 1)
				}
			}
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:fallen_tree",
			"log_decorators": [
				{
					"type": "minecraft:attached_to_logs",
					"block_provider": {
						"type": "minecraft:weighted",
						"entries": [
							{
								"weight": 2,
								"data": {
									"type": "minecraft:simple",
									"state": "minecraft:red_mushroom"
								}
							},
							{
								"weight": 1,
								"data": {
									"type": "minecraft:simple",
									"state": "minecraft:brown_mushroom"
								}
							}
						]
					},
					"directions": [
						"up"
					],
					"probability": 0.1
				}
			],
			"log_length": {
				"type": "minecraft:uniform",
				"min_inclusive": 4,
				"max_inclusive": 7
			},
			"stump_decorators": [
				{
					"type": "minecraft:trunk_vine"
				}
			],
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": {
					"id": "minecraft:oak_log",
					"properties": {
						"axis": "y"
					}
				}
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.fallenTree("fallen_poplar_tree") {
		trunkProvider = simpleStateProvider(blockState(Blocks.POPLAR_LOG, "axis" to "y"))
		logLength = uniform(4, 7)
		logDecorators {
			attachedToLogs(Direction.UP) {
				probability = 0.1
				blockProvider = simpleStateProvider(Blocks.BROWN_MUSHROOM)
			}
			shelfMushroom(0.8)
		}
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:fallen_tree",
			"log_decorators": [
				{
					"type": "minecraft:attached_to_logs",
					"block_provider": {
						"type": "minecraft:simple",
						"state": "minecraft:brown_mushroom"
					},
					"directions": [
						"up"
					],
					"probability": 0.1
				},
				{
					"type": "minecraft:shelf_mushroom",
					"probability": 0.8
				}
			],
			"log_length": {
				"type": "minecraft:uniform",
				"min_inclusive": 4,
				"max_inclusive": 7
			},
			"stump_decorators": [],
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": {
					"id": "minecraft:poplar_log",
					"properties": {
						"axis": "y"
					}
				}
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.fallenTree("test_fallen_tree_default")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:fallen_tree",
			"log_decorators": [],
			"log_length": 0,
			"stump_decorators": [],
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			}
		}
	""".trimIndent()

	jsonEncoder.encodeToString(
		mangroveRootPlacer {
			mangroveRootPlacement {
				canGrowThrough = listOf(Tags.Block.MANGROVE_ROOTS_CAN_GROW_THROUGH)
			}
		}
	) assertsIsJson """
		{
			"root_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"trunk_offset_y": 0,
			"mangrove_root_placement": {
				"max_root_width": 0,
				"max_root_length": 0,
				"random_skew_chance": 0.0,
				"can_grow_through": "#minecraft:mangrove_roots_can_grow_through",
				"muddy_roots_in": [],
				"muddy_roots_provider": {
					"type": "minecraft:simple",
					"state": "minecraft:stone"
				}
			}
		}
	""".trimIndent()

	configuredFeaturesBuilder.endGateway("test_triple_as_array", exact = true)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:end_gateway",
			"exact": true
		}
	""".trimIndent()

	configuredFeaturesBuilder.endPodium("test_end_podium", active = true)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:end_podium",
			"active": true
		}
	""".trimIndent()

	configuredFeaturesBuilder.tree("test_poplar") {
		poplarTrunkPlacer {
			baseHeight = 7
			branchAmount = uniform(1, 4)
			heightRandA = 4
			trunkHeightAboveBranches(4)
		}
		poplarFoliagePlacer {
			height = uniform(5, 6)
			sideHoleChance = 0.15
		}
		decorators { shelfMushroom(0.4) }
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"below_trunk_provider": {
				"type": "minecraft:rule_based",
				"rules": []
			},
			"minimum_size": {
				"type": "minecraft:two_layers_feature_size"
			},
			"trunk_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"foliage_provider": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"trunk_placer": {
				"type": "minecraft:poplar_trunk_placer",
				"base_height": 7,
				"height_rand_a": 4,
				"height_rand_b": 0,
				"branch_amount": {
					"type": "minecraft:uniform",
					"min_inclusive": 1,
					"max_inclusive": 4
				},
				"trunk_height_above_branches": 4
			},
			"foliage_placer": {
				"type": "minecraft:poplar_foliage_placer",
				"radius": 0,
				"offset": 0,
				"height": {
					"type": "minecraft:uniform",
					"min_inclusive": 5,
					"max_inclusive": 6
				},
				"side_hole_chance": 0.15
			},
			"decorators": [
				{
					"type": "minecraft:shelf_mushroom",
					"probability": 0.4
				}
			]
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
			"floor_search_range": 3,
			"placement_radius_around_floor": 1,
			"placement_probability_per_valid_position": 0.5
		}
	""".trimIndent()

	configuredFeaturesBuilder.vegetationPatch(
		"test_vegetation_patch", surface = Surface.FLOOR,
		replaceable = listOf(Tags.Block.DIRT),
		vegetationFeature = PlacedFeatures.ACACIA
	) {
		depth(3)
		verticalRange = 3
		vegetationChance = 0.5
		xzRadius(2)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:vegetation_patch",
			"surface": "floor",
			"depth": 3,
			"vertical_range": 3,
			"extra_bottom_block_chance": 0.0,
			"extra_edge_column_chance": 0.0,
			"vegetation_chance": 0.5,
			"xz_radius": 2,
			"replaceable": "#minecraft:dirt",
			"ground_state": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"vegetation_feature": "minecraft:acacia"
		}
	""".trimIndent()

	configuredFeaturesBuilder.vines("test_vines")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:vines"
		}
	""".trimIndent()

	configuredFeaturesBuilder.voidStartPlatform("test_void_start_platform")

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:void_start_platform"
		}
	""".trimIndent()

	configuredFeaturesBuilder.waterloggedVegetationPatch(
		"test_waterlogged_vegetation_patch", surface = Surface.CEILING,
		replaceable = listOf(Tags.Block.DIRT),
		vegetationFeature = PlacedFeatures.ACACIA
	) {
		depth(2)
		verticalRange = 8
		vegetationChance = 0.5
		xzRadius = uniform(4, 7)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:waterlogged_vegetation_patch",
			"surface": "ceiling",
			"depth": 2,
			"vertical_range": 8,
			"extra_bottom_block_chance": 0.0,
			"extra_edge_column_chance": 0.0,
			"vegetation_chance": 0.5,
			"xz_radius": {
				"type": "minecraft:uniform",
				"min_inclusive": 4,
				"max_inclusive": 7
			},
			"replaceable": "#minecraft:dirt",
			"ground_state": {
				"type": "minecraft:simple",
				"state": "minecraft:stone"
			},
			"vegetation_feature": "minecraft:acacia"
		}
	""".trimIndent()

	configuredFeaturesBuilder.weightedRandomSelector("test_weighted_random_selector") {
		feature(PlacedFeatures.ACACIA, weight = 3)
		feature(PlacedFeatures.OAK, weight = 1)
	}

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:weighted_random_selector",
			"features": [
				{
					"data": "minecraft:acacia",
					"weight": 3
				},
				{
					"data": "minecraft:oak",
					"weight": 1
				}
			]
		}
	""".trimIndent()
}

private fun DataPack.treeJson(fileName: String, block: Tree.() -> Unit): String {
	configuredFeaturesBuilder.tree(fileName, block)
	return configuredFeatures.last().generateJson(this)
}

class ConfiguredFeatureTests : FunSpec({
	test("configured feature") {
		dataPack("configuredFeature") {
			pretty()
			configuredFeatureTests()
		}
	}

	test("overlay features decode an ID, a tag and an inline placed feature") {
		val json = dataPack("placedFeatureOrInline") {}.jsonEncoder
		val mixed = """{"features":["minecraft:basalt_blobs","minecraft:acacia",{"feature":"minecraft:basalt_pillar","placement":[{"type":"minecraft:in_square"}]}]}"""

		val overlay = json.decodeFromString<Overlay>(mixed)
		overlay.features.map { it.feature?.asString() } shouldBe listOf("minecraft:basalt_blobs", "minecraft:acacia", null)
		overlay.features.map { it.inline?.feature?.asString() } shouldBe listOf(null, null, "minecraft:basalt_pillar")
		json.encodeToString(overlay) shouldBe mixed

		val tag = """{"features":"#test:my_features"}"""
		json.decodeFromString<Overlay>(tag).features.single().feature!!.asString() shouldBe "#test:my_features"
		json.encodeToString(json.decodeFromString<Overlay>(tag)) shouldBe tag

		val single = """{"features":{"feature":"minecraft:desert_well","placement":[]}}"""
		json.decodeFromString<Overlay>(single).features.single().inline!!.feature.asString() shouldBe "minecraft:desert_well"
		json.encodeToString(json.decodeFromString<Overlay>(single)) shouldBe single
	}

	test("an inline placed feature with an offset placement modifier round trips") {
		val json = dataPack("placedFeatureOrInlineOffset") {}.jsonEncoder
		val offset = """{"features":{"feature":"minecraft:basalt_pillar","placement":[{"type":"minecraft:offset","x":0,"y":-2,"z":0}]}}"""

		val decoded = json.decodeFromString<Overlay>(offset)
		decoded.features.single().inline!!.placementModifiers shouldBe
			listOf(Offset(ConstantIntProvider(0), ConstantIntProvider(-2), ConstantIntProvider(0)))
		json.encodeToString(decoded) shouldBe offset
	}

	test("an inline placed feature that can't be decoded throws instead of dropping its placement") {
		val json = dataPack("placedFeatureOrInlineInvalid") {}.jsonEncoder
		val invalidInline = """{"feature":"minecraft:basalt_pillar","placement":[{"type":"minecraft:not_a_modifier"}]}"""

		shouldThrow<SerializationException> { json.decodeFromString<Overlay>("""{"features":$invalidInline}""") }
		shouldThrow<SerializationException> { json.decodeFromString<Overlay>("""{"features":["minecraft:acacia",$invalidInline]}""") }
	}
})
