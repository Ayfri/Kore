package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.DisplaySlot
import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.entity
import io.github.ayfri.kore.commands.execute.ExecuteCondition
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.execute.run
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function
import kotlin.math.abs

data class ScoreboardDisplay(val name: String) {
	val lines = mutableListOf<ScoreboardLine>()
	var displayName: ChatComponents? = null
	var startingScore = -1
	var decreasing = true

	context(Function)
	fun appendLine(text: ChatComponents? = null, init: ScoreboardLine.() -> Unit = {}) {
		val line = ScoreboardLine(this, startingScore + if (decreasing) -lines.size else lines.size)
		line.text = text
		line.init()
		lines.add(line)
	}

	context(Function)
	fun appendLine(text: String, color: Color? = null, componentBlock: PlainTextComponent.() -> Unit = {}) =
		appendLine(textComponent(text, color ?: Color.WHITE, componentBlock))

	context(Function)
	fun create(display: Boolean = true, slot: DisplaySlot = DisplaySlots.sidebar) {
		scoreboard.objectives.add(name, displayName = displayName)
		lines.forEach { it.create() }
		if (display) display(slot)
	}

	context(Function)
	fun display(slot: DisplaySlot = DisplaySlots.sidebar) {
		scoreboard.objectives.setDisplay(slot, name)
	}

	context(Function)
	fun emptyLine() = appendLine()

	context(Function)
	fun setLine(index: Int, text: ChatComponents? = null, init: ScoreboardLine.() -> Unit = {}) {
		for (i in lines.size..abs(index)) emptyLine()

		val line = ScoreboardLine(this, startingScore + if (decreasing) -index else index)
		line.text = text
		line.init()
		lines.add(line)
	}

	context(Function)
	fun setLine(index: Int, text: String, color: Color? = null, componentBlock: PlainTextComponent.() -> Unit = {}) =
		setLine(index, textComponent(text, color ?: Color.WHITE, componentBlock))

	context(Function)
	fun remove() {
		scoreboard.objectives.remove(name)
	}

	companion object {
		context(Function)
		fun resetAll() {
			for (i in 0..15) {
				scoreboard.players.reset(entity("§${i.toString(16)}"))
			}
		}
	}
}

fun Function.scoreboardDisplay(
	name: String,
	display: Boolean = true,
	displaySlot: DisplaySlot = DisplaySlots.sidebar,
	init: ScoreboardDisplay.() -> Unit,
): ScoreboardDisplay {
	val scoreboardDisplay = ScoreboardDisplay(name)
	scoreboardDisplay.init()
	scoreboardDisplay.remove()
	scoreboardDisplay.create(display, displaySlot)
	return scoreboardDisplay
}

data class ScoreboardLine(private val sc: ScoreboardDisplay, val index: Int) {
	val fakePlayer = "§${abs(index).toString(16)}"
	var condition: (ExecuteCondition.() -> Unit)? = null
	var text: ChatComponents? = null
		set(value) {
			value?.requireSimpleComponents()
			field = value
		}

	context(Function)
	private fun setText() = scoreboard.players.set(entity(fakePlayer), sc.name, index).let { command ->
		text?.let {
			scoreboard.players.displayName(entity(fakePlayer), sc.name, it)
		} ?: command
	}

	fun condition(condition: ExecuteCondition.() -> Unit) {
		this.condition = condition
	}

	context(Function)
	fun create() {
		if (condition != null) return createIf(condition!!)
		setText()
	}

	context(Function)
	fun createIf(condition: ExecuteCondition.() -> Unit) {
		execute {
			ifCondition(condition)

			run { setText() }
		}
	}

	context(Function)
	fun createUnless(condition: ExecuteCondition.() -> Unit) {
		execute {
			unlessCondition(condition)

			run {
				setText()
			}
		}
	}

	context(Function)
	fun remove() {
		scoreboard.players.reset(entity(fakePlayer))
		scoreboard.players.clearDisplayName(entity(fakePlayer), sc.name)
	}
}
