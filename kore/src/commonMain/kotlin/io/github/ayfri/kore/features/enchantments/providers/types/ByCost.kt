package io.github.ayfri.kore.features.enchantments.providers.types

import io.github.ayfri.kore.features.enchantments.providers.EnchantmentProviders
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Spends an enchanting power budget rolled from [cost] on the [enchantments], the way the enchanting table does.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_provider#by_cost
 *
 * @property enchantments The enchantments and enchantment tags the budget is spent on.
 * @property cost The enchanting power available.
 */
@Serializable
data class ByCost(
	var enchantments: InlinableList<EnchantmentOrTagArgument> = emptyList(),
	var cost: IntProvider = constant(0),
) : EnchantmentProviderType()

/**
 * Registers a `by_cost` provider named [fileName], spending a [cost] enchanting power budget on [enchantments].
 *
 * ```kotlin
 * enchantmentProviders {
 *     byCost("mob_spawn_equipment", Tags.Enchantment.ON_MOB_SPAWN_EQUIPMENT, cost = uniform(5, 25))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_provider#by_cost
 */
fun EnchantmentProviders.byCost(
	fileName: String,
	vararg enchantments: EnchantmentOrTagArgument,
	cost: IntProvider = constant(0),
	block: ByCost.() -> Unit = {},
) = register(fileName, ByCost(enchantments.toList(), cost).apply(block))
