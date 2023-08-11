package features.loottables.entries

import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

typealias LootEntries = MutableList<LootEntry>

@Serializable(with = LootEntry.Companion.LootEntrySerializer::class)
sealed class LootEntry {
	companion object {
		data object LootEntrySerializer : NamespacedPolymorphicSerializer<LootEntry>(LootEntry::class)
	}
}
