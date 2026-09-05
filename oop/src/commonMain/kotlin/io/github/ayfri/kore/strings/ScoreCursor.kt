package io.github.ayfri.kore.strings

import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function

/**
 * Lightweight handle around a `#fakePlayer` score holder on an explicit objective.
 *
 * Avoids the ceremony of [io.github.ayfri.kore.scoreboard.ScoreboardEntity] for internal scratch
 * scores that do not belong to any in-game entity. Each method is thin and delegates to the Kore
 * scoreboard DSL so the generated commands stay consistent with the rest of the module.
 */
internal data class ScoreCursor(val holder: String, val objective: String) {
	fun add(fn: Function, value: Int) = fn.scoreboard { players { add(asScoreHolder(), objective, value) } }

	fun addFrom(fn: Function, source: ScoreCursor) = operation(fn, Operation.ADD, source)

	fun asScoreHolder(): ScoreHolderArgument = literal(holder)

	fun assignFrom(fn: Function, source: ScoreCursor) = operation(fn, Operation.SET, source)

	fun operation(fn: Function, op: Operation, source: ScoreCursor) = fn.scoreboard {
		players {
			operation(asScoreHolder(), objective, op, source.asScoreHolder(), source.objective)
		}
	}

	fun reset(fn: Function) = fn.scoreboard { players { reset(asScoreHolder(), objective) } }

	fun set(fn: Function, value: Int) = fn.scoreboard { players { set(asScoreHolder(), objective, value) } }

	/** Stores the content of an NBT path into this cursor using `execute store result`. */
	fun storeValueOfNbt(fn: Function, target: StorageArgument, path: String, scale: Double = 1.0) =
		fn.addLine("execute store result score $holder $objective run data get storage ${target.asString()} $path $scale")

	fun sub(fn: Function, value: Int) = fn.scoreboard { players { remove(asScoreHolder(), objective, value) } }

	fun subFrom(fn: Function, source: ScoreCursor) = operation(fn, Operation.REMOVE, source)

	/** Writes this cursor value into the given NBT path using `execute store result`. */
	fun writeToStorage(
		fn: Function,
		target: StorageArgument,
		path: String,
		type: DataType = DataType.INT,
		scale: Double = 1.0,
	) = fn.addLine(
		"execute store result storage ${target.asString()} $path ${type.name.lowercase()} $scale " +
			"run scoreboard players get $holder $objective"
	)
}
