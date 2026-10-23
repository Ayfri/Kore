package io.github.ayfri.kore.features.villagertrades

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.features.predicates.providers.constant
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
 * an optional item modifier applied to the result, use limits, and XP rewards.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/villager-trades
 * Minecraft Wiki: https://minecraft.wiki/w/Villager_trade_definition
 */
@Serializable
data class VillagerTrade(
	@Transient
	override var fileName: String = "villager_trade",
	var additionalWants: TradeCost? = null,
	var doubleTradePriceEnchantments: InlinableList<EnchantmentOrTagArgument>? = null,
	var givenItemModifier: InlinableItemModifier? = null,
	var gives: ItemStack,
	var maxUses: ContextIntProvider? = null,
	var merchantPredicate: PredicateCondition? = null,
	var reputationDiscount: ContextFloatProvider? = null,
	var wants: TradeCost,
	var xp: ContextIntProvider? = null,
) : Generator("villager_trade"), NumberProviderScope {
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
	wants: TradeCost = TradeCost(id = Items.AIR),
	init: VillagerTrade.() -> Unit,
): VillagerTradeArgument {
	val trade = VillagerTrade(fileName = fileName, gives = gives, wants = wants).apply(init)
	villagerTrades += trade
	return VillagerTradeArgument(fileName, trade.namespace ?: name)
}

private fun tradeCost(id: ItemArgument, count: ContextIntProvider?, block: Components.() -> Unit) =
	TradeCost(Components().apply(block).takeIf { it.components.isNotEmpty() }, count, id)

/** Set an optional second item the villager requests alongside [VillagerTrade.wants], [count] being a fixed number. */
fun VillagerTrade.additionalWants(id: ItemArgument, count: Int? = null, block: Components.() -> Unit = {}) {
	additionalWants = tradeCost(id, count?.let { constant(it) }, block)
}

/** Set an optional second item the villager requests alongside [VillagerTrade.wants], [count] being an int provider. */
fun VillagerTrade.additionalWants(id: ItemArgument, count: ContextIntProvider, block: Components.() -> Unit = {}) {
	additionalWants = tradeCost(id, count, block)
}

/**
 * Set the item modifier applied to the output item: one function is written as an object, several as a list, and an
 * item modifier ID through `reference(...)`.
 */
fun VillagerTrade.givenItemModifier(block: ItemModifier.() -> Unit) {
	givenItemModifier = ItemModifier().apply(block)
}

/** Set the item the villager offers in return. */
fun VillagerTrade.gives(id: ItemArgument, count: Short? = null, block: Components.() -> Unit = {}) {
	gives = itemStack(id, count, block)
}

/**
 * Set the item the villager requests as the primary input, [count] being a fixed number.
 *
 * ```
 * wants(Items.WHEAT, count = 20)  // "wants": {"count": 20, "id": "minecraft:wheat"}
 * ```
 */
fun VillagerTrade.wants(id: ItemArgument, count: Int? = null, block: Components.() -> Unit = {}) {
	wants = tradeCost(id, count?.let { constant(it) }, block)
}

/**
 * Set the item the villager requests as the primary input, [count] being an int provider.
 *
 * ```
 * wants(Items.WHEAT, uniform(16, 24))  // "wants": {"count": {"type": "minecraft:uniform", ...}, "id": "minecraft:wheat"}
 * ```
 */
fun VillagerTrade.wants(id: ItemArgument, count: ContextIntProvider, block: Components.() -> Unit = {}) {
	wants = tradeCost(id, count, block)
}
