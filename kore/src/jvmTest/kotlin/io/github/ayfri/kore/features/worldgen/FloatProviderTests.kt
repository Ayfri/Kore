package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.largeDripstone
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.floatproviders.clampedNormal
import io.github.ayfri.kore.features.worldgen.floatproviders.constant
import io.github.ayfri.kore.features.worldgen.floatproviders.trapezoid
import io.github.ayfri.kore.features.worldgen.floatproviders.uniform
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun DataPack.floatProviderTests() {
	configuredFeature("clamped_normal_test", largeDripstone {
		heightScale = clampedNormal(mean = 0.5f, deviation = 0.1f, min = 0.0f, max = 1.0f)
	})

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:large_dripstone",
			"config": {
				"column_radius": 0,
				"height_scale": {
					"type": "minecraft:clamped_normal",
					"mean": 0.5,
					"deviation": 0.1,
					"min": 0.0,
					"max": 1.0
				},
				"max_column_radius_to_cave_height_ratio": 0.0,
				"stalactite_bluntness": 0.0,
				"stalagmite_bluntness": 0.0,
				"wind_speed": 0.0,
				"min_radius_for_wind": 0,
				"min_bluntness_for_wind": 0.0
			}
		}
	""".trimIndent()

	configuredFeature("constant_test", largeDripstone {
		heightScale = constant(2.0f)
	})

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:large_dripstone",
			"config": {
				"column_radius": 0,
				"height_scale": 2.0,
				"max_column_radius_to_cave_height_ratio": 0.0,
				"stalactite_bluntness": 0.0,
				"stalagmite_bluntness": 0.0,
				"wind_speed": 0.0,
				"min_radius_for_wind": 0,
				"min_bluntness_for_wind": 0.0
			}
		}
	""".trimIndent()

	configuredFeature("trapezoid_test", largeDripstone {
		heightScale = trapezoid(min = 0.0f, max = 1.0f, plateau = 0.5f)
	})

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:large_dripstone",
			"config": {
				"column_radius": 0,
				"height_scale": {
					"type": "minecraft:trapezoid",
					"min": 0.0,
					"max": 1.0,
					"plateau": 0.5
				},
				"max_column_radius_to_cave_height_ratio": 0.0,
				"stalactite_bluntness": 0.0,
				"stalagmite_bluntness": 0.0,
				"wind_speed": 0.0,
				"min_radius_for_wind": 0,
				"min_bluntness_for_wind": 0.0
			}
		}
	""".trimIndent()

	configuredFeature("uniform_test", largeDripstone {
		heightScale = uniform(minInclusive = 1.0f, maxExclusive = 5.0f)
	})

	configuredFeatures.last() assertsIs """
		{
			"type": "minecraft:large_dripstone",
			"config": {
				"column_radius": 0,
				"height_scale": {
					"type": "minecraft:uniform",
					"min_inclusive": 1.0,
					"max_exclusive": 5.0
				},
				"max_column_radius_to_cave_height_ratio": 0.0,
				"stalactite_bluntness": 0.0,
				"stalagmite_bluntness": 0.0,
				"wind_speed": 0.0,
				"min_radius_for_wind": 0,
				"min_bluntness_for_wind": 0.0
			}
		}
	""".trimIndent()
}

class FloatProviderTests : FunSpec({
	test("float providers") {
		testDataPack("floatProviders") {
			pretty()
			floatProviderTests()
		}.apply {
			assertGeneratorsGenerated()
			generate()
		}
	}
})
