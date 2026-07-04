package io.github.ayfri.kore.features.enchantments.providers.types

import io.github.ayfri.kore.features.enchantments.providers.EnchantmentProvider
import io.github.ayfri.kore.features.enchantments.providers.EnchantmentProviders
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.VillagerProfessionArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import kotlinx.serialization.Serializable

@Serializable
data class Single(
	var enchantment: EnchantmentOrTagArgument,
	var level: IntProvider = constant(0),
) : EnchantmentProviderType()

fun EnchantmentProviders.singleEnchantmentProvider(
	biome: BiomeArgument,
	profession: VillagerProfessionArgument,
	villagerLevel: Int,
	enchantment: EnchantmentOrTagArgument,
	level: IntProvider = constant(0),
	block: Single.() -> Unit = {},
) {
	dp.enchantmentProviders += EnchantmentProvider(
		biome,
		profession,
		villagerLevel,
		Single(enchantment, level).apply(block)
	)
}
