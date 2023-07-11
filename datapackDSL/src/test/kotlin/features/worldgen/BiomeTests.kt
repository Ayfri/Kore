package features.worldgen

import DataPack
import arguments.Color
import commands.locateBiome
import features.worldgen.biome.*
import features.worldgen.biome.types.*
import functions.load
import generated.Carvers
import generated.Entities
import generated.Features

fun DataPack.biomeTests() {
	val biome = biome("my_biome") {
		temperature = 0.8f
		downfall = 0.4f
		hasPrecipitation = true
		temperatureModifier = TemperatureModifier.NONE
		creatureSpawnProbability = null

		effects {
			skyColor = Color.RED
			fogColor = Color.BLUE
			waterColor = Color.GREEN
			waterFogColor = Color.YELLOW
			foliageColor = Color.PURPLE
			grassColor = Color.WHITE
		}
		spawners {
			creature {
				spawner(Entities.ZOMBIE) {
					weight = 1
					minCount = 1
					maxCount = 2
				}
			}
		}

		spawnCosts {
			spawnCost(Entities.ZOMBIE, 1f, 1f)
		}

		carvers {
			air(Carvers.CANYON)
			liquid {
				add(Carvers.NETHER_CAVE)
				add(Carvers.CAVE)
			}
		}

		features {
			rawGeneration = listOf(Features.ORE, Features.DISK)
		}
	}

	load {
		locateBiome(biome)
	}
}
