package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AdditionalTradeCost.Companion.AdditionalTradeCostSerializer::class)
data class AdditionalTradeCost(var value: Int) : Component() {
	companion object {
		data object AdditionalTradeCostSerializer :
			InlineAutoSerializer<AdditionalTradeCost>(AdditionalTradeCost::class)
	}
}

fun ComponentsScope.additionalTradeCost(value: Int) =
	apply { this[ItemComponentTypes.ADDITIONAL_TRADE_COST] = AdditionalTradeCost(value) }
