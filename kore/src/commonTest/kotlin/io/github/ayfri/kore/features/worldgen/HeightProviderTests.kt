package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.heightproviders.*
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.heightRange
import io.github.ayfri.kore.features.worldgen.placedfeature.placedFeature
import io.github.ayfri.kore.features.worldgen.verticalanchors.AboveBottom
import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import io.github.ayfri.kore.features.worldgen.verticalanchors.BelowTop
import io.github.ayfri.kore.features.worldgen.verticalanchors.aboveBottom
import io.github.ayfri.kore.features.worldgen.verticalanchors.absolute
import io.github.ayfri.kore.features.worldgen.verticalanchors.belowTop
import io.github.ayfri.kore.features.worldgen.verticalanchors.relativeToSeaLevel
import io.github.ayfri.kore.generated.Features
import io.github.ayfri.kore.utils.pretty
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString

fun DataPack.heightProviderTests() {
	placedFeature("constant_absolute_test", Features.ACACIA) {
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

	placedFeature("constant_above_bottom_test", Features.ACACIA) {
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

	placedFeature("constant_below_top_test", Features.ACACIA) {
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

	placedFeature("constant_anchor_test", Features.ACACIA) {
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

	placedFeature("relative_to_sea_level_test", Features.ACACIA) {
		heightRange(uniformHeightProvider(relativeToSeaLevel(-10), relativeToSeaLevel(10)))
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
							"relative_to_sea_level": -10
						},
						"max_inclusive": {
							"relative_to_sea_level": 10
						}
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("uniform_test", Features.ACACIA) {
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

	placedFeature("uniform_absolute_test", Features.ACACIA) {
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

	placedFeature("trapezoid_test", Features.ACACIA) {
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

	placedFeature("trapezoid_absolute_test", Features.ACACIA) {
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

	placedFeature("biased_to_bottom_test", Features.ACACIA) {
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

	placedFeature("very_biased_to_bottom_test", Features.ACACIA) {
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

	placedFeature("weighted_list_test", Features.ACACIA) {
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

	placedFeature("weighted_list_pairs_test", Features.ACACIA) {
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

	test("height providers decode a bare vertical anchor as a constant") {
		val json = dataPack("heightProvidersDecode") {}.jsonEncoder
		val serializer = HeightProvider.Companion.HeightProviderSerializer

		json.decodeFromString(serializer, """{"absolute":64}""") shouldBe ConstantHeightProvider(Absolute(64))
		json.decodeFromString(serializer, """{"above_bottom":8}""") shouldBe ConstantHeightProvider(AboveBottom(8))
		json.decodeFromString(serializer, """{"type":"minecraft:constant","value":{"below_top":2}}""") shouldBe
			ConstantHeightProvider(BelowTop(2))
		json.decodeFromString(serializer, """{"type":"minecraft:uniform","min_inclusive":{"absolute":1},"max_inclusive":{"absolute":9}}""") shouldBe
			UniformHeightProvider(Absolute(1), Absolute(9))
		json.encodeToString(serializer, ConstantHeightProvider(Absolute(64))) shouldBe """{"absolute":64}"""
	}

	test("height providers reject an object without a type that isn't a vertical anchor") {
		val json = dataPack("heightProvidersDecodeUntyped") {}.jsonEncoder
		val serializer = HeightProvider.Companion.HeightProviderSerializer

		shouldThrowAny { json.decodeFromString(serializer, """{"min_inclusive":{"absolute":1},"max_inclusive":{"absolute":9}}""") }
	}
})
