package io.github.ayfri.kore.features.trialspawners

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.EquipmentSlot
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import io.github.ayfri.kore.generated.arguments.types.TrialSpawnerArgument
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.nbtCompound

/**
 * An inclusive light level range a spawn is allowed in, from 0 to 15.
 *
 * @property maxInclusive The highest allowed light level.
 * @property minInclusive The lowest allowed light level.
 */
@Serializable
data class TrialSpawnerLightLimit(
	var maxInclusive: Int,
	var minInclusive: Int,
)

/**
 * Light conditions overriding the spawned mob's own spawn rules.
 *
 * @property blockLightLimit The allowed block light range, `0..15` when `null`.
 * @property skyLightLimit The allowed sky light range, `0..15` when `null`.
 */
@Serializable
data class TrialSpawnerCustomSpawnRules(
	var blockLightLimit: TrialSpawnerLightLimit? = null,
	var skyLightLimit: TrialSpawnerLightLimit? = null,
)

/**
 * Drop chances of the equipment given to a spawned mob, written as a single float when [chance] is set, or as a
 * per-slot map otherwise.
 *
 * @property chance The drop chance shared by every slot.
 * @property slots The drop chance of each listed slot.
 */
@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = TrialSpawnerSlotDropChances.Companion.TrialSpawnerSlotDropChancesSerializer::class)
data class TrialSpawnerSlotDropChances(
	var chance: Float? = null,
	var slots: Map<EquipmentSlot, Float>? = null,
) {
	companion object {
		data object TrialSpawnerSlotDropChancesSerializer :
			EitherInlineSerializer<TrialSpawnerSlotDropChances>(generatedSerializer(), "chance", "slots")
	}
}

/**
 * Equipment given to a spawned mob, rolled from a loot table.
 *
 * @property lootTable The loot table the equipment is rolled from.
 * @property slotDropChances The chances of the equipment dropping on death.
 */
@Serializable
data class TrialSpawnerEquipment(
	var lootTable: LootTableArgument,
	var slotDropChances: TrialSpawnerSlotDropChances? = null,
)

/**
 * The entity a trial spawner spawns, with its NBT and optional spawn overrides.
 *
 * @property customSpawnRules Light conditions replacing the mob's own spawn rules.
 * @property entity The entity NBT, always containing its `id`.
 * @property equipment The equipment given to the spawned mob.
 */
@Serializable
data class TrialSpawnerSpawnData(
	var customSpawnRules: TrialSpawnerCustomSpawnRules? = null,
	@Serializable(with = NbtAsJsonSerializer::class)
	var entity: NbtTag,
	var equipment: TrialSpawnerEquipment? = null,
)

/**
 * A weighted entry of [TrialSpawner.spawnPotentials].
 *
 * @property data The entity to spawn.
 * @property weight The chance of this entry being picked relative to the others, at least 1.
 */
@Serializable
data class TrialSpawnerSpawnPotential(
	var data: TrialSpawnerSpawnData,
	var weight: Int,
)

/**
 * A weighted entry of [TrialSpawner.lootTablesToEject].
 *
 * @property data The loot table ejected as a reward.
 * @property weight The chance of this entry being picked relative to the others, at least 1.
 */
@Serializable
data class TrialSpawnerLootTableEntry(
	var data: LootTableArgument,
	var weight: Int,
)

/**
 * Data-driven trial spawner configuration, referenced by trial spawner blocks for their normal or ominous state.
 *
 * Every field is optional, the game falling back to its defaults (4 blocks range, 6 total mobs, 2 simultaneous mobs,
 * 40 ticks between spawns, vanilla reward loot tables).
 *
 * Produces `data/<namespace>/trial_spawner/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/trial-spawners
 * Minecraft Wiki: https://minecraft.wiki/w/Trial_spawner_configuration
 */
