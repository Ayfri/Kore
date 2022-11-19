package scoreboard

import arguments.Argument
import commands.Operation
import commands.scoreboard
import entities.Entity
import functions.Function

open class ScoreboardEntity(name: String, val entity: Entity) : Scoreboard(name)

fun scoreboard(name: String, entity: Entity, init: ScoreboardEntity.() -> Unit = {}) = ScoreboardEntity(name, entity).apply(init)

context(Function)
fun ScoreboardEntity.add(value: Int) = scoreboard {
	objective(entity.asSelector(), name) {
		add(value)
	}
}

context(Function)
fun ScoreboardEntity.remove(value: Int) = scoreboard {
	objective(entity.asSelector(), name) {
		remove(value)
	}
}

context(Function)
fun ScoreboardEntity.set(value: Int) = scoreboard {
	objective(entity.asSelector(), name) {
		set(value)
	}
}

context(Function)
fun ScoreboardEntity.reset() = scoreboard {
	objective(entity.asSelector(), name) {
		reset()
	}
}

context(Function)
fun ScoreboardEntity.copyTo(target: Argument.ScoreHolder, sourceObjective: String) = scoreboard {
	objective(entity.asSelector(), name) {
		operation(Operation.SET, target, sourceObjective)
	}
}

context(Function)
fun ScoreboardEntity.copyFrom(source: Argument.ScoreHolder, sourceObjective: String) = scoreboard {
	objective(source, sourceObjective) {
		operation(Operation.SET, entity.asSelector(), name)
	}
}

context(Function)
	operator fun ScoreboardEntity.plusAssign(value: Int) = add(value)

context(Function)
	operator fun ScoreboardEntity.minusAssign(value: Int) = remove(value)
