package features.loottables

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

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
		val values = values()

		object LootTableTypeSerializer : LowercaseSerializer<LootTableType>(values)
	}
}
