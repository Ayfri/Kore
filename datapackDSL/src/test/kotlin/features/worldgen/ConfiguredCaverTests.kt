package features.worldgen

import DataPack
import data.block.blockState
import data.block.properties
import features.worldgen.configuredcarver.canyonConfig
import features.worldgen.configuredcarver.configuredCarver
import features.worldgen.configuredcarver.debugSettings
import features.worldgen.configuredcarver.shape
import features.worldgen.floatproviders.clampedNormal
import features.worldgen.floatproviders.constant
import features.worldgen.floatproviders.trapezoid
import features.worldgen.floatproviders.uniform
import features.worldgen.heightproviders.constantAboveBottom
import features.worldgen.noisesettings.rules.conditions.belowTop
import generated.Blocks
import utils.assertsIs

fun DataPack.configuredCarverTests() {
	configuredCarver("my_configured_carver", canyonConfig {
		probability = 0.5
		replaceable = listOf(Blocks.AIR)
		lavaLevel = belowTop(10)
		y = constantAboveBottom(65)
		verticalRotation = clampedNormal(0.0f, 1.0f, 0.0f, 1.0f)

		debugSettings {
			debugMode = true
			airState = blockState {
				name = Blocks.AIR

				properties {
					this["test"] = "test"
				}
			}
		}

		shape {
			distanceFactor = constant(0.5f)
			thickness = trapezoid(0.0f, 1.0f, 0.5f)
			widthSmoothness = 0.5f
			horizontalRadiusMultiplier = uniform(0.5f, 1.0f)
			verticalRadiusMultiplier = 0.5f
			floorLevel = 0.5f
		}
	})

	configuredCarvers.last() assertsIs """
		{
			"type": "minecraft:canyon",
			"config": {
				"probability": 0.5,
				"y": {
					"above_bottom": 65
				},
				"yScale": 0.0,
				"lava_level": {
					"below_top": 10
				},
				"replaceable": "minecraft:air",
				"debug_settings": {
					"debug_mode": true,
					"air_state": {
						"Name": "minecraft:air",
						"Properties": {
							"test": "test"
						}
					}
				},
				"vertical_rotation": {
					"type": "minecraft:clamped_normal",
					"value": {
						"mean": 0.0,
						"deviation": 1.0,
						"min": 0.0,
						"max": 1.0
					}
				},
				"shape": {
					"distance_factor": 0.5,
					"thickness": {
						"type": "minecraft:trapezoid",
						"value": {
							"min": 0.0,
							"max": 1.0,
							"plateau": 0.5
						}
					},
					"width_smoothness": 0.5,
					"horizontal_radius_multiplier": {
						"type": "minecraft:uniform",
						"value": {
							"min_inclusive": 0.5,
							"max_exclusive": 1.0
						}
					},
					"vertical_radius_multiplier": 0.5,
					"floor_level": 0.5
				}
			}
		}
	""".trimIndent()
}
