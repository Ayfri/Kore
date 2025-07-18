package io.github.ayfri.kore.features.enchantments.providers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.enchantments.providers.types.EnchantmentProviderType
import io.github.ayfri.kore.generated.arguments.types.VillagerProfessionArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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
