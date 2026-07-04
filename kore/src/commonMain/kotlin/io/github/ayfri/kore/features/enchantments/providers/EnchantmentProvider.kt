package io.github.ayfri.kore.features.enchantments.providers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.enchantments.providers.types.EnchantmentProviderType
import io.github.ayfri.kore.generated.arguments.types.VillagerProfessionArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

private fun String.removeNamespace() = replace(Regex("[a-z_-]+:"), "")

/**
 * Data-driven enchantment provider definition.
 *
 * Enchantment providers are used to source one or more enchantments for use in specific scenarios,
 * such as mobs spawning with enchanted equipment.
 *
 * JSON format reference: https://minecraft.wiki/w/Enchantment_provider
 */
@Serializable
data class EnchantmentProvider(
	@Transient
	override var fileName: String = "enchantment_provider",
	/** The type of enchantment provider. */
	var type: EnchantmentProviderType,
) : Generator("trades") {
	constructor(biome: BiomeArgument, profession: VillagerProfessionArgument, villagerLevel: Int, type: EnchantmentProviderType) : this(
		fileName = "${biome.asId().removeNamespace()}_${profession.asId().removeNamespace()}_$villagerLevel",
		type = type
	)

	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(type)
}

/**
 * The builder for [EnchantmentProvider]s.
 */
val DataPack.enchantmentProvidersBuilder get() = EnchantmentProviders(this)

/**
 * Register [EnchantmentProvider] in this [DataPack].
 */
fun DataPack.enchantmentProviders(block: EnchantmentProviders.() -> Unit) = enchantmentProvidersBuilder.apply(block)
