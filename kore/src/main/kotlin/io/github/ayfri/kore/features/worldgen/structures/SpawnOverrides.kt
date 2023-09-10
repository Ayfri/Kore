package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.features.worldgen.biome.types.Spawn
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable
data class SpawnOverrides(
	var axolotl: SpawnOverride? = null,
	var ambiant: SpawnOverride? = null,
	var creature: SpawnOverride? = null,
	var monster: SpawnOverride? = null,
	var waterCreature: SpawnOverride? = null,
	var waterAmbiant: SpawnOverride? = null,
	var undergroundWaterCreature: SpawnOverride? = null,
)

@Serializable(with = BoundingBox.Companion.BoundingBoxSerializer::class)
enum class BoundingBox {
	FULL,
	PIECE;

	companion object {
		data object BoundingBoxSerializer : LowercaseSerializer<BoundingBox>(entries)
	}
}

@Serializable
data class SpawnOverride(
	var boundingBox: BoundingBox,
	var spawns: List<Spawn> = emptyList(),
)

fun SpawnOverrides.axolotl(boundingBox: BoundingBox, init: MutableList<Spawn>.() -> Unit) {
	axolotl = SpawnOverride(boundingBox, buildList(init))
}

fun SpawnOverrides.ambiant(boundingBox: BoundingBox, init: MutableList<Spawn>.() -> Unit) {
	ambiant = SpawnOverride(boundingBox, buildList(init))
}

fun SpawnOverrides.creature(boundingBox: BoundingBox, init: MutableList<Spawn>.() -> Unit) {
	creature = SpawnOverride(boundingBox, buildList(init))
}

fun SpawnOverrides.monster(boundingBox: BoundingBox, init: MutableList<Spawn>.() -> Unit) {
	monster = SpawnOverride(boundingBox, buildList(init))
}

fun SpawnOverrides.waterCreature(boundingBox: BoundingBox, init: MutableList<Spawn>.() -> Unit) {
	waterCreature = SpawnOverride(boundingBox, buildList(init))
}

fun SpawnOverrides.waterAmbiant(boundingBox: BoundingBox, init: MutableList<Spawn>.() -> Unit) {
	waterAmbiant = SpawnOverride(boundingBox, buildList(init))
}

fun SpawnOverrides.undergroundWaterCreature(boundingBox: BoundingBox, init: MutableList<Spawn>.() -> Unit) {
	undergroundWaterCreature = SpawnOverride(boundingBox, buildList(init))
}
