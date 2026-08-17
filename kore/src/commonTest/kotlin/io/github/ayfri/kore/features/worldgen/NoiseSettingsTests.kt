package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.dimension.biomesource.multinoise.multiNoiseBiomeSourceParameters
import io.github.ayfri.kore.features.worldgen.noise.noise
import io.github.ayfri.kore.features.worldgen.noisesettings.*
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.*
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.*
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.DensityFunctions
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.noiseSettingsTests() {
	val myNoise = noise("my_noise") {
		firstOctave = 0
		amplitudes = listOf(1.0, 1.0)
	}

	noiseSettings("my_noise_settings") {
		seaLevel = 42
		disableMobGeneration = true
		aquifersEnabled = true
		oreVeinsEnabled = true
		legacyRandomSource = true

		defaultBlock(Blocks.REDSTONE_LAMP) {
			this["lit"] = "true"
		}

		defaultFluid(Blocks.WATER) {
			this["level"] = "0"
		}

		noiseOptions(-64, 384, 1, 2)

		noiseRouter {
			barrier(0.5)
			fluidLevelFloodedness(1.5)
			fluidLevelSpread(2.5)
			lava(DensityFunctions.End.BASE_3D_NOISE)
			temperature(DensityFunctions.Overworld.BASE_3D_NOISE)
			vegetation(3.5)
			continents(DensityFunctions.Overworld.CONTINENTS)
			erosion(DensityFunctions.Overworld.EROSION)
			depth(DensityFunctions.Overworld.DEPTH)
			ridges(DensityFunctions.Overworld.RIDGES)
			preliminarySurfaceLevel(4.5)
			finalDensity(DensityFunctions.Overworld.SLOPED_CHEESE)
			veinToggle(5.5)
			veinRidged(6.5)
			veinGap(7.5)
		}

		spawnTarget += multiNoiseBiomeSourceParameters(
			0.0,
			0.0,
			0.0,
			0.0,
			0.0,
			0.0,
			0.0
		)

		surfaceRules {
			bandlands()
			block(Blocks.STONE)
			condition(AbovePreliminarySurface) { block(Blocks.ANDESITE) }
			condition(biomes(Biomes.BADLANDS)) { block(Blocks.RED_SAND) }
			condition(Hole) { block(Blocks.DIORITE) }
			condition(noiseThreshold(myNoise, minThreshold = -0.5, maxThreshold = 0.5)) { block(Blocks.GRANITE) }
			condition(not(Steep)) { block(Blocks.GRAVEL) }
			condition(Steep) { block(Blocks.STONE) }
			condition(stoneDepth(Surface.FLOOR, offset = 1.0, addSurfaceDepth = true, secondaryDepthRange = 2)) { block(Blocks.DIRT) }
			condition(Temperature) { block(Blocks.SNOW_BLOCK) }
			condition(verticalGradient("bedrock_floor", aboveBottom(0), aboveBottom(5))) { block(Blocks.BEDROCK) }
			condition(water(offset = -1, surfaceDepthMultiplier = 2, addStoneDepth = true)) { block(Blocks.GRASS_BLOCK) }
			condition(yAbove(absolute(64), surfaceDepthMultiplier = 1, addStoneDepth = true)) { block(Blocks.CALCITE) }
			condition(yAbove(belowTop(8))) { block(Blocks.PACKED_ICE) }
			noiseGradient(myNoise) {
				state(Blocks.STONE)
				empty()
			}
			sequence {
				block(Blocks.DEEPSLATE)
			}
		}
	}

	noiseSettings.last() assertsIs """
		{
			"sea_level": 42,
			"disable_mob_generation": true,
			"aquifers_enabled": true,
			"ore_veins_enabled": true,
			"legacy_random_source": true,
			"default_block": {
				"Name": "minecraft:redstone_lamp",
				"Properties": {
					"lit": "true"
				}
			},
			"default_fluid": {
				"Name": "minecraft:water",
				"Properties": {
					"level": "0"
				}
			},
			"noise": {
				"min_y": -64,
				"height": 384,
				"size_horizontal": 1,
				"size_vertical": 2
			},
			"noise_router": {
				"barrier": 0.5,
				"fluid_level_floodedness": 1.5,
				"fluid_level_spread": 2.5,
				"lava": "minecraft:end/base_3d_noise",
				"temperature": "minecraft:overworld/base_3d_noise",
				"vegetation": 3.5,
				"continents": "minecraft:overworld/continents",
				"erosion": "minecraft:overworld/erosion",
				"depth": "minecraft:overworld/depth",
				"ridges": "minecraft:overworld/ridges",
				"preliminary_surface_level": 4.5,
				"final_density": "minecraft:overworld/sloped_cheese",
				"vein_toggle": 5.5,
				"vein_ridged": 6.5,
				"vein_gap": 7.5
			},
			"spawn_target": [
				{
					"temperature": 0.0,
					"humidity": 0.0,
					"continentalness": 0.0,
					"erosion": 0.0,
					"weirdness": 0.0,
					"depth": 0.0,
					"offset": 0.0
				}
			],
			"surface_rule": {
				"type": "minecraft:sequence",
				"sequence": [
					{
						"type": "minecraft:bandlands"
					},
					{
						"type": "minecraft:block",
						"result_state": {
							"Name": "minecraft:stone"
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:above_preliminary_surface"
						},
						"then_run": {
							"type": "minecraft:block",
							"result_state": {
								"Name": "minecraft:andesite"
							}
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:biome",
							"biome_is": [
								"minecraft:badlands"
							]
						},
						"then_run": {
							"type": "minecraft:block",
							"result_state": {
								"Name": "minecraft:red_sand"
							}
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:hole"
						},
						"then_run": {
							"type": "minecraft:block",
							"result_state": {
								"Name": "minecraft:diorite"
							}
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:noise_threshold",
							"noise": "$name:my_noise",
							"min_threshold": -0.5,
							"max_threshold": 0.5
						},
						"then_run": {
							"type": "minecraft:block",
							"result_state": {
								"Name": "minecraft:granite"
							}
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:not",
							"invert": {
								"type": "minecraft:steep"
							}
						},
						"then_run": {
							"type": "minecraft:block",
							"result_state": {
								"Name": "minecraft:gravel"
							}
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:steep"
						},
						"then_run": {
							"type": "minecraft:block",
							"result_state": {
								"Name": "minecraft:stone"
							}
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:stone_depth",
							"offset": 1.0,
							"surface_type": "floor",
							"add_surface_depth": true,
							"secondary_depth_range": 2
						},
						"then_run": {
							"type": "minecraft:block",
							"result_state": {
								"Name": "minecraft:dirt"
							}
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:temperature"
						},
						"then_run": {
							"type": "minecraft:block",
							"result_state": {
								"Name": "minecraft:snow_block"
							}
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:vertical_gradient",
							"random_name": "bedrock_floor",
							"true_at_and_below": {
								"above_bottom": 0
							},
							"false_at_and_above": {
								"above_bottom": 5
							}
						},
						"then_run": {
							"type": "minecraft:block",
							"result_state": {
								"Name": "minecraft:bedrock"
							}
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:water",
							"offset": -1,
							"surface_depth_multiplier": 2,
							"add_stone_depth": true
						},
						"then_run": {
							"type": "minecraft:block",
							"result_state": {
								"Name": "minecraft:grass_block"
							}
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:y_above",
							"anchor": {
								"absolute": 64
							},
							"surface_depth_multiplier": 1,
							"add_stone_depth": true
						},
						"then_run": {
							"type": "minecraft:block",
							"result_state": {
								"Name": "minecraft:calcite"
							}
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:y_above",
							"anchor": {
								"below_top": 8
							},
							"surface_depth_multiplier": 0,
							"add_stone_depth": false
						},
						"then_run": {
							"type": "minecraft:block",
							"result_state": {
								"Name": "minecraft:packed_ice"
							}
						}
					},
					{
						"type": "minecraft:noise_gradient",
						"gradient": [
							{
								"state": {
									"Name": "minecraft:stone"
								}
							},
							{}
						],
						"noise": "$name:my_noise"
					},
					{
						"type": "minecraft:sequence",
						"sequence": [
							{
								"type": "minecraft:block",
								"result_state": {
									"Name": "minecraft:deepslate"
								}
							}
						]
					}
				]
			}
		}
	""".trimIndent()
}

class NoiseSettingsTests : FunSpec({
	test("noise settings") {
		dataPack("noiseSettings") {
			pretty()
			noiseSettingsTests()
		}
	}
})
