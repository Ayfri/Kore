package io.github.ayfri.kore.scoreboard

import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function

open class ScoreboardEntity(name: String, val entity: Entity) : Scoreboard(name)

fun scoreboard(name: String, entity: Entity, init: ScoreboardEntity.() -> Unit = {}) = ScoreboardEntity(name, entity).apply(init)

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

context(fn: Function)
operator fun ScoreboardEntity.plusAssign(value: Int) {
	add(value)
}

context(fn: Function)
operator fun ScoreboardEntity.minusAssign(value: Int) {
	remove(value)
}
