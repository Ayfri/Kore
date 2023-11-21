package io.github.ayfri.kore.commands.scoreboard

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.functions.Function

class PlayerObjective(fn: Function, val target: ScoreHolderArgument, val objective: String) {
	val players = Players(fn)

	fun add(score: Int) = players.add(target, objective, score)

	fun clearDisplayNumberFormat() = players.clearDisplayNumberFormat(target, objective)

	fun displayNumberFormatBlank() = players.displayNumberFormatBlank(target, objective)
	fun displayNumberFormatFixed(fixed: ChatComponents) =
		players.displayNumberFormatFixed(target, objective, fixed)

	fun displayNumberFormatFixed(fixed: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
		players.displayNumberFormatFixed(target, objective, textComponent(fixed, color, block))

	fun displayNumberFormatStyled(style: Style) = players.displayNumberFormatStyled(target, objective, style)
	fun displayNumberFormatStyled(block: Style.() -> Unit) = players.displayNumberFormatStyled(target, objective, block)

	fun enable() = players.enable(target, objective)
	fun get() = players.get(target, objective)
	fun operation(operation: Operation, source: ScoreHolderArgument, sourceObjective: String) =
		players.operation(target, objective, operation, source, sourceObjective)

	fun operation(operation: Operation, source: PlayerObjective) =
		players.operation(target, objective, operation, source.target, source.objective)

	fun remove(score: Int) = players.remove(target, objective, score)
	fun reset() = players.reset(target, objective)
	fun set(score: Int) = players.set(target, objective, score)

	operator fun plusAssign(score: Int) {
		add(score)
	}

	operator fun minusAssign(score: Int) {
		remove(score)
	}

	operator fun inc() = apply { add(1) }

	operator fun dec() = apply { remove(1) }

	infix fun min(obj: PlayerObjective) {
		operation(Operation.MIN, obj.target, obj.objective)
	}

	infix fun max(obj: PlayerObjective) {
		operation(Operation.MAX, obj.target, obj.objective)
	}
}
