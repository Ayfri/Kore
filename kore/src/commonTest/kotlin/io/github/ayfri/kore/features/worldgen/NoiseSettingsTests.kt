package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.noise.amplitudeModifiers
import io.github.ayfri.kore.features.worldgen.noise.noise
import io.github.ayfri.kore.features.worldgen.noisesettings.*
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.*
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions.*
import io.github.ayfri.kore.features.worldgen.verticalanchors.aboveBottom
import io.github.ayfri.kore.features.worldgen.verticalanchors.absolute
import io.github.ayfri.kore.features.worldgen.verticalanchors.belowTop
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.DensityFunctions
import io.github.ayfri.kore.generated.MaterialRules
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.noiseSettingsTests() {
	val myNoise = noise("my_noise") {
		baseOctave = 0
		amplitudeModifiers(1.0, 1.0)
	}

	noiseSettings("my_noise_settings") {
		seaLevel = 42
		disableMobGeneration = true
		legacyRandomSource = true

		aquifers {
			barrier(0.5)
			exclusion(DensityFunctions.Overworld.EROSION)
			fluidLevelFloodedness(1.5)
			fluidLevelSpread(2.5)
			lava(DensityFunctions.End.BASE_3D_NOISE)
			surfaceLevel(DensityFunctions.Overworld.PRELIMINARY_SURFACE_LEVEL)
		}

		oreVein(Blocks.COPPER_ORE, Blocks.RAW_COPPER_BLOCK, Blocks.GRANITE) {
			density(DensityFunctions.Overworld.OreVein.COPPER_DENSITY)
			fillerGap(DensityFunctions.Overworld.OreVein.GAP)
			richness(DensityFunctions.Overworld.OreVein.RICHNESS)
		}

		oreVein(Blocks.DEEPSLATE_IRON_ORE, Blocks.RAW_IRON_BLOCK, Blocks.TUFF, rawOreChance = 0.05f) {
			density(DensityFunctions.Overworld.OreVein.IRON_DENSITY)
			fillerGap(0.25)
			richness(0.75)
		}

		defaultBlock(Blocks.REDSTONE_LAMP) {
			this["lit"] = "true"
		}

		defaultFluid(Blocks.WATER) {
			this["level"] = "0"
		}

		noiseOptions(-64, 384, 1, 2)

		noiseRouter {
			continents(DensityFunctions.Overworld.CONTINENTS)
			depth(DensityFunctions.Overworld.DEPTH)
			erosion(DensityFunctions.Overworld.EROSION)
			finalDensity(DensityFunctions.Overworld.SLOPED_CHEESE)
			preliminarySurfaceLevel(4.5)
			ridges(DensityFunctions.Overworld.RIDGES)
			temperature(DensityFunctions.Overworld.BASE_3D_NOISE)
			vegetation(3.5)
		}

		spawnTargetPoint {
			this[DensityFunctions.Overworld.CONTINENTS] = -0.11..1.0
			this[DensityFunctions.Overworld.EROSION] = -1.0..1.0
			this[DensityFunctions.Overworld.RIDGES] = -1.0..-0.16
			this[DensityFunctions.Overworld.TEMPERATURE] = -1.0..1.0
			this[DensityFunctions.Overworld.VEGETATION] = -1.0..1.0
		}

		spawnTargetPoint {
			this[DensityFunctions.Overworld.CONTINENTS] = -0.11..1.0
			this[DensityFunctions.Overworld.RIDGES] = 0.16..1.0
			this[DensityFunctions.Overworld.DEPTH] = 0.5
		}

		materialRules {
			bandlands()
			block(Blocks.STONE)
			condition(AbovePreliminarySurface) { block(Blocks.ANDESITE) }
			condition(biomes(Biomes.BADLANDS)) { block(Blocks.RED_SAND) }
			condition(Hole) { block(Blocks.DIORITE) }
			condition(noiseThreshold(myNoise) { minThreshold = -0.5; maxThreshold = 0.5; is3d = true }) { block(Blocks.GRANITE) }
			condition(not(Steep)) { block(Blocks.GRAVEL) }
			condition(Steep) { block(Blocks.STONE) }
			condition(stoneDepth(Surface.FLOOR, offset = 1.0, addSurfaceDepth = true, secondaryDepthRange = 2)) { block(Blocks.DIRT) }
			condition(Temperature) { block(Blocks.SNOW_BLOCK) }
			condition(verticalGradient("bedrock_floor", aboveBottom(0), aboveBottom(5))) { block(Blocks.BEDROCK) }
			condition(water(offset = -1, surfaceDepthMultiplier = 2, addStoneDepth = true)) { block(Blocks.GRASS_BLOCK) }
			condition(yAbove(absolute(64), surfaceDepthMultiplier = 1, addStoneDepth = true)) { block(Blocks.CALCITE) }
			condition(yAbove(belowTop(8))) { block(Blocks.PACKED_ICE) }
			sequence {
				block(Blocks.DEEPSLATE)
			}
		}
	}

	noiseSettings.last() assertsIs """
		{
			"aquifers": {
				"barrier": 0.5,
				"exclusion": "minecraft:overworld/erosion",
				"fluid_level_floodedness": 1.5,
				"fluid_level_spread": 2.5,
				"lava": "minecraft:end/base_3d_noise",
				"surface_level": "minecraft:overworld/preliminary_surface_level"
			},
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
			"disable_mob_generation": true,
			"legacy_random_source": true,
			"material_rule": {
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
							"max_threshold": 0.5,
							"is3d": true
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
			},
			"noise": {
				"min_y": -64,
				"height": 384,
				"size_horizontal": 1,
				"size_vertical": 2
			},
			"noise_router": {
				"continents": "minecraft:overworld/continents",
				"depth": "minecraft:overworld/depth",
				"erosion": "minecraft:overworld/erosion",
				"final_density": "minecraft:overworld/sloped_cheese",
				"preliminary_surface_level": 4.5,
				"ridges": "minecraft:overworld/ridges",
				"temperature": "minecraft:overworld/base_3d_noise",
				"vegetation": 3.5
			},
			"ore_veins": [
				{
					"density": "minecraft:overworld/ore_vein/copper_density",
					"filler_block": {
						"Name": "minecraft:granite"
					},
					"filler_gap": "minecraft:overworld/ore_vein/gap",
					"ore_block": {
						"Name": "minecraft:copper_ore"
					},
					"raw_ore_block": {
						"Name": "minecraft:raw_copper_block"
					},
					"raw_ore_chance": 0.02,
					"richness": "minecraft:overworld/ore_vein/richness"
				},
				{
					"density": "minecraft:overworld/ore_vein/iron_density",
					"filler_block": {
						"Name": "minecraft:tuff"
					},
					"filler_gap": 0.25,
					"ore_block": {
						"Name": "minecraft:deepslate_iron_ore"
					},
					"raw_ore_block": {
						"Name": "minecraft:raw_iron_block"
					},
					"raw_ore_chance": 0.05,
					"richness": 0.75
				}
			],
			"sea_level": 42,
			"spawn_target": [
				{
					"minecraft:overworld/continents": [
						-0.11,
						1.0
					],
					"minecraft:overworld/erosion": [
						-1.0,
						1.0
					],
					"minecraft:overworld/ridges": [
						-1.0,
						-0.16
					],
					"minecraft:overworld/temperature": [
						-1.0,
						1.0
					],
					"minecraft:overworld/vegetation": [
						-1.0,
						1.0
					]
				},
				{
					"minecraft:overworld/continents": [
						-0.11,
						1.0
					],
					"minecraft:overworld/ridges": [
						0.16,
						1.0
					],
					"minecraft:overworld/depth": 0.5
				}
			]
		}
	""".trimIndent()
}