@Serializable
data class TrialSpawner(
	@Transient
	override var fileName: String = "trial_spawner",
	var itemsToDropWhenOminous: LootTableArgument? = null,
	var lootTablesToEject: MutableList<TrialSpawnerLootTableEntry>? = null,
	var simultaneousMobs: Float? = null,
	var simultaneousMobsAddedPerPlayer: Float? = null,
	var spawnPotentials: MutableList<TrialSpawnerSpawnPotential>? = null,
	var spawnRange: Int? = null,
	var ticksBetweenSpawn: Int? = null,
	var totalMobs: Float? = null,
	var totalMobsAddedPerPlayer: Float? = null,
) : Generator("trial_spawner") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a trial spawner configuration using a builder block.
 *
 * Produces `data/<namespace>/trial_spawner/<fileName>.json`.
 *
 * ```kotlin
 * trialSpawner("zombies") {
 *     totalMobs = 8f
 *     spawnPotential(EntityTypes.ZOMBIE) {
 *         equipment(LootTables.Equipment.TRIAL_CHAMBER_MELEE, 0f)
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/trial-spawners
 * Minecraft Wiki: https://minecraft.wiki/w/Trial_spawner_configuration
 */
fun DataPack.trialSpawner(
	fileName: String = "trial_spawner",
	init: TrialSpawner.() -> Unit = {},
): TrialSpawnerArgument {
	val trialSpawner = TrialSpawner(fileName = fileName).apply(init)
	trialSpawners += trialSpawner
	return TrialSpawnerArgument(fileName, trialSpawner.namespace ?: name)
}

/** Adds a weighted reward [lootTable] to [TrialSpawner.lootTablesToEject]. */
fun TrialSpawner.lootTableToEject(lootTable: LootTableArgument, weight: Int = 1) {
	lootTablesToEject = (lootTablesToEject ?: mutableListOf()).apply { add(TrialSpawnerLootTableEntry(lootTable, weight)) }
}

/**
 * Adds a weighted [type] entity to [TrialSpawner.spawnPotentials], [init] customizing its NBT, equipment and spawn rules.
 *
 * `spawnPotential(EntityTypes.SLIME, weight = 3) { entity { this["Size"] = 1 } }`
 */
fun TrialSpawner.spawnPotential(
	type: EntityTypeArgument,
	weight: Int = 1,
	init: TrialSpawnerSpawnData.() -> Unit = {},
) {
	val data = TrialSpawnerSpawnData(entity = nbt { this["id"] = type.asId() }).apply(init)
	spawnPotentials = (spawnPotentials ?: mutableListOf()).apply { add(TrialSpawnerSpawnPotential(data, weight)) }
}

/** Sets the light ranges the entity can spawn in, overriding its own spawn rules. */
fun TrialSpawnerSpawnData.customSpawnRules(blockLightLimit: IntRange? = null, skyLightLimit: IntRange? = null) {
	customSpawnRules = TrialSpawnerCustomSpawnRules(
		blockLightLimit?.let { TrialSpawnerLightLimit(it.last, it.first) },
		skyLightLimit?.let { TrialSpawnerLightLimit(it.last, it.first) },
	)
}

/** Adds NBT to the spawned entity, keeping its `id`. */
fun TrialSpawnerSpawnData.entity(block: NbtCompoundBuilder.() -> Unit) {
	entity = nbt {
		entity.nbtCompound.forEach { (key, value) -> put(key, value) }
		block()
	}
}

/** Gives the spawned mob equipment rolled from [lootTable], each slot dropping with [slotDropChance]. */
fun TrialSpawnerSpawnData.equipment(lootTable: LootTableArgument, slotDropChance: Float? = null) {
	equipment = TrialSpawnerEquipment(lootTable, slotDropChance?.let { TrialSpawnerSlotDropChances(chance = it) })
}

/** Gives the spawned mob equipment rolled from [lootTable], with a drop chance per slot. */
fun TrialSpawnerSpawnData.equipment(lootTable: LootTableArgument, slotDropChances: Map<EquipmentSlot, Float>) {
	equipment = TrialSpawnerEquipment(lootTable, TrialSpawnerSlotDropChances(slots = slotDropChances))
}
