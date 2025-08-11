package io.github.ayfri.kore.features.loottables.entries

import kotlinx.serialization.Serializable

/**
 * Loot entry that yields nothing; useful for weight balancing.
 *
 * Docs: https://kore.ayfri.com/docs/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Empty(
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/** Add an Empty entry with given quality and weight. */
fun LootEntries.empty(quality: Int = 1, weight: Int = 1) {
	add(Empty(quality, weight))
}
