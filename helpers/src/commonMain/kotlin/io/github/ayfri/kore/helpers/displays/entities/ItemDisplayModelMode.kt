package io.github.ayfri.kore.helpers.displays.entities

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(ItemDisplayModelMode.Companion.ItemDisplayModelModeSerializer::class)
enum class ItemDisplayModelMode {
	FIRSTPERSON_LEFTHAND,
	FIRSTPERSON_RIGHTHAND,
	FIXED,
	GROUND,
	GUI,
	HEAD,
	NONE,
	ON_SHELF,
	THIRDPERSON_LEFTHAND,
	THIRDPERSON_RIGHTHAND,
	;

	companion object {
		data object ItemDisplayModelModeSerializer : LowercaseSerializer<ItemDisplayModelMode>(entries)
	}
}
