package io.github.ayfri.kore.scoreboard

import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.entities.storeCountIn
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.teams.Team
import io.github.ayfri.kore.teams.members

open class ScoreboardEntity(name: String, val entity: Entity) : Scoreboard(name)

fun scoreboard(name: String, entity: Entity, init: ScoreboardEntity.() -> Unit = {}) =
	ScoreboardEntity(name, entity).apply(init)

context(fn: Function)
fun ScoreboardEntity.add(value: Int) = fn.scoreboard {
	objective(entity.asScoreHolder(), name) {
		add(value)
	}
}

context(fn: Function)
fun ScoreboardEntity.remove(value: Int) = fn.scoreboard {
	objective(entity.asScoreHolder(), name) {
		remove(value)
	}
}

context(fn: Function)
fun ScoreboardEntity.set(value: Int) = fn.scoreboard {
	objective(entity.asScoreHolder(), name) {
		set(value)
	}
}

context(fn: Function)
fun ScoreboardEntity.reset() = fn.scoreboard {
	objective(entity.asScoreHolder(), name) {
		reset()
	}
}

context(fn: Function)
fun ScoreboardEntity.copyTo(target: ScoreHolderArgument, sourceObjective: String) = fn.scoreboard {
	objective(entity.asScoreHolder(), name) {
		operation(Operation.SET, target, sourceObjective)
	}
}

context(fn: Function)
fun ScoreboardEntity.copyFrom(source: ScoreHolderArgument, sourceObjective: String) = fn.scoreboard {
	objective(source, sourceObjective) {
		operation(Operation.SET, entity.asScoreHolder(), name)
	}
}

/** Stores the current numeric NBT value from [source] at [path] into this score. */
context(fn: Function)
fun ScoreboardEntity.copyDataFrom(source: Entity, path: String, scale: Double = 1.0) = fn.execute {
	storeResult { score(entity.asScoreHolder(), name) }
	run {
		data(source.asSelector()) {
			get(path, scale)
		}
	}
}

/** Stores the current numeric NBT value from [source] at [path] into this score. */
context(fn: Function)
fun ScoreboardEntity.copyDataFrom(source: StorageArgument, path: String, scale: Double = 1.0) = fn.execute {
	storeResult { score(entity.asScoreHolder(), name) }
	run {
		data(source) {
			get(path, scale)
		}
	}
}

/** Stores how many entities currently match [source] into this score. */
context(fn: Function)
fun ScoreboardEntity.copyEntityCountFrom(source: Entity) = source.storeCountIn(this)

/** Stores how many members currently belong to [source] into this score. */
context(fn: Function)
fun ScoreboardEntity.copyMemberCountFrom(source: Team) = copyEntityCountFrom(source.members())

/** Stores this score value into an entity NBT path. */
context(fn: Function)
fun ScoreboardEntity.copyTo(target: Entity, path: String, type: DataType = DataType.INT, scale: Double = 1.0) =
	fn.execute {
		storeResult { entity(target.asSelector(), path, type, scale) }
		run {
			scoreboard {
				players {
					get(entity.asScoreHolder(), this@copyTo.name)
				}
			}
		}
	}

/** Stores this score value into a storage NBT path. */
context(fn: Function)
fun ScoreboardEntity.copyTo(target: StorageArgument, path: String, type: DataType = DataType.INT, scale: Double = 1.0) =
	fn.execute {
		storeResult { storage(target, path, type, scale) }
		run {
			scoreboard {
				players {
					get(entity.asScoreHolder(), this@copyTo.name)
				}
			}
		}
	}

/** Emits `scoreboard players operation <this> <operation> <source>`. */
context(fn: Function)
fun ScoreboardEntity.operation(operation: Operation, source: ScoreboardEntity) = fn.scoreboard.players.operation(
	target = entity.asScoreHolder(),
	objective = name,
	operation = operation,
	source = source.entity.asScoreHolder(),
	sourceObjective = source.name,
)

context(fn: Function)
operator fun ScoreboardEntity.plusAssign(value: Int) {
	add(value)
}

context(fn: Function)
operator fun ScoreboardEntity.minusAssign(value: Int) {
	remove(value)
}

context(fn: Function)
operator fun ScoreboardEntity.plusAssign(other: ScoreboardEntity) {
	operation(Operation.ADD, other)
}

context(fn: Function)
operator fun ScoreboardEntity.minusAssign(other: ScoreboardEntity) {
	operation(Operation.REMOVE, other)
}

context(fn: Function)
operator fun ScoreboardEntity.timesAssign(other: ScoreboardEntity) {
	operation(Operation.MULTIPLY, other)
}

context(fn: Function)
operator fun ScoreboardEntity.divAssign(other: ScoreboardEntity) {
	operation(Operation.DIVIDE, other)
}

context(fn: Function)
operator fun ScoreboardEntity.remAssign(other: ScoreboardEntity) {
	operation(Operation.MODULO, other)
}

/** Multiplies this score by [value], through the constant holder returned by [scoreboardConstant]. */
context(fn: Function)
operator fun ScoreboardEntity.timesAssign(value: Int) {
	operation(Operation.MULTIPLY, scoreboardConstant(value))
}

/** Divides this score by [value] using Minecraft's floored division, through [scoreboardConstant]. */
context(fn: Function)
operator fun ScoreboardEntity.divAssign(value: Int) {
	operation(Operation.DIVIDE, scoreboardConstant(value))
}

/** Reduces this score modulo [value] using Minecraft's floored modulo, through [scoreboardConstant]. */
context(fn: Function)
operator fun ScoreboardEntity.remAssign(value: Int) {
	operation(Operation.MODULO, scoreboardConstant(value))
}

/** Copies [other] into this score. */
context(fn: Function)
infix fun ScoreboardEntity.setTo(other: ScoreboardEntity) = operation(Operation.SET, other)

/** Keeps the smaller of this score and [other]. */
context(fn: Function)
infix fun ScoreboardEntity.minWith(other: ScoreboardEntity) = operation(Operation.MIN, other)

/** Keeps the larger of this score and [other]. */
context(fn: Function)
infix fun ScoreboardEntity.maxWith(other: ScoreboardEntity) = operation(Operation.MAX, other)

/** Swaps this score with [other]. */
context(fn: Function)
infix fun ScoreboardEntity.swapWith(other: ScoreboardEntity) = operation(Operation.SWAP, other)
