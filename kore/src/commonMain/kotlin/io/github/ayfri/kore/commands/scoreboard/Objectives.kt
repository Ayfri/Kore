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

class Objectives(private val fn: Function) {
	fun add(name: String, criteria: ScoreboardCriterion = ScoreboardCriteria.DUMMY, displayName: ChatComponents? = null) =
		Objective(fn, name).add(criteria, displayName)

	fun add(
		name: String,
		criteria: ScoreboardCriterion,
		displayName: String,
		color: Color? = null,
		block: PlainTextComponent.() -> Unit = {},
	) =
		add(name, criteria, textComponent(displayName, color, block))

	fun clearNumberFormat(name: String) = Objective(fn, name).clearNumberFormat()

	fun list() = fn.addLine(command("scoreboard", literal("objectives"), literal("list")))

	fun modifyDisplayAutoUpdate(name: String, autoUpdate: Boolean) = Objective(fn, name).modifyDisplayAutoUpdate(autoUpdate)

	fun modifyDisplayName(name: String, displayName: ChatComponents) = Objective(fn, name).modifyDisplayName(displayName)

	fun modifyDisplayName(name: String, displayName: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		modifyDisplayName(name, textComponent(displayName, color, block))

	fun modifyNumberFormatBlank(name: String) = Objective(fn, name).modifyNumberFormatBlank()

	fun modifyNumberFormatFixed(name: String, fixed: ChatComponents) = Objective(fn, name).modifyNumberFormatFixed(fixed)

	fun modifyNumberFormatFixed(name: String, fixed: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		modifyNumberFormatFixed(name, textComponent(fixed, color, block))

	fun modifyNumberFormatStyled(name: String, style: Style) = Objective(fn, name).modifyNumberFormatStyled(style)

	fun modifyNumberFormatStyled(name: String, style: Style.() -> Unit) = modifyNumberFormatStyled(name, Style().apply(style))

	fun modifyRenderType(name: String, renderType: RenderType) = Objective(fn, name).modifyRenderType(renderType)

	fun remove(name: String) = Objective(fn, name).remove()

	fun setDisplay(slot: DisplaySlot, name: String) = Objective(fn, name).setDisplaySlot(slot)

	fun setRenderType(name: String, renderType: RenderType) = Objective(fn, name).setRenderType(renderType)
}
