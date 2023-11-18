package io.github.ayfri.kore.commands.scoreboard

import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.utils.asArg

class Players(private val fn: Function) {
	fun add(target: ScoreHolderArgument, objective: String, score: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("add"), target, literal(objective), int(score)))

	fun enable(target: ScoreHolderArgument, objective: String) =
		fn.addLine(command("scoreboard", literal("players"), literal("enable"), target, literal(objective)))

	fun get(target: ScoreHolderArgument, objective: String) =
		fn.addLine(command("scoreboard", literal("players"), literal("get"), target, literal(objective)))

	fun list(target: ScoreHolderArgument? = null) =
		fn.addLine(command("scoreboard", literal("players"), literal("list"), target))

	fun operation(
		target: ScoreHolderArgument,
		objective: String,
		operation: Operation,
		source: ScoreHolderArgument,
		sourceObjective: String,
	) = fn.addLine(
		command(
			"scoreboard",
			literal("players"),
			literal("operation"),
			target,
			literal(objective),
			literal(operation.asArg()),
			source,
			literal(sourceObjective)
		)
	)

	fun remove(target: ScoreHolderArgument, objective: String, score: Int) = fn.addLine(
		command(
			"scoreboard", literal("players"), literal("remove"), target, literal(objective), int(score)
		)
	)

	fun reset(target: ScoreHolderArgument, objective: String? = null) =
		fn.addLine(command("scoreboard", literal("players"), literal("reset"), target, literal(objective)))

	fun set(target: ScoreHolderArgument, objective: String, score: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("set"), target, literal(objective), int(score)))
}
