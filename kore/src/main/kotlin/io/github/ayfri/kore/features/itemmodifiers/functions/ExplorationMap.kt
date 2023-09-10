package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ExplorationMap(
	override var conditions: PredicateAsList? = null,
	var destination: Tags.Worldgen.Structure? = null,
	var decoration: MapDecoration? = null,
	var zoom: Int? = null,
	var searchRadius: Int? = null,
	var skipExistingChunks: Boolean? = null,
) : ItemFunction()

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

fun ItemModifier.explorationMap(
	destination: Tags.Worldgen.Structure,
	decoration: MapDecoration? = null,
	zoom: Int? = null,
	searchRadius: Int? = null,
	skipExistingChunks: Boolean? = null,
	block: ExplorationMap.() -> Unit = {},
) {
	modifiers += ExplorationMap(
		destination = destination,
		decoration = decoration,
		zoom = zoom,
		searchRadius = searchRadius,
		skipExistingChunks = skipExistingChunks
	).apply(block)
}

fun ItemModifier.explorationMap(block: ExplorationMap.() -> Unit) {
	modifiers += ExplorationMap().apply(block)
}
