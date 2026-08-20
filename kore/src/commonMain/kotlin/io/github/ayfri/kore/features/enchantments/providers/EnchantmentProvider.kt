package io.github.ayfri.kore.features.enchantments.providers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.enchantments.providers.types.EnchantmentProviderType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition picking the enchantments an item receives outside the enchanting table, such as the gear
 * mobs spawn with or the crossbow of a raid pillager.
 *
 * Produces `data/<namespace>/enchantment_provider/<fileName>.json`. A [fileName] holding slashes lands in
 * subfolders, the way vanilla groups its raid providers.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/enchantments
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_provider
 *
 * @property fileName The name of the generated file, slashes creating subfolders.
 * @property type How the provider picks its enchantments and their levels.
 */
@Serializable
data class EnchantmentProvider(
	@Transient
	override var fileName: String = "enchantment_provider",
	var type: EnchantmentProviderType,
) : Generator("enchantment_provider") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(type)
}

/** The builder registering [EnchantmentProvider]s in this datapack. */
val DataPack.enchantmentProvidersBuilder get() = EnchantmentProviders(this)

/**
 * Registers enchantment providers in this datapack.
 *
 * ```kotlin
 * enchantmentProviders {
 *     single("pillager_spawn_crossbow", Enchantments.PIERCING, uniform(1, 3))
 *     byCost("mob_spawn_equipment", Tags.Enchantment.ON_MOB_SPAWN_EQUIPMENT, cost = uniform(5, 25))
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/enchantments
 */
fun DataPack.enchantmentProviders(block: EnchantmentProviders.() -> Unit) = enchantmentProvidersBuilder.apply(block)
