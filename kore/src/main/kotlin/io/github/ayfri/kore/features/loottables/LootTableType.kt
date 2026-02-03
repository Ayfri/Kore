package io.github.ayfri.kore.features.loottables

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Loot table type identifiers matching vanilla categories.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable(with = LootTableType.Companion.LootTableTypeSerializer::class)
enum class LootTableType {
	ADVANCEMENT_ENTITY,
	ADVANCEMENT_LOCATION,
	ADVANCEMENT_REWARD,
	ARCHEOLOGY,
	BARTER,
	BLOCK,
	BLOCK_INTERACT,
	BLOCK_USE,
	CHEST,
	COMMAND,
	EMPTY,
	ENTITY,
	ENTITY_INTERACT,
	EQUIPMENT,
	FISHING,
	GENERIC,
	GIFT,
	SELECTOR,
	SHEARING,
	VAULT,
	;

	companion object {
		data object LootTableTypeSerializer : LowercaseSerializer<LootTableType>(entries)
	}
}
