package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifierEntry
import generated.Tags
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable
data class ExplorationMap(
	var destination: Tags.Worldgen.Structure? = null,
	var decoration: MapDecoration? = null,
	var zoom: Int? = null,
	var searchRadius: Int? = null,
	var skipExistingChunks: Boolean? = null,
) : ItemFunctionSurrogate

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
	FRAME,
	MANSION,
	MONUMENT,
	PLAYER,
	PLAYER_OFF_LIMITS,
	PLAYER_OFF_MAP,
	RED_MARKER,
	RED_X,
	TARGET_POINT,
	TARGET_X;

	companion object {
		data object MapDecorationSerializer : LowercaseSerializer<MapDecoration>(entries)
	}
}

fun ItemModifierEntry.explorationMap(
	destination: Tags.Worldgen.Structure,
	decoration: MapDecoration? = null,
	zoom: Int? = null,
	searchRadius: Int? = null,
	skipExistingChunks: Boolean? = null,
) {
	function = ExplorationMap(destination, decoration, zoom, searchRadius, skipExistingChunks)
}

fun ItemModifierEntry.explorationMap(block: ExplorationMap.() -> Unit) {
	function = ExplorationMap().apply(block)
}
