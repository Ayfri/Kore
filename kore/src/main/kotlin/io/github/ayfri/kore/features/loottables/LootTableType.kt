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
	EQUIPMENT,
	ARCHEOLOGY,
	GIFT,
	BARTER,
	VAULT,
	ADVANCEMENT_REWARD,
	ADVANCEMENT_ENTITY,
	ADVANCEMENT_LOCATION,
	BLOCK_USE,
	GENERIC,
	BLOCK,
	SHEARING;

	companion object {
		data object LootTableTypeSerializer : LowercaseSerializer<LootTableType>(entries)
	}
}
