package io.github.ayfri.kore.features.villagertrades

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.EnchantmentOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.VillagerTradeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven villager trade definition.
 *
 * Defines a single trade offer: the item(s) the villager wants, the item it gives,
 * optional item modifier functions applied to the result, use limits, and XP rewards.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/villager-trades
 * Minecraft Wiki: https://minecraft.wiki/w/Villager_trade_definition
 */
@Serializable
data class VillagerTrade(
	@Transient
	override var fileName: String = "villager_trade",
	var additionalWants: ItemStack? = null,
	var doubleTradePriceEnchantments: InlinableList<EnchantmentOrTagArgument>? = null,
	var givenItemModifiers: ItemModifierAsList? = null,
	var gives: ItemStack,
	var maxUses: NumberProvider? = null,
	var merchantPredicate: PredicateCondition? = null,
	var reputationDiscount: NumberProvider? = null,
	var wants: ItemStack,
	var xp: NumberProvider? = null,
) : Generator("villager_trade") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a villager trade using a builder block.
 *
 * Call [VillagerTrade.wants] and [VillagerTrade.gives] inside the block to set the required items.
 * Produces `data/<namespace>/villager_trade/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/villager-trades
 * Minecraft Wiki: https://minecraft.wiki/w/Villager_trade_definition
 */
fun DataPack.villagerTrade(
	fileName: String = "villager_trade",
	gives: ItemStack = itemStack(Items.AIR),
	wants: ItemStack = itemStack(Items.AIR),
	init: VillagerTrade.() -> Unit,
): VillagerTradeArgument {
	val trade = VillagerTrade(fileName = fileName, gives = gives, wants = wants).apply(init)
	villagerTrades += trade
	return VillagerTradeArgument(fileName, trade.namespace ?: name)
}

/** Set an optional second item the villager requests alongside [wants]. */
fun VillagerTrade.additionalWants(id: ItemArgument, count: Short? = null, block: Components.() -> Unit = {}) {
	additionalWants = itemStack(id, count, block)
}

/** Set item modifier functions applied to the output item. */
fun VillagerTrade.givenItemModifiers(block: ItemModifier.() -> Unit) {
	givenItemModifiers = ItemModifierAsList().apply(block)
}

/** Set the item the villager offers in return. */
fun VillagerTrade.gives(id: ItemArgument, count: Short? = null, block: Components.() -> Unit = {}) {
	gives = itemStack(id, count, block)
}

/** Set the item the villager requests as the primary input. */
fun VillagerTrade.wants(id: ItemArgument, count: Short? = null, block: Components.() -> Unit = {}) {
	wants = itemStack(id, count, block)
}
