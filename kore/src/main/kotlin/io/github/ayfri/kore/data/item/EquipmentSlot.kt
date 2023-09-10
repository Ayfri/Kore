package io.github.ayfri.kore.data.item

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

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
