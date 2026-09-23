package io.github.ayfri.kore.helpers.sidebar

import io.github.ayfri.kore.arguments.DisplaySlot
import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.chatcomponents.*
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.entity
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.commands.execute.ExecuteCondition
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function

/**
 * A scoreboard display made of up to 15 lines, each with a left [SidebarLine.text] and an optional right [SidebarLine.value].
 *
 * Each line is the fake player `$<index>` of [objective], scored `-index` so lines sort top to bottom, and two sidebars
 * never share a fake player. Score, selector and NBT components are resolved when a line is written, [refresh] rewrites them.
 *
 * ```kotlin
 * val lobby = sidebar("lobby") {
 * 	title("Mini-game", Color.GOLD)
 * 	line("Map: Castle")
 * 	line("Kills", value = scoreComponent("kills", literal("Ayfri")))
 * }
 *
 * load { lobby.create() }
 * tick { lobby.refresh() }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/helpers/sidebars
 */
class Sidebar(val objective: String) {
	val lines = mutableListOf<SidebarLine>()
	var title: ChatComponents? = null

	/** Adds an empty line. */
	fun emptyLine() = line("")

	/** Adds a line showing [text] on the left and [value] on the right, no right value when `null`. */
	fun line(text: ChatComponents, value: ChatComponents? = null, block: SidebarLine.() -> Unit = {}): SidebarLine {
		require(lines.size < MAX_LINES) { "Sidebar '$objective' can't have more than $MAX_LINES lines." }
		return SidebarLine(lines.size, text, value).apply(block).also { lines += it }
	}

	/** Adds a line showing [text] on the left and [value] on the right, no right value when `null`. */
	fun line(text: String, color: Color? = null, value: ChatComponents? = null, block: SidebarLine.() -> Unit = {}) =
		line(textComponent(text, color), value, block)

	/** Sets the [title] shown above the lines. */
	fun title(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) {
		title = textComponent(text, color, block)
	}

	/** Recreates the objective with every line from scratch, then shows it in [slot], or keeps it hidden when [slot] is `null`. */
	context(fn: Function)
	fun create(slot: DisplaySlot? = DisplaySlots.sidebar) {
		remove()
		fn.scoreboard.objectives.add(objective, displayName = title)
		fn.scoreboard.objectives.modifyNumberFormatBlank(objective)
		lines.forEach { it.write() }
		slot?.let { show(it) }
	}

	/** Empties [slot], whichever objective it was showing. */
	context(fn: Function)
	fun hide(slot: DisplaySlot = DisplaySlots.sidebar) =
		fn.addLine(command("scoreboard", literal("objectives"), literal("setdisplay"), slot))

	/** Rewrites the lines holding score, selector or NBT components and the lines with a [SidebarLine.condition]. */
	context(fn: Function)
	fun refresh() = lines.filter { it.isDynamic || it.condition != null }.forEach { it.write() }

	/** Deletes the objective, and with it every line. */
	context(fn: Function)
	fun remove() = fn.scoreboard.objectives.remove(objective)

	/** Replaces the line at [index] at runtime without touching its declaration. */
	context(fn: Function)
	fun setLine(index: Int, text: ChatComponents, value: ChatComponents? = null) {
		require(index in lines.indices) { "Sidebar '$objective' has no line $index." }
		SidebarLine(index, text, value).apply { condition = lines[index].condition }.write()
	}

	/** Shows this sidebar in [slot]. */
	context(fn: Function)
	fun show(slot: DisplaySlot = DisplaySlots.sidebar) = fn.scoreboard.objectives.setDisplay(slot, objective)

	/**
	 * A single sidebar line, displayed by the fake player [holder].
	 *
	 * @property value Right-aligned text replacing the score number, the line has no right text when `null`.
	 */
	inner class SidebarLine internal constructor(val index: Int, var text: ChatComponents, var value: ChatComponents? = null) {
		val holder: ScoreHolderArgument = entity("$$index")

		/** Only shows the line while this condition passes, checked on [create] and [refresh]. */
		var condition: (ExecuteCondition.() -> Unit)? = null

		/** True when the line holds components the game resolves when the line is written. */
		val isDynamic get() = text.isDynamic || value?.isDynamic == true

		/** Sets [value] to the plain [text]. */
		fun value(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) {
			value = textComponent(text, color, block)
		}

		/** Only shows the line while [condition] passes. */
		fun visibleIf(condition: ExecuteCondition.() -> Unit) {
			this.condition = condition
		}

		context(fn: Function)
		internal fun write() {
			val condition = condition ?: return writeDisplay()
			fn.execute {
				ifCondition(condition)
				run { writeDisplay() }
			}
			fn.execute {
				unlessCondition(condition)
				run { scoreboard.players.reset(holder, objective) }
			}
		}

		context(fn: Function)
		private fun writeDisplay() {
			fn.scoreboard.players.set(holder, objective, -index)
			fn.scoreboard.players.displayName(holder, objective, text)
			value?.let { fn.scoreboard.players.displayNumberFormatFixed(holder, objective, it) }
		}
	}

	companion object {
		const val MAX_LINES = 15

		private val ChatComponents.isDynamic: Boolean get() = any { it.isDynamic }

		private val ChatComponent.isDynamic: Boolean
			get() = this is ScoreComponent || this is EntityComponent || this is NbtComponent || extra?.isDynamic == true ||
				this is TranslatedTextComponent && with.orEmpty().any { it.isDynamic }
	}
}

/** Declares a [Sidebar] backed by the [objective] scoreboard objective, nothing is emitted until [Sidebar.create] is called. */
fun sidebar(objective: String, init: Sidebar.() -> Unit) = Sidebar(objective).apply(init)
