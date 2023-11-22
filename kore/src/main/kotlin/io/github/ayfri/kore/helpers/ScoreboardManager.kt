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

/**
 * A class representing a scoreboard display.
 *
 * @property name The unique name of the scoreboard display.
 * @property lines The list of scoreboard lines.
 * @property displayName The display name of the scoreboard.
 * @property startingScore The starting score for the scoreboard lines.
 * @property decreasing Specifies whether the scores should decrease or increase.
 */
data class ScoreboardDisplay(val name: String) {
	val lines = mutableListOf<ScoreboardLine>()
	var displayName: ChatComponents? = null
	var startingScore = -1
	var decreasing = true

	/**
	 * Appends a line to the scoreboard.
	 *
	 * @param text The text to be displayed on the line (optional). If not provided, the line will be empty.
	 * @param init A lambda expression to configure the line using the [ScoreboardLine] object.
	 */
	context(Function)
	fun appendLine(text: ChatComponents? = null, init: ScoreboardLine.() -> Unit = {}) {
		val line = ScoreboardLine(this, startingScore + if (decreasing) -lines.size else lines.size)
		line.text = text
		line.init()
		lines.add(line)
	}

	/**
	 * Appends a new line with optional formatting to the existing text.
	 *
	 * @param text The text to be appended.
	 * @param color The color of the appended text. If not specified, the default color is white.
	 * @param componentBlock A lambda expression to configure the text using the [PlainTextComponent] object.
	 */
	context(Function)
	fun appendLine(text: String, color: Color? = null, componentBlock: PlainTextComponent.() -> Unit = {}) =
		appendLine(textComponent(text, color ?: Color.WHITE, componentBlock))

	/**
	 * Creates the scoreboard objective and lines, displaying the objective if specified.
	 *
	 * @param display Determines whether the scoreboard should be displayed immediately. By default, it is set to true.
	 * @param slot The display slot where the scoreboard should be shown. By default, it is set to DisplaySlot#sidebar.
	 */
	context(Function)
	fun create(display: Boolean = true, slot: DisplaySlot = DisplaySlots.sidebar) {
		scoreboard.objectives.add(name, displayName = displayName)
		lines.forEach { it.create() }
		if (display) display(slot)
	}

	/**
	 * Sets the display slot for the scoreboard objective.
	 *
	 * @param slot The display slot to set. Defaults to [DisplaySlots.sidebar].
	 */
	context(Function)
	fun display(slot: DisplaySlot = DisplaySlots.sidebar) = scoreboard.objectives.setDisplay(slot, name)

	/* Appends an empty line to the existing string. */
	context(Function)
	fun emptyLine() = appendLine()

	/**
	 * Sets the `hideValue` property to `true` for each element in the `lines` collection.
	 * This will hide the values of the lines from the scoreboard.
	 *
	 * @param range The range of lines to hide. Defaults to all lines.
	 */
	context(Function)
	fun hideValues(range: IntRange = lines.indices) = lines.subList(range.first, range.last).forEach { it.hideValue = true }

	/**
	 * Sets a line in the scoreboard.
	 *
	 * @param index The index of the line to set. Lines are zero-indexed.
	 * @param text The text to set for the line. Defaults to null.
	 * @param init A lambda expression to configure the line using the [ScoreboardLine] object.
	 */
	context(Function)
	fun setLine(index: Int, text: ChatComponents? = null, init: ScoreboardLine.() -> Unit = {}) {
		for (i in lines.size..abs(index)) emptyLine()

		val line = ScoreboardLine(this, startingScore + if (decreasing) -index else index)
		line.text = text
		line.init()
		lines.add(line)
	}

	/**
	 * Sets the line at the specified index in the component.
	 *
	 * @param index The index of the line to set.
	 * @param text The text that will be displayed on the line.
	 * @param color The color of the text. If null, the default color of Color.WHITE will be used.
	 * @param componentBlock A lambda expression to configure the text using the [PlainTextComponent] object.
	 */
	context(Function)
	fun setLine(index: Int, text: String, color: Color? = null, componentBlock: PlainTextComponent.() -> Unit = {}) =
		setLine(index, textComponent(text, color ?: Color.WHITE, componentBlock))

