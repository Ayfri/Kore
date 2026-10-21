package io.github.ayfri.kore.features.worldgen.biome.types

import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.intproviders.UniformIntProvider
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import kotlinx.serialization.Serializable

/**
 * The mobs spawned naturally, one weighted list per mob category, the `spawns_by_category` of
 * [io.github.ayfri.kore.features.worldgen.environmentattributes.types.NaturalMobSpawns].
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
 * @property count The pack size spawned at once, a constant or an int provider like `uniform(2, 6)`.
 * @property type The entity spawned.
 * @property weight The relative chance this entry is drawn, against the other entries of the category.
 */
@Serializable
data class Spawn(
	var count: IntProvider = ConstantIntProvider(1),
	var type: EntityTypeArgument,
	var weight: Int = 1,
) : IntProviderScope

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

/**
 * Adds a spawn entry to the category being built, spawning [minCount] to [maxCount] mobs at once.
 *
 * ```
 * spawner(EntityTypes.COW, weight = 8, minCount = 2, maxCount = 4)  // "count": {"type": "minecraft:uniform", ...}
 * spawner(EntityTypes.BAT, weight = 10, minCount = 8)              // "count": 8
 * ```
 */
fun MutableList<Spawn>.spawner(type: EntityTypeArgument, weight: Int, minCount: Int = 1, maxCount: Int = minCount) {
	add(Spawn(if (minCount == maxCount) ConstantIntProvider(minCount) else UniformIntProvider(minCount, maxCount), type, weight))
}

/** Adds a spawn entry to the category being built, its weight and pack size being reachable through [init]. */
fun MutableList<Spawn>.spawner(type: EntityTypeArgument, init: Spawn.() -> Unit = {}) {
	add(Spawn(type = type).apply(init))
}
