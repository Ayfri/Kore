package io.github.ayfri.kore.commands.scoreboard

import io.github.ayfri.kore.arguments.DisplaySlot
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.scores.ScoreboardCriterion
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.encodeToString

class Objectives(private val fn: Function) {
	fun add(name: String, criteria: ScoreboardCriterion = ScoreboardCriteria.DUMMY, displayName: ChatComponents? = null) =
		fn.addLine(
			command(
				"scoreboard",
				literal("objectives"),
				literal("add"),
				literal(name),
				criteria,
				displayName?.asJsonArg()
			)
		)

	fun add(
		name: String,
		criteria: ScoreboardCriterion,
		displayName: String,
		color: Color? = null,
		block: PlainTextComponent.() -> Unit = {},
	) =
		add(name, criteria, textComponent(displayName, color, block))

	fun clearNumberFormat(name: String) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(name),
			literal("numberformat"),
		)
	)

	fun list() = fn.addLine(command("scoreboard", literal("objectives"), literal("list")))

	fun modifyDisplayAutoUpdate(name: String, autoUpdate: Boolean) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(name),
			literal("displayautoupdate"),
			literal(autoUpdate.asArg())
		)
	)

	fun modifyDisplayName(name: String, displayName: ChatComponents) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(name),
			literal("displayname"),
			displayName.asJsonArg()
		)
	)

	fun modifyDisplayName(name: String, displayName: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		modifyDisplayName(name, textComponent(displayName, color, block))

	fun modifyNumberFormatBlank(name: String) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(name),
			literal("numberformat"),
			literal("blank")
		)
	)

	fun modifyNumberFormatFixed(name: String, fixed: ChatComponents) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(name),
			literal("numberformat"),
			literal("fixed"),
			fixed.asJsonArg()
		)
	)

	fun modifyNumberFormatFixed(name: String, fixed: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		modifyNumberFormatFixed(name, textComponent(fixed, color, block))

	fun modifyNumberFormatStyled(name: String, style: Style) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(name),
			literal("numberformat"),
			literal("styled"),
			literal(fn.datapack.jsonEncoder.encodeToString(style))
		)
	)

	fun modifyNumberFormatStyled(name: String, style: Style.() -> Unit) = modifyNumberFormatStyled(name, Style().apply(style))

	fun modifyRenderType(name: String, renderType: RenderType) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("modify"),
			literal(name),
			literal("rendertype"),
			literal(renderType.asArg())
		)
	)

	fun remove(name: String) =
		fn.addLine(command("scoreboard", literal("objectives"), literal("remove"), literal(name)))

	fun setDisplay(slot: DisplaySlot, name: String) = fn.addLine(
		command(
			"scoreboard",
			literal("objectives"),
			literal("setdisplay"),
			slot,
			literal(name)
		)
	)
}
