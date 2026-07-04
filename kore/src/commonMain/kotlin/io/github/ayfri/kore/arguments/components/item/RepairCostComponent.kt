package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:repair_cost` item component, which adds an extra XP cost when repairing this item in an anvil.
 *
 * Serializes as the integer cost directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#repair_cost
 */
@Serializable(with = RepairCostComponent.Companion.RepairCostComponentSerializer::class)
data class RepairCostComponent(var repairCost: Int) : Component() {
	companion object {
		data object RepairCostComponentSerializer : InlineAutoSerializer<RepairCostComponent, Int>(
			serializer<Int>(),
			RepairCostComponent::repairCost,
			::RepairCostComponent
		)
	}
}

/** Adds an extra XP cost when repairing this item in an anvil. */
fun ComponentsScope.repairCost(repairCost: Int) = apply { this[ItemComponentTypes.REPAIR_COST] = RepairCostComponent(repairCost) }
