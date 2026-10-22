package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.blockpredicate.*
import io.github.ayfri.kore.features.worldgen.configuredfeature.VerticalDirection
import io.github.ayfri.kore.features.worldgen.heightproviders.constantAbsolute
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.features.worldgen.intproviders.uniform
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.*
import io.github.ayfri.kore.features.worldgen.placedfeature.placedFeature
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.Features
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.utils.pretty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

fun DataPack.placedFeatureTests() {
	placedFeature("test", Features.ACACIA) {
		noiseThresholdCount {
			noiseLevel = 2.0
			belowNoise = 2
			aboveNoise = 4
		}

		rarityFilter(5)

		blockPredicateFilter {
			predicate {
				matchingBlockTag(Tags.Block.DIRT)
				alwaysTrue()
			}
		}
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:noise_threshold_count",
					"noise_level": 2.0,
					"below_noise": 2,
					"above_noise": 4
				},
				{
					"type": "minecraft:rarity_filter",
					"chance": 5
				},
				{
					"type": "minecraft:block_predicate_filter",
					"predicate": {
						"type": "minecraft:all_of",
						"predicates": [
							{
								"type": "minecraft:matching_block_tag",
								"tag": "minecraft:dirt"
							},
							{
								"type": "minecraft:true"
							}
						]
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("matching_biomes", Features.ACACIA) {
		blockPredicateFilter {
			predicate { matchingBiomes(Biomes.PLAINS, Biomes.SAVANNA) }
		}
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:block_predicate_filter",
					"predicate": {
						"type": "minecraft:matching_biomes",
						"biomes": [
							"minecraft:plains",
							"minecraft:savanna"
						]
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("matching_biome_tag", Features.ACACIA) {
		blockPredicateFilter {
			predicate { matchingBiomes(Tags.Worldgen.Biome.IS_SAVANNA) }
		}
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:block_predicate_filter",
					"predicate": {
						"type": "minecraft:matching_biomes",
						"biomes": "#minecraft:is_savanna"
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("environment_scan", Features.ACACIA) {
		environmentScan(VerticalDirection.DOWN, 12) {
			targetCondition { solid() }
		}
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:environment_scan",
					"direction_of_search": "down",
					"max_steps": 12,
					"target_condition": {
						"type": "minecraft:solid"
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("offset", Features.ACACIA) {
		offset(constant(1), uniform(-2, 2), constant(0))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:offset",
					"x": 1,
					"y": {
						"type": "minecraft:uniform",
						"min_inclusive": -2,
						"max_inclusive": 2
					},
					"z": 0
				}
			]
		}
	""".trimIndent()

	placedFeature("offset_defaults", Features.ACACIA) {
		offset(y = constant(-1))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:offset",
					"x": 0,
					"y": -1,
					"z": 0
				}
			]
		}
	""".trimIndent()

	placedFeature("int_counts", Features.ACACIA) {
		count(10)
		countOnEveryLayer(3)
		heightRange(constantAbsolute(64))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:count",
					"count": 10
				},
				{
					"type": "minecraft:count_on_every_layer",
					"count": 3
				},
				{
					"type": "minecraft:height_range",
					"height": {
						"absolute": 64
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("cuboid", Features.ACACIA) {
		cuboid(4, 2)
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:cuboid",
					"xz_size": 4,
					"y_size": 2
				}
			]
		}
	""".trimIndent()

	placedFeature("cuboid_full", Features.ACACIA) {
		cuboid(uniform(2, 6), constant(3), includeEdges = false, includeInterior = true)
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:cuboid",
					"include_edges": false,
					"include_interior": true,
					"xz_size": {
						"type": "minecraft:uniform",
						"min_inclusive": 2,
						"max_inclusive": 6
					},
					"y_size": 3
				}
			]
		}
	""".trimIndent()

	placedFeature("random_chance", Features.ACACIA) {
		randomChance(0.25f)
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:random_chance",
					"chance": 0.25
				}
			]
		}
	""".trimIndent()

	placedFeature("randomly_selected", Features.ACACIA) {
		randomlySelected {
			offset(y = constant(-1))
			offset(x = constant(-1), y = constant(-1))
			offset(y = constant(-1), z = constant(1))
			offset(x = constant(1), y = constant(-1))
			offset(y = constant(-1), z = constant(-1))
		}
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
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
							"x": -1,
							"y": -1,
							"z": 0
						},
						{
							"type": "minecraft:offset",
							"x": 0,
							"y": -1,
							"z": 1
						},
						{
							"type": "minecraft:offset",
							"x": 1,
							"y": -1,
							"z": 0
						},
						{
							"type": "minecraft:offset",
							"x": 0,
							"y": -1,
							"z": -1
						}
					]
				}
			]
		}
	""".trimIndent()

	placedFeature("randomly_selected_nested", Features.ACACIA) {
		randomlySelected {
			randomlySelected {
				count(2)
			}
			inSquare()
		}
		rarityFilter(4)
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:randomly_selected",
					"placements": [
						{
							"type": "minecraft:randomly_selected",
							"placements": [
								{
									"type": "minecraft:count",
									"count": 2
								}
							]
						},
						{
							"type": "minecraft:in_square"
						}
					]
				},
				{
					"type": "minecraft:rarity_filter",
					"chance": 4
				}
			]
		}
	""".trimIndent()

	placedFeature("fixed_placement", Features.END_PLATFORM) {
		fixedPlacement {
			position(0, 0, 0)
		}
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:end_platform",
			"placement": [
				{
					"type": "minecraft:fixed_placement",
					"positions": [
						[
							0,
							0,
							0
						]
					]
				}
			]
		}
	""".trimIndent()
}

class PlacedFeatureTests : FunSpec({
	test("placed feature") {
		dataPack("placedFeature") {
			pretty()
			placedFeatureTests()
		}
	}

	test("randomly selected rejects an empty block") {
		shouldThrow<IllegalArgumentException> {
			dataPack("randomlySelectedEmpty") {
				placedFeature("empty", Features.ACACIA) { randomlySelected {} }
			}
		}
	}
})
