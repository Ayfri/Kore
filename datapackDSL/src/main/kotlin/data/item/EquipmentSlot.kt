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
		val values = values()

		object EquipmentSlotSerializer : LowercaseSerializer<EquipmentSlot>(values)
	}
}
