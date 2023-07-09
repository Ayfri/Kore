package features.itemmodifiers.functions

import generated.Tags
import kotlinx.serialization.Serializable
import serializers.EnumOrdinalSerializer

@Serializable
data class ExplorationMap(
	var destination: Tags.Worldgen.Structure,
	var decoration: MapDecoration? = null,
	var zoom: Int? = null,
	var searchRadius: Int? = null,
	var skipExistingChunks: Boolean? = null,
) : ItemFunctionSurrogate

@Serializable(MapDecoration.Companion.MapDecorationSerializer::class)
enum class MapDecoration {
	PLAYER,
	FRAME,
	RED_MARKER,
	BLUE_MARKER,
	TARGET_X,
	TARGET_POINT,
	PLAYER_OFF_MAP,
	PLAYER_OFF_LIMITS,
	MANSION,
	MONUMENT,
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
	RED_X;

	companion object {
		data object MapDecorationSerializer : EnumOrdinalSerializer<MapDecoration>(entries)
	}
}
