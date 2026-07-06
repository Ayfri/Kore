package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.intproviders.*
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.count
import io.github.ayfri.kore.features.worldgen.placedfeature.placedFeature
import io.github.ayfri.kore.generated.ConfiguredFeatures
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.intProviderTests() {
	placedFeature("biased_to_bottom_test", ConfiguredFeatures.ACACIA) {
		count(biasedToBottom(1, 5))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:count",
					"count": {
						"type": "minecraft:biased_to_bottom",
						"min_inclusive": 1,
						"max_inclusive": 5
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("clamped_test", ConfiguredFeatures.ACACIA) {
		count(clamped(1, 5, constant(3)))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:count",
					"count": {
						"type": "minecraft:clamped",
						"min_inclusive": 1,
						"max_inclusive": 5,
						"source": 3
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("clamped_normal_test", ConfiguredFeatures.ACACIA) {
		count(clampedNormal(1, 5, 3.0f, 1.0f))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:count",
					"count": {
						"type": "minecraft:clamped_normal",
						"min_inclusive": 1,
						"max_inclusive": 5,
						"mean": 3.0,
						"deviation": 1.0
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("constant_test", ConfiguredFeatures.ACACIA) {
		count(constant(5))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:count",
					"count": 5
				}
			]
		}
	""".trimIndent()

	placedFeature("trapezoid_test", ConfiguredFeatures.ACACIA) {
		count(trapezoid(1, 5, 2))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:count",
					"count": {
						"type": "minecraft:trapezoid",
						"min": 1,
						"max": 5,
						"plateau": 2
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("uniform_test", ConfiguredFeatures.ACACIA) {
		count(uniform(1, 5))
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:count",
					"count": {
						"type": "minecraft:uniform",
						"min_inclusive": 1,
						"max_inclusive": 5
					}
				}
			]
		}
	""".trimIndent()

	placedFeature("weighted_list_test", ConfiguredFeatures.ACACIA) {
		count(weightedList {
			add(weightedEntry(3, uniform(1, 5)))
			add(weightedEntry(1, constant(10)))
		})
	}

	placedFeatures.last() assertsIs """
		{
			"feature": "minecraft:acacia",
			"placement": [
				{
					"type": "minecraft:count",
					"count": {
						"type": "minecraft:weighted_list",
						"distribution": [
							{
								"weight": 3,
								"data": {
									"type": "minecraft:uniform",
									"min_inclusive": 1,
									"max_inclusive": 5
								}
							},
							{
								"weight": 1,
								"data": 10
							}
						]
					}
				}
			]
		}
	""".trimIndent()
}

class IntProviderTests : FunSpec({
	test("int providers") {
		dataPack("intProviders") {
			pretty()
			intProviderTests()
		}
	}
})
