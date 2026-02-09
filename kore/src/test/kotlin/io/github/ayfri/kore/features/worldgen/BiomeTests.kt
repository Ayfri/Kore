package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.rgb
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.locateBiome
import io.github.ayfri.kore.features.worldgen.biome.*
import io.github.ayfri.kore.features.worldgen.biome.types.*
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.fogColor
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.skyColor
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Carvers
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.PlacedFeatures

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

		carvers {
			air(Carvers.CANYON)
			liquid {
				add(Carvers.NETHER_CAVE)
				add(Carvers.CAVE)
			}
		}

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
			"carvers": {
				"air": [
					"minecraft:canyon"
				],
				"liquid": [
					"minecraft:nether_cave",
					"minecraft:cave"
				]
			},
			"features": [
				[
					"minecraft:ore_diamond",
					"minecraft:ore_diamond_large"
				]
			]
		}
	""".trimIndent()

	load {
		locateBiome(biome)
	}
}
