package io.github.ayfri.kore.commands.scoreboard

import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function

class Scoreboard(private val fn: Function) {
	val objectives = Objectives(fn)
	val players = Players(fn)

	fun objectives(block: Objectives.() -> Command) = Objectives(fn).block()

	fun objective(target: ScoreHolderArgument, objective: String, block: PlayerObjective.() -> Command) =
		PlayerObjective(fn, target, objective).block()

	fun objective(target: ScoreHolderArgument, objective: String) = PlayerObjective(fn, target, objective)
	fun objective(objective: String) = Objective(fn, objective)
	fun objective(objective: String, block: Objective.() -> Command) = Objective(fn, objective).block()

	fun players(block: Players.() -> Command) = Players(fn).block()
	fun player(target: ScoreHolderArgument, block: Player.() -> Command) = Player(fn, target).block()
}

val Function.scoreboard get() = Scoreboard(this)
fun Function.scoreboard(block: Scoreboard.() -> Command) = Scoreboard(this).block()
