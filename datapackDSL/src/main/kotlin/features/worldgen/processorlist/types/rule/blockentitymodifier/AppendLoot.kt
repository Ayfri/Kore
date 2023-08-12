package features.worldgen.processorlist.types.rule.blockentitymodifier

import arguments.types.resources.LootTableArgument
import kotlinx.serialization.Serializable

@Serializable
data class AppendLoot(
	var lootTable: LootTableArgument,
) : BlockEntityModifier()

fun appendLoot(lootTable: LootTableArgument) = AppendLoot(lootTable)
