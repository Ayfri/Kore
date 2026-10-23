package io.github.ayfri.kore.features.villagertrades

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import kotlinx.serialization.Serializable

/**
 * An item a villager requests in a [VillagerTrade], with a count that is rolled when the trade is offered.
 *
 * [count] is an int provider written as a bare number, a provider file ID or a typed provider, and defaults to 1.
 * [components] must match the traded item's components exactly, any item matches when it is unset.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/villager-trades
 * Minecraft Wiki: https://minecraft.wiki/w/Villager_trade_definition
 */
@Serializable
data class TradeCost(
	var components: Components? = null,
	var count: ContextIntProvider? = null,
	var id: ItemArgument,
)
