package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

/**
 * Loot entry that yields nothing; useful for weight balancing.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Empty(
	override var condition: PredicateCondition? = null,
	override var modifier: InlinableItemModifier? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/** Add an Empty entry with given quality and weight, and configure it. */
fun LootEntries.empty(quality: Int = 1, weight: Int = 1, block: Empty.() -> Unit = {}) {
	add(Empty(quality = quality, weight = weight).apply(block))
}
