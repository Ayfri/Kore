package io.github.ayfri.kore.features.enchantments.providers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.features.enchantments.providers.types.EnchantmentProviderType
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.generated.arguments.types.VillagerProfessionArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument

/**
 * Receiver of the [EnchantmentProvider] builders, registering each of them in [dataPack].
 *
 * Obtained through `enchantmentProviders { }`.
 */
data class EnchantmentProvidersScope(val dataPack: DataPack) : IntProviderScope {
	/** Registers a provider named [fileName], slashes creating subfolders. */
	internal fun register(fileName: String, type: EnchantmentProviderType) {
		dataPack.enchantmentProviders += EnchantmentProvider(fileName, type)
	}
}

/**
 * Returns the file name the villager trade providers follow, `<biome>_<profession>_<villagerLevel>`.
 *
 * Namespaces are dropped, so `minecraft:badlands` and `minecraft:nitwit` at level 1 give `badlands_nitwit_1`.
 *
 * ```kotlin
 * enchantmentProviders {
 *     single(villagerTradeName(Biomes.BADLANDS, VillagerProfessions.NITWIT, 1), Enchantments.SHARPNESS)
 * }
 * ```
 */
fun EnchantmentProvidersScope.villagerTradeName(
	biome: BiomeArgument,
	profession: VillagerProfessionArgument,
	villagerLevel: Int,
) = "${biome.asId().substringAfter(':')}_${profession.asId().substringAfter(':')}_$villagerLevel"
