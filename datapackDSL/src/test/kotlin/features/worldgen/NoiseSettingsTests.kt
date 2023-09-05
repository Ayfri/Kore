package features.worldgen

import DataPack
import assertions.assertsIs
import features.worldgen.dimension.biomesource.multinoise.multiNoiseBiomeSourceParameters
import features.worldgen.noise.noise
import features.worldgen.noisesettings.*
import features.worldgen.noisesettings.rules.Bandlands
import features.worldgen.noisesettings.rules.block
import features.worldgen.noisesettings.rules.condition
import features.worldgen.noisesettings.rules.conditions.biomes
import features.worldgen.noisesettings.rules.conditions.invert
import features.worldgen.noisesettings.rules.conditions.noiseThreshold
import features.worldgen.noisesettings.rules.surfaceRules
import generated.Biomes
import generated.Blocks
import generated.DensityFunctions

fun DataPack.noiseSettingsTests() {
	val myNoise = noise("my_noise") {
		firstOctave = 0
		amplitudes = listOf(1.0, 1.0)
	}

	noiseSettings("my_noise_settings") {
		defaultBlock(Blocks.REDSTONE_LAMP) {
			this["lit"] = "true"
		}

		defaultFluid(Blocks.WATER) {
			this["level"] = "0"
		}

		noiseRouter {
			barrier(0.5)
			lava(DensityFunctions.End.BASE_3D_NOISE)
		}

		noiseOptions(-64, 384, 1, 2)

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
			this += Bandlands
			this += block(Blocks.STONE)
			this += condition(
				invert {
					biomes {
						this += Biomes.BADLANDS
					}
				}
			) {
				this += block(Blocks.STONE)
				this += block(Blocks.DIRT)
				this += block(Blocks.GRAVEL)
				this += block(Blocks.GRANITE)
				this += block(Blocks.DIORITE)
				this += block(Blocks.ANDESITE)
			}

			this += condition(noiseThreshold(myNoise))
		}
	}

	// language=json
	noiseSettings.last() assertsIs """
		{
			"sea_level": 63,
			"disable_mob_generation": false,
			"aquifers_enabled": false,
			"ore_veins_enabled": false,
			"legacy_random_source": false,
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
				"fluid_level_floodedness": 0.0,
				"fluid_level_spread": 0.0,
				"lava": "minecraft:end/base_3d_noise",
				"temperature": 0.0,
				"vegetation": 0.0,
				"continents": 0.0,
				"erosion": 0.0,
				"depth": 0.0,
				"ridges": 0.0,
				"initial_density_without_jaggedness": 0.0,
				"final_density": 0.0,
				"vein_toggle": 0.0,
				"vein_ridged": 0.0,
				"vein_gap": 0.0
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
							"type": "minecraft:invert",
							"invert": {
								"type": "minecraft:biome",
								"biome_is": "minecraft:badlands"
							}
						},
						"then_run": {
							"type": "minecraft:sequence",
							"sequence": [
								{
									"type": "minecraft:block",
									"result_state": {
										"Name": "minecraft:stone"
									}
								},
								{
									"type": "minecraft:block",
									"result_state": {
										"Name": "minecraft:dirt"
									}
								},
								{
									"type": "minecraft:block",
									"result_state": {
										"Name": "minecraft:gravel"
									}
								},
								{
									"type": "minecraft:block",
									"result_state": {
										"Name": "minecraft:granite"
									}
								},
								{
									"type": "minecraft:block",
									"result_state": {
										"Name": "minecraft:diorite"
									}
								},
								{
									"type": "minecraft:block",
									"result_state": {
										"Name": "minecraft:andesite"
									}
								}
							]
						}
					},
					{
						"type": "minecraft:condition",
						"if_true": {
							"type": "minecraft:noise_threshold",
							"noise": "$name:my_noise",
							"min_threshold": 0.0,
							"max_threshold": 0.0
						},
						"then_run": {
							"type": "minecraft:sequence",
							"sequence": [
							]
						}
					}
				]
			}
		}
	""".trimIndent()
}
