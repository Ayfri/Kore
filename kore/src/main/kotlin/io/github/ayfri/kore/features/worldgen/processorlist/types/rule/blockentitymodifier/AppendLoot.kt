package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier

import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.Serializable

@Serializable
data class AppendLoot(
	var lootTable: LootTableArgument,
) : BlockEntityModifier()

fun appendLoot(lootTable: LootTableArgument) = AppendLoot(lootTable)
