package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

typealias LootEntries = MutableList<LootEntry>

/**
 * Base sealed class for all loot table entries.
 *
 * Docs: https://kore.ayfri.com/docs/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable(with = LootEntry.Companion.LootEntrySerializer::class)
sealed class LootEntry {
	companion object {
		data object LootEntrySerializer : NamespacedPolymorphicSerializer<LootEntry>(LootEntry::class)
	}
}
