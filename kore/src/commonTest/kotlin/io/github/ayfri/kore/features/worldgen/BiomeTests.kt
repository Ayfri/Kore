package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.rgb
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.locateBiome
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.roundTrip
import io.github.ayfri.kore.features.worldgen.biome.*
import io.github.ayfri.kore.features.worldgen.biome.types.*
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.fogColor
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.skyColor
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.ConfiguredCarvers
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.PlacedFeatures
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.biomeTests() {
	val biome = biome("my_biome") {
		temperature = 0.8f
		downfall = 0.4f
		hasPrecipitation = true
		temperatureModifier = TemperatureModifier.NONE
		creatureSpawnProbability = null

		attributes {
			fogColor(rgb(255, 255, 0), EnvironmentAttributeModifier.ADD)
			skyColor(Color.RED)
		}

		effects {
			waterColor = Color.GREEN
			foliageColor = Color.PURPLE
			grassColor = Color.WHITE
			dryFoliageColor = Color.BLACK
		}
		spawners {
			creature {
				spawner(EntityTypes.ZOMBIE) {
					weight = 1
					minCount = 1
					maxCount = 2
				}
			}
		}

		spawnCosts {
			spawnCost(EntityTypes.ZOMBIE, 1f, 1f)
		}

		carvers(ConfiguredCarvers.CANYON, ConfiguredCarvers.NETHER_CAVE, ConfiguredCarvers.CAVE)

		features {
			rawGeneration = listOf(PlacedFeatures.ORE_DIAMOND, PlacedFeatures.ORE_DIAMOND_LARGE)
		}
	}
	biomes.last() assertsIs """
		{
			"attributes": {
				"minecraft:visual/fog_color": {
					"argument": 16776960,
					"modifier": "add"
				},
				"minecraft:visual/sky_color": 16733525
			},
			"temperature": 0.8,
			"downfall": 0.4,
			"has_precipitation": true,
			"temperature_modifier": "none",
			"effects": {
				"water_color": 5635925,
				"grass_color": 16777215,
				"foliage_color": 11141375,
				"dry_foliage_color": 0
			},
			"spawners": {
				"creature": [
					{
						"type": "minecraft:zombie",
						"weight": 1,
						"minCount": 1,
						"maxCount": 2
					}
				]
			},
			"spawn_costs": {
				"minecraft:zombie": {
					"energy_budget": 1.0,
					"charge": 1.0
				}
			},
			"carvers": [
				"minecraft:canyon",
				"minecraft:nether_cave",
				"minecraft:cave"
			],
			"features": [
				[
					"minecraft:ore_diamond",
					"minecraft:ore_diamond_large"
				],
				[],
				[],
				[],
				[],
				[],
				[],
				[],
				[],
				[],
				[]
			]
		}
	""".trimIndent()

	biome("frozen_biome") {
		temperature = -0.5f
		downfall = 0.8f
		hasPrecipitation = true
		temperatureModifier = TemperatureModifier.FROZEN
		creatureSpawnProbability = 0.1f

		effects {
			waterColor = Color.AQUA
			grassColor = Color.WHITE
			foliageColor = Color.GRAY
			dryFoliageColor = Color.GRAY
			grassColorModifier = GrassColorModifier.SWAMP
		}

		spawners {
			ambient {
				spawner(EntityTypes.BAT, weight = 10, minCount = 8, maxCount = 8)
			}
			axolotls {
				spawner(EntityTypes.AXOLOTL, weight = 10, minCount = 4, maxCount = 6)
			}
			monster {
				spawner(EntityTypes.ZOMBIE, weight = 100, minCount = 4, maxCount = 4)
				spawner(EntityTypes.SKELETON, weight = 100, minCount = 4, maxCount = 4)
			}
			undergroundWaterCreature {
				spawner(EntityTypes.GLOW_SQUID, weight = 10, minCount = 4, maxCount = 6)
			}
			waterAmbient {
				spawner(EntityTypes.COD, weight = 10, minCount = 3, maxCount = 6)
			}
			waterCreature {
				spawner(EntityTypes.SQUID, weight = 10, minCount = 1, maxCount = 4)
			}
		}

		spawnCosts {
			spawnCost(EntityTypes.ZOMBIE, 0.5f, 0.5f)
			spawnCost(EntityTypes.SKELETON, 0.5f, 0.5f)
		}

		features {
			rawGeneration = emptyList()
			lakes = listOf(PlacedFeatures.ORE_DIAMOND)
			undergroundOres = listOf(PlacedFeatures.ORE_DIAMOND, PlacedFeatures.ORE_DIAMOND_LARGE)
			vegetalDecoration = emptyList()
		}
	}

	biomes.last() assertsIs """
		{
			"temperature": -0.5,
			"downfall": 0.8,
			"has_precipitation": true,
			"temperature_modifier": "frozen",
			"creature_spawn_probability": 0.1,
			"effects": {
				"water_color": 5636095,
				"grass_color": 16777215,
				"foliage_color": 11184810,
				"dry_foliage_color": 11184810,
				"grass_color_modifier": "swamp"
			},
			"spawners": {
				"ambient": [
					{
						"type": "minecraft:bat",
						"weight": 10,
						"minCount": 8,
						"maxCount": 8
					}
				],
				"axolotls": [
					{
						"type": "minecraft:axolotl",
						"weight": 10,
						"minCount": 4,
						"maxCount": 6
					}
				],
				"monster": [
					{
						"type": "minecraft:zombie",
						"weight": 100,
						"minCount": 4,
						"maxCount": 4
					},
					{
						"type": "minecraft:skeleton",
						"weight": 100,
						"minCount": 4,
						"maxCount": 4
					}
				],
				"underground_water_creature": [
					{
						"type": "minecraft:glow_squid",
						"weight": 10,
						"minCount": 4,
						"maxCount": 6
					}
				],
				"water_ambient": [
					{
						"type": "minecraft:cod",
						"weight": 10,
						"minCount": 3,
						"maxCount": 6
					}
				],
				"water_creature": [
					{
						"type": "minecraft:squid",
						"weight": 10,
						"minCount": 1,
						"maxCount": 4
					}
				]
			},
			"spawn_costs": {
				"minecraft:zombie": {
					"energy_budget": 0.5,
					"charge": 0.5
				},
				"minecraft:skeleton": {
					"energy_budget": 0.5,
					"charge": 0.5
				}
			},
			"carvers": [],
			"features": [
				[],
				[
					"minecraft:ore_diamond"
				],
				[],
				[],
				[],
				[],
				[
					"minecraft:ore_diamond",
					"minecraft:ore_diamond_large"
				],
				[],
				[],
				[],
				[]
			]
		}
	""".trimIndent()

	load {
		locateBiome(biome)
	}

	roundTrip(biomes.first())
}

class BiomeTests : FunSpec({
	test("biome") {
		dataPack("biome") {
			pretty()
			biomeTests()
		}
	}
})
