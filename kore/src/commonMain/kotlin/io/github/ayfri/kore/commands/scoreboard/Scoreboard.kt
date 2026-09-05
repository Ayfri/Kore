package io.github.ayfri.kore.commands.scoreboard

import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function

/**
 * Builder for the `/scoreboard` command.
 *
 * The scoreboard DSL groups objective management, player score edits, and target-scoped helpers in
 * one entry point so you can keep related score logic together.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/scoreboard)
 */
class Scoreboard(private val fn: Function) {
	val objectives = Objectives(fn)
	val players = Players(fn)

	/** Opens the objective-management DSL. */
	fun objectives(block: Objectives.() -> Command) = Objectives(fn).block()

	/** Opens the player-objective DSL for [target] and [objective]. */
	fun objective(target: ScoreHolderArgument, objective: String, block: PlayerObjective.() -> Command) =
		PlayerObjective(fn, target, objective).block()

	/** Returns the player-objective DSL for [target] and [objective]. */
	fun objective(target: ScoreHolderArgument, objective: String) = PlayerObjective(fn, target, objective)
	/** Returns the objective DSL for [objective]. */
	fun objective(objective: String) = Objective(fn, objective)
	/** Opens the objective DSL for [objective]. */
	fun objective(objective: String, block: Objective.() -> Command) = Objective(fn, objective).block()

	/** Opens the player-management DSL. */
	fun players(block: Players.() -> Command) = Players(fn).block()
	/** Opens the score DSL for [target]. */
	fun player(target: ScoreHolderArgument, block: Player.() -> Command) = Player(fn, target).block()
}

/** Returns the reusable [/scoreboard](https://minecraft.wiki/w/Commands/scoreboard) DSL. */
val Function.scoreboard get() = Scoreboard(this)

/** Opens the reusable [/scoreboard](https://minecraft.wiki/w/Commands/scoreboard) DSL. */
fun Function.scoreboard(block: Scoreboard.() -> Command) = Scoreboard(this).block()
