package io.github.ayfri.kore.features.loottables

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Loot table type identifiers matching vanilla categories.
 *
 * Docs: https://kore.ayfri.com/docs/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
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
