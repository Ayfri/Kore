package helpers.displays.entities

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(ItemDisplayModelMode.Companion.ItemDisplayModelModeSerializer::class)
enum class ItemDisplayModelMode {
	NONE,
	THIRDPERSON_LEFTHAND,
	THIRDPERSON_RIGHTHAND,
	FIRSTPERSON_LEFTHAND,
	FIRSTPERSON_RIGHTHAND,
	HEAD,
	GUI,
	GROUND,
	FIXED;

	companion object {
		val values = values()

		object ItemDisplayModelModeSerializer : LowercaseSerializer<ItemDisplayModelMode>(values)
	}
}
