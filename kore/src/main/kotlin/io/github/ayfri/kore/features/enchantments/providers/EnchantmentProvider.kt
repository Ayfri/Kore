package io.github.ayfri.kore.features.enchantments.providers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.VillagerProfessionArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.BiomeArgument
import io.github.ayfri.kore.features.enchantments.providers.types.EnchantmentProviderType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

private fun String.removeNamespace() = replace(Regex("[a-z_-]+:"), "")

@Serializable
data class EnchantmentProvider(
	@Transient
	override var fileName: String = "enchantment_provider",
	var type: EnchantmentProviderType,
) : Generator("trades") {
	constructor(biome: BiomeArgument, profession: VillagerProfessionArgument, villagerLevel: Int, type: EnchantmentProviderType) : this(
		fileName = "${biome.asId().removeNamespace()}_${profession.asId().removeNamespace()}_$villagerLevel",
		type = type
	)

	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(type)
}

val DataPack.enchantmentProvidersBuilder get() = EnchantmentProviders(this)
fun DataPack.enchantmentProviders(block: EnchantmentProviders.() -> Unit) = enchantmentProvidersBuilder.apply(block)
