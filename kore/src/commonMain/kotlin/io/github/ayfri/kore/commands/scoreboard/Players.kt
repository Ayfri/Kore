package io.github.ayfri.kore.commands.scoreboard

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.utils.asArg
import io.github.ayfri.kore.utils.snbtSerializer
import kotlinx.serialization.encodeToString

class Players(private val fn: Function) {
	fun add(target: ScoreHolderArgument, objective: String, score: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("add"), target, literal(objective), int(score)))

	fun clearDisplayName(target: ScoreHolderArgument, objective: String) = fn.addLine(
		command(
			"scoreboard",
			literal("players"),
			literal("display"),
			literal("name"),
			target,
			literal(objective),
		)
	)

	fun clearDisplayNumberFormat(target: ScoreHolderArgument, objective: String) = fn.addLine(
		command(
			"scoreboard",
			literal("players"),
			literal("display"),
			literal("numberformat"),
			target,
			literal(objective),
		)
	)

	fun displayName(target: ScoreHolderArgument, objective: String, displayName: ChatComponents) = fn.addLine(
		command(
			"scoreboard",
			literal("players"),
			literal("display"),
			literal("name"),
			target,
			literal(objective),
			displayName.asSnbtArg()
		)
	)

	fun displayName(
		target: ScoreHolderArgument,
		objective: String,
		displayName: String,
		color: Color? = null,
		block: PlainTextComponent.() -> Unit = {},
	) =
		displayName(target, objective, textComponent(displayName, color, block))

	fun displayNumberFormatBlank(target: ScoreHolderArgument, objective: String) = fn.addLine(
		command(
			"scoreboard",
			literal("players"),
			literal("display"),
			literal("numberformat"),
			target,
			literal(objective),
			literal("blank")
		)
	)

	fun displayNumberFormatFixed(target: ScoreHolderArgument, objective: String, fixed: ChatComponents) = fn.addLine(
		command(
			"scoreboard",
			literal("players"),
			literal("display"),
			literal("numberformat"),
			target,
			literal(objective),
			literal("fixed"),
			fixed.asSnbtArg()
		)
	)

	fun displayNumberFormatFixed(
		target: ScoreHolderArgument,
		objective: String,
		fixed: String,
		color: Color? = null,
		block: PlainTextComponent.() -> Unit = {},
	) = displayNumberFormatFixed(target, objective, textComponent(fixed, color, block))

	fun displayNumberFormatStyled(target: ScoreHolderArgument, objective: String, style: Style) = fn.addLine(
		command(
			"scoreboard",
			literal("players"),
			literal("display"),
			literal("numberformat"),
			target,
			literal(objective),
			literal("styled"),
			literal(snbtSerializer.encodeToString(style))
		)
	)

	fun displayNumberFormatStyled(target: ScoreHolderArgument, objective: String, block: Style.() -> Unit) =
		displayNumberFormatStyled(target, objective, Style().apply(block))

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
			"scoreboard",
			literal("players"),
			literal("remove"),
			target,
			literal(objective),
			int(score)
		)
	)

	fun reset(target: ScoreHolderArgument, objective: String? = null) =
		fn.addLine(command("scoreboard", literal("players"), literal("reset"), target, literal(objective)))

	fun set(target: ScoreHolderArgument, objective: String, score: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("set"), target, literal(objective), int(score)))
}
