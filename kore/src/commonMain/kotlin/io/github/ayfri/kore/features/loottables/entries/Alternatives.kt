package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

/**
 * Loot entry that selects the first successful child entry (alternative of children).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Alternatives(
	override var children: List<LootEntry>,
	override var condition: PredicateCondition? = null,
	override var modifier: InlinableItemModifier? = null,
) : LootEntry(), CompositeLootEntry

/** Add an Alternatives entry with optional condition and modifier. */
fun LootEntries.alternatives(children: List<LootEntry> = emptyList(), block: Alternatives.() -> Unit = {}) {
	add(Alternatives(children).apply(block))
}
