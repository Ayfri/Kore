package io.github.ayfri.kore.arguments.types.literals

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.DataPackStateKey
import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtIntArray
import kotlin.uuid.Uuid

@Serializable(with = Argument.ArgumentSerializer::class)
data class UUIDArgument(val uuid: Uuid) : EntityArgument, ScoreHolderArgument {
	override fun asString() = uuid.toString()

	fun toIntArray() = uuid.toLongs { mostSignificantBits, leastSignificantBits ->
		intArrayOf(
			(mostSignificantBits ushr 32).toInt(),
			mostSignificantBits.toInt(),
			(leastSignificantBits ushr 32).toInt(),
			leastSignificantBits.toInt(),
		)
	}

	fun toNBTIntArray() = NbtIntArray(toIntArray())

	companion object {
		fun random() = UUIDArgument(Uuid.random())
	}
}

private val entityUuidOccurrences = DataPackStateKey<MutableMap<String, Int>>("kore.entityUuids")

/** Returns a UUID derived from [key] only, through two differently seeded 64-bit FNV-1a hashes, so every build yields the same one. */
fun hashedUUID(key: String): UUIDArgument {
	val bytes = key.encodeToByteArray()
	fun fnv1a(seed: Long) = bytes.fold(seed) { hash, byte -> (hash xor (byte.toLong() and 0xFF)) * 0x100000001B3L }
	return UUIDArgument(Uuid.fromLongs(fnv1a(-0x340D631B7BDDDCDBL), fnv1a(0x6C62272E07BB0142L)))
}

/**
 * Returns a UUID for an entity this pack summons and later targets, derived from the pack name, [key] and how many
 * times [key] was already asked for, so it is unique per entity yet identical across builds, unlike [randomUUID].
 *
 * ```kotlin
 * val uuid = datapack.entityUUID("my_marker")
 * summon(EntityTypes.MARKER) { this["UUID"] = uuid.toNBTIntArray() }
 * ```
 */
fun DataPack.entityUUID(key: String): UUIDArgument {
	val occurrences = state(entityUuidOccurrences) { mutableMapOf() }
	val occurrence = occurrences[key] ?: 0
	occurrences[key] = occurrence + 1
	return hashedUUID("$name:$key:$occurrence")
}

fun randomUUID() = UUIDArgument.random()
fun uuid(uuid: Uuid) = UUIDArgument(uuid)
fun uuid(uuid: String) = UUIDArgument(Uuid.parse(uuid))
