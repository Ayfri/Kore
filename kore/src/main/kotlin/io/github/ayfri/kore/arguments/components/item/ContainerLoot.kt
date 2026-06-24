package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:container_loot` item component, which references a loot table that fills the container when first opened.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#container_loot
 */
@Serializable
data class ContainerLoot(
	@SerialName("loot_table") var lootTable: LootTableArgument,
	var seed: Long? = null,
) : Component()

/** References a loot table to generate container contents when opened. */
fun ComponentsScope.containerLoot(lootTable: LootTableArgument, seed: Long? = null) = apply {
	this[ItemComponentTypes.CONTAINER_LOOT] = ContainerLoot(lootTable, seed)
}
