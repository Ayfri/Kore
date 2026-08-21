package io.github.ayfri.kore.features.enchantments.providers.types

import io.github.ayfri.kore.features.enchantments.providers.EnchantmentProvidersScope
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Spends an enchanting power budget on the [enchantments], scaling it with the regional difficulty from [minCost] up
 * to `minCost + maxCostSpan`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_provider#by_cost_with_difficulty
 *
 * @property enchantments The enchantments and enchantment tags the budget is spent on.
 * @property minCost The enchanting power available at the lowest regional difficulty.
 * @property maxCostSpan The extra enchanting power available at the highest regional difficulty.
 */
@Serializable
data class ByCostWithDifficulty(
	var enchantments: InlinableList<EnchantmentOrTagArgument> = emptyList(),
	var minCost: Int = 1,
	var maxCostSpan: Int = 0,
) : EnchantmentProviderType()

/**
 * Registers a `by_cost_with_difficulty` provider named [fileName], spending an enchanting power budget scaled with
 * the regional difficulty on [enchantments].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_provider#by_cost_with_difficulty
 */
fun EnchantmentProvidersScope.byCostWithDifficulty(
	fileName: String,
	vararg enchantments: EnchantmentOrTagArgument,
	minCost: Int = 1,
	maxCostSpan: Int = 0,
	block: ByCostWithDifficulty.() -> Unit = {},
) = register(fileName, ByCostWithDifficulty(enchantments.toList(), minCost, maxCostSpan).apply(block))
