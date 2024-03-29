package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(MapDecoration.Companion.MapDecorationSerializer::class)
enum class MapDecoration {
	BANNER_BLACK,
	BANNER_BLUE,
	BANNER_BROWN,
	BANNER_CYAN,
	BANNER_GRAY,
	BANNER_GREEN,
	BANNER_LIGHT_BLUE,
	BANNER_LIGHT_GRAY,
	BANNER_LIME,
	BANNER_MAGENTA,
	BANNER_ORANGE,
	BANNER_PINK,
	BANNER_PURPLE,
	BANNER_RED,
	BANNER_WHITE,
	BANNER_YELLOW,
	BLUE_MARKER,
	DESERT_VILLAGE,
	FRAME,
	JUNGLE_TEMPLE,
	MANSION,
	MONUMENT,
	PLAINS_VILLAGE,
	PLAYER,
	PLAYER_OFF_LIMITS,
	PLAYER_OFF_MAP,
	RED_MARKER,
	RED_X,
	SAVANNA_VILLAGE,
	SNOWY_VILLAGE,
	SWAMP_HUT,
	TAIGA_VILLAGE,
	TARGET_POINT,
	TARGET_X;

	companion object {
		data object MapDecorationSerializer : LowercaseSerializer<MapDecoration>(entries)
	}
}
