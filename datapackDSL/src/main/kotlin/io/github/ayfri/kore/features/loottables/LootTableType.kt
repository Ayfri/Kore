package io.github.ayfri.kore.features.loottables

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LootTableType.Companion.LootTableTypeSerializer::class)
enum class LootTableType {
	EMPTY,
	CHEST,
	COMMAND,
	SELECTOR,
	FISHING,
	ENTITY,
	GIFT,
	BARTER,
	ADVANCEMENT_REWARD,
	ADVANCEMENT_ENTITY,
	GENERIC,
	BLOCK;

	companion object {
		data object LootTableTypeSerializer : LowercaseSerializer<LootTableType>(entries)
	}
}
