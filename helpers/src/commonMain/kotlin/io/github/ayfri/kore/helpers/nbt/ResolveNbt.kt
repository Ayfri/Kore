package io.github.ayfri.kore.helpers.nbt

import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.helpers.state.ScoreboardDelegate
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import net.benwoodworth.knbt.NbtByte
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtDouble
import net.benwoodworth.knbt.NbtFloat
import net.benwoodworth.knbt.NbtInt
import net.benwoodworth.knbt.NbtLong
import net.benwoodworth.knbt.NbtShort
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put

/** One runtime value of a resolved tree, emitted as its own `execute store result` line. */
data class ResolvedScore(
	val path: NbtPath,
	val holder: ScoreHolderArgument,
	val objective: String,
	val type: DataType,
	val scale: Double,
)

/**
 * Builds one NBT tree mixing literals and scores.
 *
 * Literals are collected into a single compound written with `data modify ... set value`, while every score becomes
 * an `execute store result` line targeting its own path. Both halves are derived from the same declaration, so they
 * cannot drift apart the way a hand-written `merge` plus `store` pair does.
 */
class ResolveNbtScope internal constructor(private val basePath: NbtPath) {
	private val statics = mutableMapOf<String, NbtTag>()
	internal val dynamics = mutableListOf<ResolvedScore>()

	operator fun set(name: String, value: NbtTag) {
		statics[name] = value
	}

	operator fun set(name: String, value: Boolean) = set(name, NbtByte(value))
	operator fun set(name: String, value: Byte) = set(name, NbtByte(value))
	operator fun set(name: String, value: Short) = set(name, NbtShort(value))
	operator fun set(name: String, value: Int) = set(name, NbtInt(value))
	operator fun set(name: String, value: Long) = set(name, NbtLong(value))
	operator fun set(name: String, value: Float) = set(name, NbtFloat(value))
	operator fun set(name: String, value: Double) = set(name, NbtDouble(value))
	operator fun set(name: String, value: String) = set(name, NbtString(value))

	/** Resolves [score] into [name] at runtime, as an `int` with no scaling. */
	operator fun set(name: String, score: ScoreboardEntity) = set(name, score, DataType.INT)

	/** Resolves [score] into [name] at runtime, as an `int` with no scaling. */
	operator fun set(name: String, score: ScoreboardDelegate) = set(name, score.scoreboardEntity(), DataType.INT)

	/**
	 * Resolves [score] into [name] at runtime.
	 *
	 * Use [scale] together with a float [type] for fixed-point scores, for example `scale = 0.001` to turn a score
	 * counting thousandths into a `double`.
	 */
	fun set(name: String, score: ScoreboardEntity, type: DataType = DataType.INT, scale: Double = 1.0) {
		dynamics += ResolvedScore(basePath / name, score.entity.asScoreHolder(), score.name, type, scale)
	}

	/** Resolves [score] into [name] at runtime. */
	fun set(name: String, score: ScoreboardDelegate, type: DataType = DataType.INT, scale: Double = 1.0) =
		set(name, score.scoreboardEntity(), type, scale)

	/** Declares a nested compound under [name]. */
	fun compound(name: String, block: ResolveNbtScope.() -> Unit) {
		val child = ResolveNbtScope(basePath / name).apply(block)
		val nested = child.asNbt()
		if (nested.isNotEmpty()) statics[name] = nested
		dynamics += child.dynamics
	}

	internal fun asNbt(): NbtCompound = buildNbtCompound { statics.forEach { (key, value) -> put(key, value) } }
}

private fun emit(
	scope: ResolveNbtScope,
	writeStatic: (NbtCompound) -> Command,
	writeScore: (ResolvedScore) -> Command,
): List<Command> {
	val commands = mutableListOf<Command>()
	val static = scope.asNbt()
	if (static.isNotEmpty()) commands += writeStatic(static)
	scope.dynamics.mapTo(commands, writeScore)
	return commands
}

/**
 * Writes a mixed literal/score NBT tree into [target] at [path].
 *
 * ```kotlin
 * resolveNbt(storage("shop", "kore"), nbtPath("offer")) {
 *     this["item"] = "minecraft:diamond"
 *     this["price"] = playerPrice
 *     compound("owner") { this["id"] = ownerId }
 * }
 * ```
 *
 * Emits one `data modify ... set value` for the literal half (or `data merge` when [path] is the root), then one
 * `execute store result storage` per score.
 *
 * @return every emitted command, in order.
 */
context(fn: Function)
fun resolveNbt(target: StorageArgument, path: NbtPath = NbtPath(), block: ResolveNbtScope.() -> Unit): List<Command> {
	val scope = ResolveNbtScope(path).apply(block)
	return emit(
		scope = scope,
		writeStatic = { static -> if (path.isRoot) fn.data(target).merge(static) else fn.data(target).modify(path, static) },
		writeScore = { resolved ->
			fn.execute {
				storeResult { storage(target, resolved.path, resolved.type, resolved.scale) }
				run { scoreboard { players { get(resolved.holder, resolved.objective) } } }
			}
		},
	)
}

/** Writes a mixed literal/score NBT tree into [target]'s NBT at [path]. */
context(fn: Function)
fun resolveNbt(target: Entity, path: NbtPath = NbtPath(), block: ResolveNbtScope.() -> Unit): List<Command> {
	val scope = ResolveNbtScope(path).apply(block)
	val selector = target.asSelector()
	return emit(
		scope = scope,
		writeStatic = { static ->
			if (path.isRoot) fn.data(selector).merge(static) else fn.data(selector).modify(path, static)
		},
		writeScore = { resolved ->
			fn.execute {
				storeResult { entity(selector, resolved.path, resolved.type, resolved.scale) }
				run { scoreboard { players { get(resolved.holder, resolved.objective) } } }
			}
		},
	)
}
