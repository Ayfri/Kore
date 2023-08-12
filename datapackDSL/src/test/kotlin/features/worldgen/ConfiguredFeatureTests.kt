package features.worldgen

import DataPack
import data.block.blockState
import features.worldgen.blockpredicate.allOf
import features.worldgen.blockpredicate.matchingBlocks
import features.worldgen.blockpredicate.not
import features.worldgen.blockpredicate.solid
import features.worldgen.configuredfeature.Direction
import features.worldgen.configuredfeature.blockstateprovider.dualNoiseProvider
import features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import features.worldgen.configuredfeature.blockstateprovider.slowNoise
import features.worldgen.configuredfeature.configurations.*
import features.worldgen.configuredfeature.configurations.tree.foliageplacer.cherryFoliagePlacer
import features.worldgen.configuredfeature.configurations.tree.layersfeaturesize.threeLayersFeatureSize
import features.worldgen.configuredfeature.configurations.tree.treedecorator.attachedToLeaves
import features.worldgen.configuredfeature.configurations.tree.trunkplacer.cherryTrunkPlacer
import features.worldgen.configuredfeature.configuredFeature
import features.worldgen.configuredfeature.target
import features.worldgen.ruletest.randomBlockMatch
import generated.Blocks
import generated.Tags
import utils.assertsIs

fun DataPack.configuredFeatureTests() {
	configuredFeature("test_tree", tree {
		minimumSize = threeLayersFeatureSize {
			limit = 5
			upperLimit = 2
			lowerSize = 1
			middleSize = 2
			upperSize = 3
		}

		cherryTrunkPlacer()
		cherryFoliagePlacer()
		decorators {
			attachedToLeaves {
				blockProvider = simpleStateProvider(Blocks.DIRT)
				directions = listOf(Direction.DOWN)
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
				"dirt_provider": {
					"type": "minecraft:simple_state_provider",
					"state": {
						"Name": "minecraft:stone"
					}
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
					"branch_count": 0,
					"branch_horizontal_length": 0,
					"branch_start_offset_from_top": {
						"min_inclusive": 0,
						"max_inclusive": 0
					},
					"branch_end_offset_from_top": 0
				},
				"foliage_placer": {
					"type": "minecraft:cherry_foliage_placer",
					"radius": 0,
					"offset": 0,
					"height": 0,
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
						"required_empty_blocks": 0,
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
							Tags.Blocks.DIRT,
							Blocks.DIRT,
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
									"#minecraft:dirt",
									"minecraft:dirt"
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
								"amplitudes": [
								]
							},
							"scale": 0.0,
							"variety": {
								"min_inclusive": 0,
								"max_inclusive": 0
							},
							"slow_noise": {
								"firstOctave": 1,
								"amplitudes": [
									1.2
								]
							},
							"slow_scale": 0.0,
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
}
