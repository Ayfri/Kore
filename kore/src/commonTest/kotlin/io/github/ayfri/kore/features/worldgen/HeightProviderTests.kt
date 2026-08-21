package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.heightproviders.*
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.heightRange
import io.github.ayfri.kore.features.worldgen.placedfeature.placedFeature
import io.github.ayfri.kore.features.worldgen.verticalanchors.aboveBottom
import io.github.ayfri.kore.features.worldgen.verticalanchors.absolute
import io.github.ayfri.kore.features.worldgen.verticalanchors.belowTop
import io.github.ayfri.kore.generated.ConfiguredFeatures
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.heightProviderTests() {
	placedFeature("constant_absolute_test", ConfiguredFeatures.ACACIA) {
		heightRange(constantAbsolute(32))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:height_range",
					"height": {
						"absolute": 32
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("constant_above_bottom_test", ConfiguredFeatures.ACACIA) {
		heightRange(constantAboveBottom(8))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:height_range",
					"height": {
						"above_bottom": 8
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("constant_below_top_test", ConfiguredFeatures.ACACIA) {
		heightRange(constantBelowTop(4))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:height_range",
					"height": {
						"below_top": 4
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("constant_anchor_test", ConfiguredFeatures.ACACIA) {
		heightRange(constantHeightProvider(aboveBottom(16)))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:height_range",
					"height": {
						"above_bottom": 16
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("uniform_test", ConfiguredFeatures.ACACIA) {
		heightRange(uniformHeightProvider(aboveBottom(8), belowTop(2)))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:height_range",
					"height": {
						"type": "minecraft:uniform",
						"min_inclusive": {
							"above_bottom": 8
						},
						"max_inclusive": {
							"below_top": 2
						}
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("uniform_absolute_test", ConfiguredFeatures.ACACIA) {
		heightRange(uniformHeightProvider(0, 64))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:height_range",
					"height": {
						"type": "minecraft:uniform",
						"min_inclusive": {
							"absolute": 0
						},
						"max_inclusive": {
							"absolute": 64
						}
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("trapezoid_test", ConfiguredFeatures.ACACIA) {
		heightRange(trapezoidHeightProvider(absolute(0), absolute(128), plateau = 32))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:height_range",
					"height": {
						"type": "minecraft:trapezoid",
						"min_inclusive": {
							"absolute": 0
						},
						"max_inclusive": {
							"absolute": 128
						},
						"plateau": 32
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("trapezoid_absolute_test", ConfiguredFeatures.ACACIA) {
		heightRange(trapezoidHeightProvider(0, 64))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:height_range",
					"height": {
						"type": "minecraft:trapezoid",
						"min_inclusive": {
							"absolute": 0
						},
						"max_inclusive": {
							"absolute": 64
						}
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("biased_to_bottom_test", ConfiguredFeatures.ACACIA) {
		heightRange(biasedToBottomHeightProvider(aboveBottom(8), absolute(64), inner = 4))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:height_range",
					"height": {
						"type": "minecraft:biased_to_bottom",
						"min_inclusive": {
							"above_bottom": 8
						},
						"max_inclusive": {
							"absolute": 64
						},
						"inner": 4
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("very_biased_to_bottom_test", ConfiguredFeatures.ACACIA) {
		heightRange(veryBiasedToBottomHeightProvider(-64, 16))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:height_range",
					"height": {
						"type": "minecraft:very_biased_to_bottom",
						"min_inclusive": {
							"absolute": -64
						},
						"max_inclusive": {
							"absolute": 16
						}
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("weighted_list_test", ConfiguredFeatures.ACACIA) {
		heightRange(weightedListHeightProvider {
			entry(3, constantAbsolute(32))
			entry(1, uniformHeightProvider(64, 96))
		})
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:height_range",
					"height": {
						"type": "minecraft:weighted_list",
						"distribution": [
							{
								"weight": 3,
								"data": {
									"absolute": 32
								}
							},
							{
								"weight": 1,
								"data": {
									"type": "minecraft:uniform",
									"min_inclusive": {
										"absolute": 64
									},
									"max_inclusive": {
										"absolute": 96
									}
								}
							}
						]
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("weighted_list_pairs_test", ConfiguredFeatures.ACACIA) {
		heightRange(weightedListHeightProvider(2 to constantBelowTop(1), 1 to constantAboveBottom(1)))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:height_range",
					"height": {
						"type": "minecraft:weighted_list",
						"distribution": [
							{
								"weight": 2,
								"data": {
									"below_top": 1
								}
							},
							{
								"weight": 1,
								"data": {
									"above_bottom": 1
								}
							}
						]
					}
				}
			]
		}
	""".trimIndent()
}

class HeightProviderTests : FunSpec({
	test("height providers") {
		dataPack("heightProviders") {
			pretty()
			heightProviderTests()
		}
	}
})