	/**
	 * Removes an objective from the scoreboard.
	 *
	 * @return true if the objective was successfully removed, false otherwise
	 */
	context(Function)
	fun remove() = scoreboard.objectives.remove(name)

	companion object {
		/* Resets all scoreboard lines. */
		context(Function)
		fun resetAll() {
			for (i in 0..15) {
				scoreboard.players.reset(entity("§${i.toString(16)}"))
			}
		}
	}
}

/**
 * Represents a line on a scoreboard.
 * A line is linked to a fake player, which is used to display the line.
 *
 * @property sc The scoreboard display this line belongs to.
 * @property index The index of this line on the scoreboard.
 * @property fakePlayer The fake player used to display the line.
 * @property condition The condition to be checked before creating the line.
 * @property hideValue A flag indicating whether to hide the value of the line.
 * @property text The text to be displayed on the line.
 */
data class ScoreboardLine(private val sc: ScoreboardDisplay, val index: Int) {
	val fakePlayer = "§${abs(index).toString(16)}"

	var condition: (ExecuteCondition.() -> Unit)? = null
	var hideValue = false
	var text: ChatComponents? = null
		set(value) {
			value?.requireSimpleComponents()
			field = value
		}

	context(Function)
	private fun setText() = scoreboard.players.set(entity(fakePlayer), sc.name, index).let { command ->
		if (hideValue) scoreboard.players.displayNumberFormatBlank(entity(fakePlayer), sc.name)

		text?.let {
			scoreboard.players.displayName(entity(fakePlayer), sc.name, it)
		} ?: command
	}

	/**
	 * Sets the execution condition for a specific action.
	 *
	 * @param condition The condition to be evaluated.
	 * It is defined as a lambda expression with receiver type [ExecuteCondition].
	 * It uses an execute command to check the condition.
	 */
	fun condition(condition: ExecuteCondition.() -> Unit) {
		this.condition = condition
	}

	/* Creates the scoreboard line. */
	context(Function)
	fun create() {
		if (condition != null) return createIf(condition!!)
		setText()
	}

	/**
	 * Executes the specified condition and sets the text if the condition is met.
	 *
	 * @param condition The condition to check.
	 * It is a lambda function that takes an instance of [ExecuteCondition] as a parameter.
	 * It uses an execute command to check the condition.
	 */
	context(Function)
	fun createIf(condition: ExecuteCondition.() -> Unit) {
		execute {
			ifCondition(condition)

			run { setText() }
		}
	}

	/**
	 * Executes the specified actions unless the given condition is fulfilled.
	 *
	 * @param condition The condition to check.
	 * It is a lambda function that takes an instance of [ExecuteCondition] as a parameter.
	 * It uses an execute command to check the condition.
	 */
	context(Function)
	fun createUnless(condition: ExecuteCondition.() -> Unit) {
		execute {
			unlessCondition(condition)

			run {
				setText()
			}
		}
	}

	/**
	 * Removes a player from the scoreboard.
	 * This method resets the player's score and clears their display name.
	 */
	context(Function)
	fun remove() {
		scoreboard.players.reset(entity(fakePlayer))
	}
}

/**
 * Creates and displays a scoreboard with the specified properties.
 * See complete documentation [here](https://github.com/Ayfri/Kore/wiki/Scoreboards#scoreboard-displays).
 *
 *
 * @param name The name of the scoreboard.
 * @param display A flag indicating whether to display the scoreboard. Default is `true`.
 * @param displaySlot The display slot for the scoreboard. Default is `DisplaySlots.sidebar`.
 * @param init A lambda function to configure the scoreboard display.
 * @return The created `ScoreboardDisplay` object.
 */
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
