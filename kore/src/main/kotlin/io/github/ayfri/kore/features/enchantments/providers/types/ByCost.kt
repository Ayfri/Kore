package io.github.ayfri.kore.features.enchantments.providers.types

import io.github.ayfri.kore.arguments.types.EnchantmentOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.VillagerProfessionArgument
import io.github.ayfri.kore.features.enchantments.providers.EnchantmentProvider
import io.github.ayfri.kore.features.enchantments.providers.EnchantmentProviders
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class ByCost(
	var enchantments: InlinableList<EnchantmentOrTagArgument> = emptyList(),
	var cost: IntProvider = constant(0),
) : EnchantmentProviderType()

fun EnchantmentProviders.byCostEnchantmentProvider(
	biome: BiomeArgument,
	profession: VillagerProfessionArgument,
	villagerLevel: Int,
	vararg enchantments: EnchantmentOrTagArgument,
	cost: IntProvider = constant(0),
	block: ByCost.() -> Unit = {},
) {
	dp.enchantmentProviders += EnchantmentProvider(
		biome,
		profession,
		villagerLevel,
		ByCost(enchantments.toList(), cost).apply(block)
	)
}
