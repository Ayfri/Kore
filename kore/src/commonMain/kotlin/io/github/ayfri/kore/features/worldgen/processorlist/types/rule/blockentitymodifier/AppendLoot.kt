package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier

import io.github.ayfri.kore.features.worldgen.processorlist.types.rule.ProcessorRule
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.Serializable

/**
 * Keeps the block entity data of the template and adds a `LootTable` tag to it, so the placed container fills itself
 * from [lootTable] the first time a player opens it.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property lootTable The loot table the container is filled with.
 */
@Serializable
data class AppendLoot(
	var lootTable: LootTableArgument,
) : BlockEntityModifier()

/**
 * Creates an `append_loot` block entity modifier filling the placed container from [lootTable].
 *
 * ```kotlin
 * rule {
 *     blockEntityModifier = appendLoot(LootTables.Chests.SIMPLE_DUNGEON)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorRule.appendLoot(lootTable: LootTableArgument) = AppendLoot(lootTable)
