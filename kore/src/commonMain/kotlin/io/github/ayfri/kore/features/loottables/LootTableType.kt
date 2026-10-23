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
	ARCHAEOLOGY,
	BARTER,
	BLOCK,
	BLOCK_INTERACT,
	BLOCK_USE,
	CHEST,
	COMMAND,
	COMMAND_COMPUTE_DEFAULT,
	COMMAND_COMPUTE_ENTITY,
	COMMAND_COMPUTE_POSITION,
	COMMAND_SLOT_SOURCE,
	CONTAINER_PROCESS,
	EMPTY,
	ENCHANTED_DAMAGE,
	ENCHANTED_ENTITY,
	ENCHANTED_ITEM,
	ENCHANTED_LOCATION,
	ENTITY,
	ENTITY_INTERACT,
	EQUIPMENT,
	FISHING,
	GENERIC,
	GIFT,
	HIT_BLOCK,
	SELECTOR,
	SHEARING,
	VAULT,
	VILLAGER_TRADE,
	;

	companion object {
		data object LootTableTypeSerializer : LowercaseSerializer<LootTableType>(entries)
	}
}
