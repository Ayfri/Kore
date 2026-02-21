package io.github.ayfri.kore.state

import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.scores.ScoreboardCriterion
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.Data
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A Kotlin property delegate that maps a scoreboard objective to a simple `var`.
 *
 * When the value is read, nothing is emitted (values can only be read via `execute store`
 * at Minecraft runtime). When the value is set, a `/scoreboard players set` command is emitted.
 * Use [plusAssign]/[minusAssign] on the returned [ScoreboardEntity] for relative changes.
 *
 * @property objectiveName The name of the scoreboard objective.
 * @property entity The target entity.
 * @property fn The function context used to emit commands.
 * @property default The initial value set the first time [getValue] is called (and whenever setValue is used).
 * @property criterion The scoreboard criterion (default: `dummy`).
 */
data class ScoreboardDelegate(
	val objectiveName: String,
	val entity: Entity,
	val fn: Function,
	val default: Int = 0,
	val criterion: ScoreboardCriterion = ScoreboardCriteria.DUMMY,
) : ReadWriteProperty<Any?, Int> {
	/** Lazily ensure the scoreboard objective exists once per datapack. */
	private var initialized = false

	private fun ensureObjective() {
		if (!initialized) {
			with(fn) {
				scoreboard {
					objectives {
						add(objectiveName, criterion)
					}
				}
			}
			initialized = true
		}
	}

	/**
	 * Reading the value always returns [default] (actual runtime value lives in Minecraft's scoreboard).
	 * A call to getValue also ensures the scoreboard objective is created.
	 */
	override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
		ensureObjective()
		return default
	}

	/** Emits `/scoreboard players set <entity> <objective> <value>`. */
	override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
		ensureObjective()
		fn.scoreboard {
			players {
				set(entity.asSelector(), objectiveName, value)
			}
		}
	}
}

/**
 * A Kotlin property delegate that maps a NBT storage path to a simple `var`.
 *
 * When the value is set, a `/data modify storage <storage> <path> set value <value>` is emitted.
 *
 * @property storageId The storage resource location (e.g. `"my_namespace:data"`).
 * @property path The NBT path inside the storage (e.g. `"customTag"`).
 * @property fn The function context used to emit commands.
 * @property default The compile-time default value (actual runtime value lives in storage).
 */
class StorageDelegate<T : Any>(
	val storageId: String,
	val path: String,
	val fn: Function,
	val default: T,
) : ReadWriteProperty<Any?, T> {
	private val storage: StorageArgument by lazy {
		val parts = storageId.split(":")
		require(parts.size == 2) { "storageId must be in format 'namespace:name', got '$storageId'" }
		StorageArgument(parts[1], parts[0])
	}

	/** Returns [default] at compile time (actual value lives in Minecraft's storage). */
	override fun getValue(thisRef: Any?, property: KProperty<*>): T = default

	/** Emits a `/data modify storage` command. */
	override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		val data: Data = fn.data(storage)
		when (value) {
			is Int -> data.modify(path, value)
			is Float -> data.modify(path, value)
			is String -> data.modify(path, value)
			is Boolean -> data.modify(path, value)
			else -> data.modify(path, value.toString())
		}
	}
}

/**
 * Creates a scoreboard delegate for an entity, mapping a scoreboard objective to a `var`.
 *
 * Usage:
 * ```kotlin
 * var mana by player.scoreboard("mana_obj", default = 100)
 * mana = 80 // emits: /scoreboard players set <player> mana_obj 80
 * ```
 *
 * @param objectiveName The name of the scoreboard objective.
 * @param default The compile-time default (runtime value lives in the scoreboard).
 * @param criterion The scoreboard criterion (default: `dummy`).
 */
context(fn: Function)
fun Entity.scoreboard(
	objectiveName: String,
	default: Int = 0,
	criterion: ScoreboardCriterion = ScoreboardCriteria.DUMMY,
) = ScoreboardDelegate(objectiveName, this, fn, default, criterion)

/**
 * Creates a scoreboard entity handle from a delegate. Provides access to `+=` and `-=` operators.
 *
 * Usage:
 * ```kotlin
 * val manaObj by player.scoreboardEntity("mana_obj")
 * manaObj += 10   // emits: /scoreboard players add <player> mana_obj 10
 * manaObj -= 20   // emits: /scoreboard players remove <player> mana_obj 20
 * ```
 */
context(fn: Function)
fun Entity.scoreboardEntity(objectiveName: String) = ScoreboardEntity(objectiveName, this)

/**
 * Creates a storage delegate for an entity, mapping a NBT storage path to a `var`.
 *
 * Usage:
 * ```kotlin
 * var customTag by player.storage("my_namespace:data", "customTag", default = "hello")
 * customTag = "world" // emits: /data modify storage my_namespace:data customTag set value "world"
 * ```
 *
 * @param storageId The storage resource location in format `"namespace:name"`.
 * @param path The NBT path inside the storage.
 * @param default The compile-time default value.
 */
context(fn: Function)
fun <T : Any> Entity.storage(
	storageId: String,
	path: String,
	default: T,
) = StorageDelegate(storageId, path, fn, default)
