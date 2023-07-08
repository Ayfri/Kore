package data.item

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(EquipmentSlot.Companion.EquipmentSlotSerializer::class)
enum class EquipmentSlot {
	MAINHAND,
	OFFHAND,
	FEET,
	LEGS,
	CHEST,
	HEAD;

	companion object {
		data object EquipmentSlotSerializer : LowercaseSerializer<EquipmentSlot>(entries)
	}
}
