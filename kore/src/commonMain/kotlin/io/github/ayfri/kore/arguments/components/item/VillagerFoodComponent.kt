package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:villager_food` item component, which makes villagers eat the item to restore [nutrition] hunger.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#villager_food
 */
@Serializable
data class VillagerFoodComponent(var nutrition: Int) : Component()

/**
 * Makes villagers eat the item, restoring [nutrition] hunger (at least 1).
 *
 * ```
 * villagerFood(2)
 * ```
 */
fun ComponentsScope.villagerFood(nutrition: Int) = apply {
	require(nutrition >= 1) { "Nutrition must be at least 1, got $nutrition." }
	this[ItemComponentTypes.VILLAGER_FOOD] = VillagerFoodComponent(nutrition)
}
