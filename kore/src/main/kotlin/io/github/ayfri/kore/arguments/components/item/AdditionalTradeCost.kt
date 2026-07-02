package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:additional_trade_cost` item component, which adds an extra emerald cost on top of the base price of a villager trade.
 *
 * Serializes as the integer cost directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#additional_trade_cost
 */
@Serializable(with = AdditionalTradeCost.Companion.AdditionalTradeCostSerializer::class)
data class AdditionalTradeCost(var value: Int) : Component() {
	companion object {
		data object AdditionalTradeCostSerializer :
			InlineAutoSerializer<AdditionalTradeCost, Int>(
				serializer<Int>(),
				AdditionalTradeCost::value,
				::AdditionalTradeCost
			)
	}
}

/** Sets an extra emerald cost added on top of the base price for villager trades. */
fun ComponentsScope.additionalTradeCost(value: Int) =
	apply { this[ItemComponentTypes.ADDITIONAL_TRADE_COST] = AdditionalTradeCost(value) }
