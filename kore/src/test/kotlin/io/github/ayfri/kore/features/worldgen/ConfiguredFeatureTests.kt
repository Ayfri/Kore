package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.blockpredicate.*
import io.github.ayfri.kore.features.worldgen.configuredfeature.Direction
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.*
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.*
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer.cherryFoliagePlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.layersfeaturesize.threeLayersFeatureSize
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator.attachedToLeaves
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer.cherryTrunkPlacer
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.target
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.intproviders.uniform
import io.github.ayfri.kore.features.worldgen.ruletest.randomBlockMatch
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun DataPack.configuredFeatureTests() {
	configuredFeature("test_simple_block", simpleBlock(simpleStateProvider(Blocks.STONE), scheduleTick = true))

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

	configuredFeature("test_tree", tree {
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
	})

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"config": {
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

	configuredFeature("test_ore", ore {
		targets {
			target(state = blockState(Blocks.STONE), target = randomBlockMatch(Blocks.STONE))
		}
	})

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

	configuredFeature(
		"test_triple_as_array", endGateway(exact = true)
	)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:end_gateway",
			"config": {
				"exact": true
			}
		}
	""".trimIndent()

	configuredFeature(
		"test_block_predicates_and_complex_provider",
		blockColumn(direction = Direction.DOWN) {
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
	)

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


	configuredFeature("end_platform", EndPlatform)

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:end_platform",
			"config": {}
		}
	""".trimIndent()

	configuredFeature("test_block_blob", blockBlob(canPlaceOn = Solid, state = blockStateStone()))

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

	configuredFeature("test_spike", spike(canPlaceOn = Solid, canReplace = Solid, state = blockStateStone()))

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

	configuredFeature("test_huge_red_mushroom", hugeRedMushroom(canPlaceOn = Solid))

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

	configuredFeature("test_tree_below_trunk", tree {
		cherryTrunkPlacer {
			branchCount = constant(1)
			branchHorizontalLength = constant(2)
			branchStartOffsetFromTop = uniform(-5, -2)
		}
		cherryFoliagePlacer { height = constant(3) }
		belowTrunkProvider = ruleBasedBlockStateProvider(fallback = simpleStateProvider(Blocks.DIRT))
	})

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:tree",
			"config": {
				"below_trunk_provider": {
					"fallback": {
						"type": "minecraft:simple_state_provider",
						"state": {
							"Name": "minecraft:dirt"
						}
					},
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
}

class ConfiguredFeatureTests : FunSpec({
	test("configured feature") {
		testDataPack("configuredFeature") {
			pretty()
			configuredFeatureTests()
		}.apply {
			assertGeneratorsGenerated()
			generate()
		}
	}
})
