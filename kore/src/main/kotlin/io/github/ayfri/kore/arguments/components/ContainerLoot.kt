package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.LootTableArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContainerLoot(
	@SerialName("loot_table") var lootTable: LootTableArgument,
	var seed: Long? = null,
) : Component()

fun Components.containerLoot(lootTable: LootTableArgument, seed: Long? = null) = apply {
	components["container_loot"] = ContainerLoot(lootTable, seed)
}
