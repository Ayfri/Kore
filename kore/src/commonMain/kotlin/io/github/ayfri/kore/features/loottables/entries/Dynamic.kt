package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable


@Serializable(with = LootEntryDynamicName.Companion.LootEntryDynamicNameSerializer::class)
enum class LootEntryDynamicName {
	CONTENTS,
	SHERDS,
	;

	companion object {
		data object LootEntryDynamicNameSerializer : LowercaseSerializer<LootEntryDynamicName>(entries, {
			"minecraft:${name.lowercase()}"
		})
	}
}

/**
 * Loot entry that pulls items from a dynamic context (e.g., `minecraft:contents`).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Dynamic(
	var name: LootEntryDynamicName,
	override var condition: PredicateCondition? = null,
	override var modifier: InlinableItemModifier? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/** Add and configure a Dynamic entry. */
fun LootEntries.dynamic(name: LootEntryDynamicName, block: Dynamic.() -> Unit = {}) {
	add(Dynamic(name).apply(block))
}
