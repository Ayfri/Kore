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
	objective(entity.asSelector(), name) {
		add(value)
	}
}

context(fn: Function)
fun ScoreboardEntity.remove(value: Int) = fn.scoreboard {
	objective(entity.asSelector(), name) {
		remove(value)
	}
}

context(fn: Function)
fun ScoreboardEntity.set(value: Int) = fn.scoreboard {
	objective(entity.asSelector(), name) {
		set(value)
	}
}

context(fn: Function)
fun ScoreboardEntity.reset() = fn.scoreboard {
	objective(entity.asSelector(), name) {
		reset()
	}
}

context(fn: Function)
fun ScoreboardEntity.copyTo(target: ScoreHolderArgument, sourceObjective: String) = fn.scoreboard {
	objective(entity.asSelector(), name) {
		operation(Operation.SET, target, sourceObjective)
	}
}

context(fn: Function)
fun ScoreboardEntity.copyFrom(source: ScoreHolderArgument, sourceObjective: String) = fn.scoreboard {
	objective(source, sourceObjective) {
		operation(Operation.SET, entity.asSelector(), name)
	}
}

/** Stores the current numeric NBT value from [source] at [path] into this score. */
context(fn: Function)
fun ScoreboardEntity.copyDataFrom(source: Entity, path: String, scale: Double = 1.0) = fn.execute {
	storeResult { score(entity.asSelector(), name) }
	run {
		fn.data(source.asSelector()) {
			get(path, scale)
		}
	}
}

/** Stores the current numeric NBT value from [source] at [path] into this score. */
context(fn: Function)
fun ScoreboardEntity.copyDataFrom(source: StorageArgument, path: String, scale: Double = 1.0) = fn.execute {
	storeResult { score(entity.asSelector(), name) }
	run {
		fn.data(source) {
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
			fn.scoreboard {
				players {
					get(entity.asSelector(), name)
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
			fn.scoreboard {
				players {
					get(entity.asSelector(), name)
				}
			}
		}
	}

context(fn: Function)
operator fun ScoreboardEntity.plusAssign(value: Int) {
	add(value)
}

context(fn: Function)
operator fun ScoreboardEntity.minusAssign(value: Int) {
	remove(value)
}
