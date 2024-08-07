package io.github.ayfri.kore.features.enchantment.providers.types

import io.github.ayfri.kore.arguments.types.EnchantmentOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.VillagerProfessionArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.BiomeArgument
import io.github.ayfri.kore.features.enchantment.providers.EnchantmentProvider
import io.github.ayfri.kore.features.enchantment.providers.EnchantmentProviders
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class ByCostWithDifficulty(
	var enchantments: InlinableList<EnchantmentOrTagArgument> = emptyList(),
	var minCost: Int,
	var maxCostSpan: Int,
) : EnchantmentProviderType()

fun EnchantmentProviders.byCostWithDifficultyEnchantmentProvider(
	biome: BiomeArgument,
	profession: VillagerProfessionArgument,
	villagerLevel: Int,
	vararg enchantments: EnchantmentOrTagArgument,
	minCost: Int = 1,
	maxCostSpan: Int = 0,
	block: ByCostWithDifficulty.() -> Unit = {},
) {
	dp.enchantmentProviders += EnchantmentProvider(
		biome,
		profession,
		villagerLevel,
		ByCostWithDifficulty(enchantments.toList(), minCost, maxCostSpan).apply(block)
	)
}
