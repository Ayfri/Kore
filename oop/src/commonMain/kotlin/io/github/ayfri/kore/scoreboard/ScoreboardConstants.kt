package io.github.ayfri.kore.scoreboard

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.entities.FakePlayer
import io.github.ayfri.kore.functions.Function

private val emittedConstants = mutableMapOf<Function, MutableSet<Int>>()

/**
 * Returns the fake-player score holding [value], emitting `OopConstants.constantsObjective` and its
 * `scoreboard players set` the first time that constant is used inside the current function.
 *
 * `scoreboard players operation` has no literal right-hand side, so operators such as `*=`, `/=` and `%=` against a
 * plain `Int` need a holder to read from.
 */
context(fn: Function)
fun scoreboardConstant(value: Int): ScoreboardEntity {
	val holder = FakePlayer("#$value")

	if (emittedConstants.getOrPut(fn) { mutableSetOf() }.add(value)) {
		fn.scoreboard.objectives.add(OopConstants.constantsObjective)
		fn.scoreboard.players.set(holder.asScoreHolder(), OopConstants.constantsObjective, value)
	}

	return ScoreboardEntity(OopConstants.constantsObjective, holder)
}
