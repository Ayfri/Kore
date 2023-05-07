package features.loottables.entries

import kotlinx.serialization.Serializable

@Serializable
data class Empty(
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry
