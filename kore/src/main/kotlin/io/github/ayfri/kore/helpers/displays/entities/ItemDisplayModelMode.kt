package io.github.ayfri.kore.helpers.displays.entities

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

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
		data object ItemDisplayModelModeSerializer : LowercaseSerializer<ItemDisplayModelMode>(entries)
	}
}
