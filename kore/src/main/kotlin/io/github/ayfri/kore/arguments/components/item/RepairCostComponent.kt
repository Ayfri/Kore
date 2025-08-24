package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RepairCostComponent.Companion.RepairCostComponentSerializer::class)
data class RepairCostComponent(var repairCost: Int) : Component() {
	companion object {
		data object RepairCostComponentSerializer : InlineAutoSerializer<RepairCostComponent>(RepairCostComponent::class)
	}
}

fun ComponentsScope.repairCost(repairCost: Int) = apply { this[ItemComponentTypes.REPAIR_COST] = RepairCostComponent(repairCost) }
