package io.github.ayfri.kore.features.loottables.entries

import kotlinx.serialization.Serializable

@Serializable
data class Empty(
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

fun LootEntries.empty(quality: Int = 1, weight: Int = 1) {
	add(Empty(quality, weight))
}
