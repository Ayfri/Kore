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

	fun list() = fn.addLine(command("scoreboard", literal("objectives"), literal("list")))
	fun modifyDisplayName(name: String, displayName: ChatComponents) =
		fn.addLine(
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

	fun modifyRenderType(name: String, renderType: RenderType) =
		fn.addLine(
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

	fun setDisplay(slot: DisplaySlot, name: String) =
		fn.addLine(
			command(
				"scoreboard",
				literal("objectives"),
				literal("setdisplay"),
				slot,
				literal(name)
			)
		)
}
