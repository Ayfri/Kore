package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContainerLoot(
	@SerialName("loot_table") var lootTable: LootTableArgument,
	var seed: Long? = null,
) : Component()

fun ComponentsScope.containerLoot(lootTable: LootTableArgument, seed: Long? = null) = apply {
	this[ItemComponentTypes.CONTAINER_LOOT] = ContainerLoot(lootTable, seed)
}
