package io.github.ayfri.kore.commands.scoreboard

import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function

class Player(private val fn: Function, val target: ScoreHolderArgument) {
	val players = Players(fn)
	fun objective(name: String) = PlayerObjective(fn, target, name)
	fun objective(name: String, block: PlayerObjective.() -> Command) = PlayerObjective(fn, target, name).block()

	fun add(objective: String, score: Int) = players.add(target, objective, score)
	fun enable(objective: String) = players.enable(target, objective)
	fun list() = players.list(target)
	fun operation(objective: String, operation: Operation, source: ScoreHolderArgument, sourceObjective: String) =
		players.operation(target, objective, operation, source, sourceObjective)

	fun remove(objective: String, score: Int) = players.remove(target, objective, score)
	fun reset(objective: String? = null) = players.reset(target, objective)

	operator fun get(objective: String) = players.get(target, objective)
	operator fun set(objective: String, score: Int) = players.set(target, objective, score)
}
