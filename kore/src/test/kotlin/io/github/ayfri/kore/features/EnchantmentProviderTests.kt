package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.enchantment.providers.enchantmentProviders
import io.github.ayfri.kore.features.enchantment.providers.types.byCostEnchantmentProvider
import io.github.ayfri.kore.features.enchantment.providers.types.byCostWithDifficultyEnchantmentProvider
import io.github.ayfri.kore.features.enchantment.providers.types.singleEnchantmentProvider
import io.github.ayfri.kore.features.worldgen.intproviders.uniform
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.VillagerProfessions

fun DataPack.enchantmentProviderTests() {
	val biome = Biomes.BADLANDS
	val profession = VillagerProfessions.NITWIT
	val enchantment = Enchantments.AQUA_AFFINITY

	val builtName = "badlands_nitwit"

	enchantmentProviders {
		byCostEnchantmentProvider(biome, profession, 1, enchantment, cost = uniform(1, 5))
		byCostWithDifficultyEnchantmentProvider(biome, profession, 2, enchantment, minCost = 0, maxCostSpan = 2)
		singleEnchantmentProvider(biome, profession, 3, enchantment)
	}

	enchantmentProviders.first().fileName assertsIs "${builtName}_${1}"
	enchantmentProviders.first() assertsIs """
		{
			"type": "minecraft:by_cost",
			"enchantments": "minecraft:aqua_affinity",
			"cost": {
				"type": "minecraft:uniform",
				"min_inclusive": 1,
				"max_inclusive": 5
			}
		}
	""".trimIndent()

	enchantmentProviders[1].fileName assertsIs "${builtName}_${2}"
	enchantmentProviders[1] assertsIs """
		{
			"type": "minecraft:by_cost_with_difficulty",
			"enchantments": "minecraft:aqua_affinity",
			"min_cost": 0,
			"max_cost_span": 2
		}
	""".trimIndent()

	enchantmentProviders[2].fileName assertsIs "${builtName}_${3}"
	enchantmentProviders[2] assertsIs """
		{
			"type": "minecraft:single",
			"enchantment": "minecraft:aqua_affinity",
			"level": 0
		}
	""".trimIndent()
}
