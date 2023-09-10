package io.github.ayfri.kore.features.worldgen.biome.types

import io.github.ayfri.kore.arguments.types.resources.EntityTypeArgument
import kotlinx.serialization.Serializable

@Serializable
data class Spawners(
	var axolotl: List<Spawn>? = null,
	var ambiant: List<Spawn>? = null,
	var creature: List<Spawn>? = null,
	var monster: List<Spawn>? = null,
	var waterCreature: List<Spawn>? = null,
	var waterAmbiant: List<Spawn>? = null,
	var undergroundWaterCreature: List<Spawn>? = null,
)

@Serializable
data class Spawn(
	var type: EntityTypeArgument,
	var weight: Int = 1,
	var minCount: Int = 0,
	var maxCount: Int = 0,
)

fun Spawners.axolotl(init: MutableList<Spawn>.() -> Unit) {
	axolotl = buildList(init)
}

fun Spawners.ambiant(init: MutableList<Spawn>.() -> Unit) {
	ambiant = buildList(init)
}

fun Spawners.creature(init: MutableList<Spawn>.() -> Unit) {
	creature = buildList(init)
}

fun Spawners.monster(init: MutableList<Spawn>.() -> Unit) {
	monster = buildList(init)
}

fun Spawners.waterCreature(init: MutableList<Spawn>.() -> Unit) {
	waterCreature = buildList(init)
}

fun Spawners.waterAmbiant(init: MutableList<Spawn>.() -> Unit) {
	waterAmbiant = buildList(init)
}

fun Spawners.undergroundWaterCreature(init: MutableList<Spawn>.() -> Unit) {
	undergroundWaterCreature = buildList(init)
}

fun MutableList<Spawn>.spawner(type: EntityTypeArgument, weight: Int, minCount: Int = 0, maxCount: Int = 0) {
	add(Spawn(type, weight, minCount, maxCount))
}

fun MutableList<Spawn>.spawner(type: EntityTypeArgument, init: Spawn.() -> Unit = {}) {
	add(Spawn(type).apply(init))
}
