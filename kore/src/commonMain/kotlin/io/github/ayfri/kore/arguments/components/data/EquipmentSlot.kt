package io.github.ayfri.kore.arguments.components.data

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * A slot, or group of slots, an item can be equipped in.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Slot
 */
@Serializable(EquipmentSlot.Companion.EquipmentSlotSerializer::class)
enum class EquipmentSlot {
	ANY,
	MAINHAND,
	OFFHAND,
	HAND,
	HEAD,
	CHEST,
	LEGS,
	FEET,
	ARMOR,
	BODY,
	SADDLE;

	companion object {
		data object EquipmentSlotSerializer : LowercaseSerializer<EquipmentSlot>(entries)
	}
}
