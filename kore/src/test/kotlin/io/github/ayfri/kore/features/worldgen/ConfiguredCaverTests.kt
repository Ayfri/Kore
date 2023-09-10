package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.data.block.properties
import io.github.ayfri.kore.features.worldgen.configuredcarver.canyonConfig
import io.github.ayfri.kore.features.worldgen.configuredcarver.configuredCarver
import io.github.ayfri.kore.features.worldgen.configuredcarver.debugSettings
import io.github.ayfri.kore.features.worldgen.configuredcarver.shape
import io.github.ayfri.kore.features.worldgen.floatproviders.clampedNormal
import io.github.ayfri.kore.features.worldgen.floatproviders.constant
import io.github.ayfri.kore.features.worldgen.floatproviders.trapezoid
import io.github.ayfri.kore.features.worldgen.floatproviders.uniform
import io.github.ayfri.kore.features.worldgen.heightproviders.constantAboveBottom
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.belowTop
import io.github.ayfri.kore.generated.Blocks

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
