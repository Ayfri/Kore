package io.github.ayfri.kore.arguments.components.data

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

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
	BODY;

	companion object {
		data object EquipmentSlotSerializer : LowercaseSerializer<EquipmentSlot>(entries)
	}
}
