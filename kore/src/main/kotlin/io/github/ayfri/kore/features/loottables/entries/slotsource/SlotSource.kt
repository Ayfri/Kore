package io.github.ayfri.kore.features.loottables.entries.slotsource

import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Slot sources allow the location of any inventory slot to be specified within datapacks.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable(with = SlotSource.Companion.SlotSourceSerializer::class)
sealed class SlotSource {
	companion object {
		data object SlotSourceSerializer : NamespacedPolymorphicSerializer<SlotSource>(SlotSource::class)
	}
}

@Serializable(with = SlotSourceOrigin.Companion.SlotSourceOriginSerializer::class)
enum class SlotSourceOrigin {
	ATTACKING_ENTITY,
	BLOCK_ENTITY,
	DIRECT_ATTACKER,
	INTERACTING_ENTITY,
	LAST_DAMAGE_PLAYER,
	TARGET_ENTITY,
	THIS,
	;

	companion object {
		data object SlotSourceOriginSerializer : LowercaseSerializer<SlotSourceOrigin>(entries)
	}
}

@Serializable(with = InventoryComponentType.Companion.InventoryComponentTypeSerializer::class)
enum class InventoryComponentType {
	BUNDLE_CONTENTS,
	CHARGED_PROJECTILES,
	CONTAINER,
	;

	companion object {
		data object InventoryComponentTypeSerializer : LowercaseSerializer<InventoryComponentType>(entries, {
			"minecraft:${name.lowercase()}"
		})
	}
}

/** Builder scope for constructing a list of [SlotSource] instances. */
class SlotSourcesBuilder {
	val sources = mutableListOf<SlotSource>()

	fun build() = sources.toList()
}