fun DataPack.materialRuleTests() {
	val highlands = materialCondition("highlands") { yAbove(absolute(120)) }

	materialConditions.last() assertsIs """
		{
			"type": "minecraft:y_above",
			"anchor": {
				"absolute": 120
			},
			"surface_depth_multiplier": 0,
			"add_stone_depth": false
		}
	""".trimIndent()

	val peaks = materialRule("peaks") {
		condition(reference(highlands)) { block(Blocks.SNOW_BLOCK) }
	}

	materialRules.last() assertsIs """
		{
			"type": "minecraft:condition",
			"if_true": "noiseSettings:highlands",
			"then_run": {
				"type": "minecraft:block",
				"result_state": {
					"Name": "minecraft:snow_block"
				}
			}
		}
	""".trimIndent()

	materialRule("overworld") {
		condition(not(reference(highlands))) { rule(MaterialRules.Overworld.SURFACE) }
		rule(peaks)
	}

	materialRules.last() assertsIs """
		{
			"type": "minecraft:sequence",
			"sequence": [
				{
					"type": "minecraft:condition",
					"if_true": {
						"type": "minecraft:not",
						"invert": "noiseSettings:highlands"
					},
					"then_run": "minecraft:overworld/surface"
				},
				"noiseSettings:peaks"
			]
		}
	""".trimIndent()

	noiseSettings("referenced_rule") {
		materialRule(MaterialRules.Overworld)
	}

	jsonEncoder.encodeToString(MaterialRule.serializer(), noiseSettings.last().materialRule) assertsIs "\"minecraft:overworld\""
}

class NoiseSettingsTests : FunSpec({
	test("noise settings") {
		dataPack("noiseSettings") {
			pretty()
			noiseSettingsTests()
		}
	}

	test("material rules") {
		dataPack("noiseSettings") {
			pretty()
			materialRuleTests()
		}
	}
})
