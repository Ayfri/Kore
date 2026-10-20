package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.exportAsStrings
import io.github.ayfri.kore.features.tags.carverTag
import io.github.ayfri.kore.features.worldgen.carver.canyon
import io.github.ayfri.kore.features.worldgen.carver.carvers
import io.github.ayfri.kore.features.worldgen.carver.cave
import io.github.ayfri.kore.features.worldgen.carver.shape
import io.github.ayfri.kore.features.worldgen.floatproviders.clampedNormal
import io.github.ayfri.kore.features.worldgen.floatproviders.constant
import io.github.ayfri.kore.features.worldgen.floatproviders.trapezoid
import io.github.ayfri.kore.features.worldgen.floatproviders.uniform
import io.github.ayfri.kore.features.worldgen.heightproviders.constantAboveBottom
import io.github.ayfri.kore.features.worldgen.heightproviders.uniformHeightProvider
import io.github.ayfri.kore.features.worldgen.intproviders.uniform
import io.github.ayfri.kore.features.worldgen.verticalanchors.aboveBottom
import io.github.ayfri.kore.features.worldgen.verticalanchors.absolute
import io.github.ayfri.kore.generated.Carvers
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.carverTests() {
	carvers {
		cave("default_cave")

		cave("my_cave") {
			count = uniform(0, 14)
			floorLevel = uniform(-1.0f, -0.4f)
			horizontalRadiusMultiplier = uniform(0.7f, 1.4f)
			probability = 0.15
			roomVerticalRadiusMultiplier = uniform(0.1f, 0.9f)
			startVerticalRadiusMultiplier = constant(1.5f)
			thickness = trapezoid(0.0f, 3.0f, 1.0f)
			verticalRadiusMultiplier = uniform(0.8f, 1.3f)
			weirdThicknessBias = true
			y = uniformHeightProvider(aboveBottom(8), absolute(180))
		}

		canyon("my_canyon") {
			probability = 0.02
			verticalRotation = clampedNormal(0.0f, 1.0f, -1.0f, 1.0f)
			y = constantAboveBottom(65)

			shape {
				distanceFactor = constant(0.5f)
				horizontalRadiusFactor = uniform(0.75f, 1.0f)
				thickness = trapezoid(0.0f, 6.0f, 2.0f)
				verticalRadiusCenterFactor = 0.0f
				verticalRadiusDefaultFactor = 1.0f
				widthSmoothness = 3
				yScale = constant(3.0f)
			}
		}
	}

	carvers[0] assertsIs """
		{
			"type": "minecraft:cave",
			"count": 1,
			"floor_level": 0.0,
			"horizontal_radius_multiplier": 1.0,
			"probability": 0.1,
			"room_vertical_radius_multiplier": 1.0,
			"thickness": 1.0,
			"vertical_radius_multiplier": 1.0,
			"y": {
				"absolute": 0
			}
		}
	""".trimIndent()

	carvers[1] assertsIs """
		{
			"type": "minecraft:cave",
			"count": {
				"type": "minecraft:uniform",
				"min_inclusive": 0,
				"max_inclusive": 14
			},
			"floor_level": {
				"type": "minecraft:uniform",
				"min_inclusive": -1.0,
				"max_exclusive": -0.4
			},
			"horizontal_radius_multiplier": {
				"type": "minecraft:uniform",
				"min_inclusive": 0.7,
				"max_exclusive": 1.4
			},
			"probability": 0.15,
			"room_vertical_radius_multiplier": {
				"type": "minecraft:uniform",
				"min_inclusive": 0.1,
				"max_exclusive": 0.9
			},
			"start_vertical_radius_multiplier": 1.5,
			"thickness": {
				"type": "minecraft:trapezoid",
				"min": 0.0,
				"max": 3.0,
				"plateau": 1.0
			},
			"vertical_radius_multiplier": {
				"type": "minecraft:uniform",
				"min_inclusive": 0.8,
				"max_exclusive": 1.3
			},
			"weird_thickness_bias": true,
			"y": {
				"type": "minecraft:uniform",
				"min_inclusive": {
					"above_bottom": 8
				},
				"max_inclusive": {
					"absolute": 180
				}
			}
		}
	""".trimIndent()

	carvers[2] assertsIs """
		{
			"type": "minecraft:canyon",
			"probability": 0.02,
			"shape": {
				"distance_factor": 0.5,
				"horizontal_radius_factor": {
					"type": "minecraft:uniform",
					"min_inclusive": 0.75,
					"max_exclusive": 1.0
				},
				"thickness": {
					"type": "minecraft:trapezoid",
					"min": 0.0,
					"max": 6.0,
					"plateau": 2.0
				},
				"vertical_radius_center_factor": 0.0,
				"vertical_radius_default_factor": 1.0,
				"width_smoothness": 3,
				"y_scale": 3.0
			},
			"vertical_rotation": {
				"type": "minecraft:clamped_normal",
				"mean": 0.0,
				"deviation": 1.0,
				"min": -1.0,
				"max": 1.0
			},
			"y": {
				"above_bottom": 65
			}
		}
	""".trimIndent()

	carverTag("my_carvers") {
		this += Carvers.CAVE
	}

	carvers.last().resourceFolder assertsIs "worldgen/carver"

	val paths = exportAsStrings().keys
	("data/carver/worldgen/carver/default_cave.json" in paths) assertsIs true
	("data/carver/tags/worldgen/carver/my_carvers.json" in paths) assertsIs true
	paths.none { "configured_carver" in it } assertsIs true
}

class CarverTests : FunSpec({
	test("carver") {
		dataPack("carver") {
			pretty()
			carverTests()
		}
	}
})
