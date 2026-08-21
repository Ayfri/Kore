package io.github.ayfri.kore.features.worldgen.biome.types

import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.Serializable

/**
 * The mobs a biome spawns naturally, one weighted list per mob category.
 *
 * A category left to `null` is left out of the file entirely. A structure landing in the biome can replace these lists
 * through its [io.github.ayfri.kore.features.worldgen.structures.SpawnOverrides].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/biomes
 * Minecraft Wiki: https://minecraft.wiki/w/Biome_definition
 */
@Serializable
data class Spawners(
	var ambient: List<Spawn>? = null,
	var axolotls: List<Spawn>? = null,
	var creature: List<Spawn>? = null,
	var misc: List<Spawn>? = null,
	var monster: List<Spawn>? = null,
	var undergroundWaterCreature: List<Spawn>? = null,
	var waterAmbient: List<Spawn>? = null,
	var waterCreature: List<Spawn>? = null,
)

/**
 * One weighted entry of a mob category, picked when the game tries to spawn a mob of that category.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/biomes
 * Minecraft Wiki: https://minecraft.wiki/w/Biome_definition
 *
 * @property type The entity spawned.
 * @property weight The relative chance this entry is drawn, against the other entries of the category.
 * @property minCount The smallest pack size spawned at once.
 * @property maxCount The largest pack size spawned at once.
 */
@Serializable
data class Spawn(
	var type: EntityTypeArgument,
	var weight: Int = 1,
	@JsonSerialName("minCount") var minCount: Int = 0,
	@JsonSerialName("maxCount") var maxCount: Int = 0,
)

/** Sets the `ambient` spawns, the bats and the fireflies. */
fun Spawners.ambient(init: MutableList<Spawn>.() -> Unit) {
	ambient = buildList(init)
}

/** Sets the `axolotls` spawns. */
fun Spawners.axolotls(init: MutableList<Spawn>.() -> Unit) {
	axolotls = buildList(init)
}

/** Sets the `creature` spawns, the passive land mobs. */
fun Spawners.creature(init: MutableList<Spawn>.() -> Unit) {
	creature = buildList(init)
}

/** Sets the `misc` spawns, the category of the mobs that never spawn naturally. */
fun Spawners.misc(init: MutableList<Spawn>.() -> Unit) {
	misc = buildList(init)
}

/** Sets the `monster` spawns. */
fun Spawners.monster(init: MutableList<Spawn>.() -> Unit) {
	monster = buildList(init)
}

/** Sets the `underground_water_creature` spawns, the glow squids. */
fun Spawners.undergroundWaterCreature(init: MutableList<Spawn>.() -> Unit) {
	undergroundWaterCreature = buildList(init)
}

/** Sets the `water_ambient` spawns, the fish. */
fun Spawners.waterAmbient(init: MutableList<Spawn>.() -> Unit) {
	waterAmbient = buildList(init)
}

/** Sets the `water_creature` spawns, the squids and the dolphins. */
fun Spawners.waterCreature(init: MutableList<Spawn>.() -> Unit) {
	waterCreature = buildList(init)
}

/** Adds a spawn entry of a fixed pack size to the category being built. */
fun MutableList<Spawn>.spawner(type: EntityTypeArgument, weight: Int, minCount: Int = 0, maxCount: Int = 0) {
	add(Spawn(type, weight, minCount, maxCount))
}

/** Adds a spawn entry to the category being built, its weight and pack size being reachable through [init]. */
fun MutableList<Spawn>.spawner(type: EntityTypeArgument, init: Spawn.() -> Unit = {}) {
	add(Spawn(type).apply(init))
}
