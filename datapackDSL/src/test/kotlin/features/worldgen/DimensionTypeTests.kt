package features.worldgen

import DataPack
import features.worldgen.intproviders.*

fun DataPack.dimensionTypeTests() {
	dimensionType("my_dimension") {
		height = 512
		logicalHeight = 512
		minY = 0
		monsterSpawnLightLevel = uniform(7, 15)
	}

	dimensionType("my_dimension2") {
		monsterSpawnLightLevel = constant(2)
	}

	dimensionType("my_dimension3") {
		monsterSpawnLightLevel = weightedList {
			this += entry(1, constant(2))
			add(entry(1, clampedNormal(0, 10, 5f, 2f)))
		}
	}
}
