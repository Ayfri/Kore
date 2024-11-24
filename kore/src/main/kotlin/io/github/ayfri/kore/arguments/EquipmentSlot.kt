package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(EquipmentSlot.Companion.EquipmentSlotSerializer::class)
enum class EquipmentSlot {
	HEAD,
	CHEST,
	LEGS,
	FEET,
	BODY,
	MAINHAND,
	OFFHAND;

	companion object {
		data object EquipmentSlotSerializer : LowercaseSerializer<EquipmentSlot>(entries)
	}
} 