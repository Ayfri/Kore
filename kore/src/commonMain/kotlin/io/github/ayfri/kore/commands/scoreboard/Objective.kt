package io.github.ayfri.kore.commands.scoreboard

import io.github.ayfri.kore.arguments.DisplaySlot
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.scores.ScoreboardCriterion
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.utils.asArg
import io.github.ayfri.kore.utils.snbtSerializer
import kotlinx.serialization.encodeToString

class Objective(private val fn: Function, val objective: String) {
	fun player(target: ScoreHolderArgument) = PlayerObjective(fn, target, objective)
	fun player(target: ScoreHolderArgument, block: PlayerObjective.() -> Command) = PlayerObjective(fn, target, objective).block()

	fun add(criteria: ScoreboardCriterion = ScoreboardCriteria.DUMMY, displayName: ChatComponents? = null) = create(criteria, displayName)
	fun add(
		criteria: ScoreboardCriterion = ScoreboardCriteria.DUMMY,
		displayName: String,
		color: Color? = null,
		block: PlainTextComponent.() -> Unit = {},
	) =
		create(criteria, textComponent(displayName, color, block))

	fun clearNumberFormat() = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(objective),
			literal("numberformat"),
		)
	)

	fun create(criteria: ScoreboardCriterion = ScoreboardCriteria.DUMMY, displayName: ChatComponents? = null) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("add"),
			literal(objective),
			criteria,
			displayName?.asSnbtArg()
		)
	)

	fun create(
		criteria: ScoreboardCriterion = ScoreboardCriteria.DUMMY,
		displayName: String,
		color: Color? = null,
		block: PlainTextComponent.() -> Unit = {},
	) =
		create(criteria, textComponent(displayName, color, block))

	fun modifyDisplayAutoUpdate(autoUpdate: Boolean) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(objective),
			literal("displayautoupdate"),
			bool(autoUpdate)
		)
	)

	fun modifyDisplayName(displayName: ChatComponents) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(objective),
			literal("displayname"),
			displayName.asSnbtArg()
		)
	)

	fun modifyDisplayName(displayName: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		modifyDisplayName(textComponent(displayName, color, block))

	fun modifyNumberFormatBlank() = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(objective),
			literal("numberformat"),
			literal("blank")
		)
	)

	fun modifyNumberFormatFixed(fixed: ChatComponents) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(objective),
			literal("numberformat"),
			literal("fixed"),
			fixed.asSnbtArg()
		)
	)

	fun modifyNumberFormatFixed(fixed: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		modifyNumberFormatFixed(textComponent(fixed, color, block))

	fun modifyNumberFormatStyled(style: Style) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(objective),
			literal("numberformat"),
			literal("styled"),
			literal(snbtSerializer.encodeToString(style))
		)
	)

	fun modifyNumberFormatStyled(block: Style.() -> Unit) = modifyNumberFormatStyled(Style().apply(block))

	fun modifyRenderType(renderType: RenderType) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(objective),
			literal("rendertype"),
			literal(renderType.asArg())
		)
	)

	fun remove() = fn.addLine(command("scoreboard", literal("objectives"), literal("remove"), literal(objective)))

	fun setDisplaySlot(slot: DisplaySlot) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("setdisplay"),
			slot,
			literal(objective)
		)
	)

	fun setRenderType(renderType: RenderType) = modifyRenderType(renderType)

	fun add(target: ScoreHolderArgument, amount: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("add"), target, literal(objective), int(amount)))

	fun enable(target: ScoreHolderArgument) =
		fn.addLine(command("scoreboard", literal("players"), literal("enable"), target, literal(objective)))

	fun operation(
		target: ScoreHolderArgument,
		operation: Operation,
		source: ScoreHolderArgument,
		sourceObjective: String,
	) =
		fn.addLine(
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

	fun remove(target: ScoreHolderArgument, amount: Int) = fn.addLine(
		command(
			"scoreboard",
			literal("players"),
			literal("remove"),
			target,
			literal(objective),
			int(amount)
		)
	)

	fun reset(target: ScoreHolderArgument) =
		fn.addLine(command("scoreboard", literal("players"), literal("reset"), target, literal(objective)))

	operator fun get(target: ScoreHolderArgument) =
		fn.addLine(command("scoreboard", literal("players"), literal("get"), target, literal(objective)))

	operator fun set(target: ScoreHolderArgument, amount: Int) =
		fn.addLine(command("scoreboard", literal("players"), literal("set"), target, literal(objective), int(amount)))
}
