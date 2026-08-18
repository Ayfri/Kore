package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.configuredcarver.canyon
import io.github.ayfri.kore.features.worldgen.configuredcarver.cave
import io.github.ayfri.kore.features.worldgen.configuredcarver.configuredCarvers
import io.github.ayfri.kore.features.worldgen.configuredcarver.debugSettings
import io.github.ayfri.kore.features.worldgen.configuredcarver.netherCave
import io.github.ayfri.kore.features.worldgen.configuredcarver.replaceable
import io.github.ayfri.kore.features.worldgen.configuredcarver.shape
import io.github.ayfri.kore.features.worldgen.floatproviders.clampedNormal
import io.github.ayfri.kore.features.worldgen.floatproviders.constant
import io.github.ayfri.kore.features.worldgen.floatproviders.trapezoid
import io.github.ayfri.kore.features.worldgen.floatproviders.uniform
import io.github.ayfri.kore.features.worldgen.heightproviders.constantAboveBottom
import io.github.ayfri.kore.features.worldgen.heightproviders.uniformHeightProvider
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.aboveBottom
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.absolute
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.belowTop
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.configuredCarverTests() {
	configuredCarvers {
		cave("default_cave")

		cave("my_cave") {
			probability = 0.15
			y = uniformHeightProvider(aboveBottom(8), absolute(180))
			yScale = uniform(0.1f, 0.9f)
			lavaLevel = absolute(-54)
			replaceable(Blocks.STONE, Blocks.DIRT, Tags.Block.BASE_STONE_OVERWORLD)
			horizontalRadiusMultiplier = uniform(0.7f, 1.4f)
			verticalRadiusMultiplier = uniform(0.8f, 1.3f)
			floorLevel = uniform(-1.0f, -0.4f)
		}

		netherCave("my_nether_cave") {
			probability = 0.2
			y = uniformHeightProvider(aboveBottom(1), belowTop(1))
			lavaLevel = aboveBottom(31)
			floorLevel = constant(-0.7f)
		}

		canyon("my_canyon") {
			probability = 0.02
			y = constantAboveBottom(65)
			yScale = constant(3.0f)
			lavaLevel = belowTop(10)
			replaceable(Blocks.STONE, Blocks.DEEPSLATE)
			verticalRotation = clampedNormal(0.0f, 1.0f, -1.0f, 1.0f)

			shape {
				distanceFactor = constant(0.5f)
				thickness = trapezoid(0.0f, 6.0f, 2.0f)
				widthSmoothness = 3
				horizontalRadiusFactor = uniform(0.75f, 1.0f)
				verticalRadiusDefaultFactor = 1.0f
				verticalRadiusCenterFactor = 0.0f
			}

			debugSettings {
				debugMode = true
				airState = blockState(Blocks.ACACIA_BUTTON, "face" to "floor")
				waterState = blockState(Blocks.BLUE_STAINED_GLASS)
				lavaState = blockState(Blocks.ORANGE_STAINED_GLASS)
				barrierState = blockState(Blocks.BARRIER)
			}
		}
	}

	configuredCarvers[0] assertsIs """
		{
			"type": "minecraft:cave",
			"config": {
				"probability": 0.1,
				"y": {
					"absolute": 0
				},
				"yScale": 1.0,
				"lava_level": {
					"absolute": -54
				},
				"replaceable": "#minecraft:overworld_carver_replaceables",
				"horizontal_radius_multiplier": 1.0,
				"vertical_radius_multiplier": 1.0,
				"floor_level": 0.0
			}
		}
	""".trimIndent()

	configuredCarvers[1] assertsIs """
		{
			"type": "minecraft:cave",
			"config": {
				"probability": 0.15,
				"y": {
					"type": "minecraft:uniform",
					"min_inclusive": {
						"above_bottom": 8
					},
					"max_inclusive": {
						"absolute": 180
					}
				},
				"yScale": {
					"type": "minecraft:uniform",
					"min_inclusive": 0.1,
					"max_exclusive": 0.9
				},
				"lava_level": {
					"absolute": -54
				},
				"replaceable": [
					"minecraft:stone",
					"minecraft:dirt",
					"#minecraft:base_stone_overworld"
				],
				"horizontal_radius_multiplier": {
					"type": "minecraft:uniform",
					"min_inclusive": 0.7,
					"max_exclusive": 1.4
				},
				"vertical_radius_multiplier": {
					"type": "minecraft:uniform",
					"min_inclusive": 0.8,
					"max_exclusive": 1.3
				},
				"floor_level": {
					"type": "minecraft:uniform",
					"min_inclusive": -1.0,
					"max_exclusive": -0.4
				}
			}
		}
	""".trimIndent()

	configuredCarvers[2] assertsIs """
		{
			"type": "minecraft:nether_cave",
			"config": {
				"probability": 0.2,
				"y": {
					"type": "minecraft:uniform",
					"min_inclusive": {
						"above_bottom": 1
					},
					"max_inclusive": {
						"below_top": 1
					}
				},
				"yScale": 1.0,
				"lava_level": {
					"above_bottom": 31
				},
				"replaceable": "#minecraft:nether_carver_replaceables",
				"horizontal_radius_multiplier": 1.0,
				"vertical_radius_multiplier": 1.0,
				"floor_level": -0.7
			}
		}
	""".trimIndent()

	configuredCarvers[3] assertsIs """
		{
			"type": "minecraft:canyon",
			"config": {
				"probability": 0.02,
				"y": {
					"above_bottom": 65
				},
				"yScale": 3.0,
				"lava_level": {
					"below_top": 10
				},
				"replaceable": [
					"minecraft:stone",
					"minecraft:deepslate"
				],
				"debug_settings": {
					"debug_mode": true,
					"air_state": {
						"Name": "minecraft:acacia_button",
						"Properties": {
							"face": "floor"
						}
					},
					"water_state": {
						"Name": "minecraft:blue_stained_glass"
					},
					"lava_state": {
						"Name": "minecraft:orange_stained_glass"
					},
					"barrier_state": {
						"Name": "minecraft:barrier"
					}
				},
				"vertical_rotation": {
					"type": "minecraft:clamped_normal",
					"mean": 0.0,
					"deviation": 1.0,
					"min": -1.0,
					"max": 1.0
				},
				"shape": {
					"distance_factor": 0.5,
					"thickness": {
						"type": "minecraft:trapezoid",
						"min": 0.0,
						"max": 6.0,
						"plateau": 2.0
					},
					"width_smoothness": 3,
					"horizontal_radius_factor": {
						"type": "minecraft:uniform",
						"min_inclusive": 0.75,
						"max_exclusive": 1.0
					},
					"vertical_radius_default_factor": 1.0,
					"vertical_radius_center_factor": 0.0
				}
			}
		}
	""".trimIndent()
}

class ConfiguredCarverTests : FunSpec({
	test("configured carver") {
		dataPack("configuredCarver") {
			pretty()
			configuredCarverTests()
		}
	}
})
