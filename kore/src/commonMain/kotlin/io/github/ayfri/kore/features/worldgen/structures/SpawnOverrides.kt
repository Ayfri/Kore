package io.github.ayfri.kore.features.worldgen.structures

import io.github.ayfri.kore.features.worldgen.biome.types.Spawn
import kotlinx.serialization.Serializable

/**
 * The mob spawn lists a structure substitutes for the ones of the biome it lands in, one per mob category.
 *
 * A category left to `null` keeps the biome spawns, while a category set to an empty [SpawnOverride.spawns] list stops
 * that category from spawning inside the structure entirely. Use the functions of this class instead of assigning the
 * properties, so a category always carries its [BoundingBox].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@Serializable
data class SpawnOverrides(
	var ambient: SpawnOverride? = null,
	var axolotls: SpawnOverride? = null,
	var creature: SpawnOverride? = null,
	var misc: SpawnOverride? = null,
	var monster: SpawnOverride? = null,
	var undergroundWaterCreature: SpawnOverride? = null,
	var waterAmbient: SpawnOverride? = null,
	var waterCreature: SpawnOverride? = null,
)

/**
 * The spawn list of a single mob category inside a structure.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property boundingBox The volume the override applies to.
 * @property spawns The weighted entries mobs of that category are picked from, empty stopping them from spawning.
 */
@Serializable
data class SpawnOverride(
	var boundingBox: BoundingBox = BoundingBox.PIECE,
	var spawns: List<Spawn> = emptyList(),
)

/** Overrides the `ambient` spawns, the bats and the fireflies, inside [boundingBox]. */
fun SpawnOverrides.ambient(boundingBox: BoundingBox = BoundingBox.PIECE, init: MutableList<Spawn>.() -> Unit = {}) {
	ambient = SpawnOverride(boundingBox, buildList(init))
}

/** Overrides the `axolotls` spawns inside [boundingBox]. */
fun SpawnOverrides.axolotls(boundingBox: BoundingBox = BoundingBox.PIECE, init: MutableList<Spawn>.() -> Unit = {}) {
	axolotls = SpawnOverride(boundingBox, buildList(init))
}

/** Overrides the `creature` spawns, the passive land mobs, inside [boundingBox]. */
fun SpawnOverrides.creature(boundingBox: BoundingBox = BoundingBox.PIECE, init: MutableList<Spawn>.() -> Unit = {}) {
	creature = SpawnOverride(boundingBox, buildList(init))
}

/** Overrides the `misc` spawns, the category of the mobs that never spawn naturally, inside [boundingBox]. */
fun SpawnOverrides.misc(boundingBox: BoundingBox = BoundingBox.PIECE, init: MutableList<Spawn>.() -> Unit = {}) {
	misc = SpawnOverride(boundingBox, buildList(init))
}

/** Overrides the `monster` spawns inside [boundingBox], the most common override for a structure. */
fun SpawnOverrides.monster(boundingBox: BoundingBox = BoundingBox.PIECE, init: MutableList<Spawn>.() -> Unit = {}) {
	monster = SpawnOverride(boundingBox, buildList(init))
}

/** Overrides the `underground_water_creature` spawns, the glow squids, inside [boundingBox]. */
fun SpawnOverrides.undergroundWaterCreature(
	boundingBox: BoundingBox = BoundingBox.PIECE,
	init: MutableList<Spawn>.() -> Unit = {},
) {
	undergroundWaterCreature = SpawnOverride(boundingBox, buildList(init))
}

/** Overrides the `water_ambient` spawns, the fish, inside [boundingBox]. */
fun SpawnOverrides.waterAmbient(
	boundingBox: BoundingBox = BoundingBox.PIECE,
	init: MutableList<Spawn>.() -> Unit = {},
) {
	waterAmbient = SpawnOverride(boundingBox, buildList(init))
}

/** Overrides the `water_creature` spawns, the squids and the dolphins, inside [boundingBox]. */
fun SpawnOverrides.waterCreature(
	boundingBox: BoundingBox = BoundingBox.PIECE,
	init: MutableList<Spawn>.() -> Unit = {},
) {
	waterCreature = SpawnOverride(boundingBox, buildList(init))
}
