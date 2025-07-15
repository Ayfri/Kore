package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.pigvariants.PigModel
import io.github.ayfri.kore.features.pigvariants.pigVariant
import io.github.ayfri.kore.generated.Biomes

fun DataPack.pigVariantTests() {
	pigVariant("test_pig_variant") {
		model = PigModel.COLD
		texture = "my_datapack:entity/cold_pig/pig"
		biome = listOf(Biomes.SNOWY_TAIGA, Biomes.SNOWY_PLAINS)
	}

	pigVariants.last() assertsIs """
		{
			"model": "cold",
			"texture": "my_datapack:entity/cold_pig/pig",
			"biome": [
				"minecraft:snowy_taiga",
				"minecraft:snowy_plains"
			]
		}
	""".trimIndent()

	pigVariant("normal_pig_variant") {
		model = PigModel.NORMAL
		texture = "my_datapack:entity/normal_pig/pig"
		biome = listOf(Biomes.PLAINS, Biomes.FOREST)
	}

	pigVariants.last() assertsIs """
		{
			"model": "normal",
			"texture": "my_datapack:entity/normal_pig/pig",
			"biome": [
				"minecraft:plains",
				"minecraft:forest"
			]
		}
	""".trimIndent()
}
