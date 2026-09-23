package io.github.ayfri.kore.helpers.menus

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.actions.DialogAction
import io.github.ayfri.kore.arguments.actions.OpenUrl
import io.github.ayfri.kore.arguments.actions.RunCommand
import io.github.ayfri.kore.arguments.actions.ShowDialog
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.chatcomponents.translatedTextComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.scores.score
import io.github.ayfri.kore.arguments.selector.scores
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.dialogShow
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.dialogs.action.DialogLabelledAction
import io.github.ayfri.kore.features.dialogs.body.DialogBody
import io.github.ayfri.kore.features.dialogs.body.PlainMessage
import io.github.ayfri.kore.features.dialogs.types.DialogList
import io.github.ayfri.kore.features.dialogs.types.MultiAction
import io.github.ayfri.kore.features.tags.addToTag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.arguments.tagged.DialogTagArgument
import io.github.ayfri.kore.generated.arguments.types.DialogArgument

/**
 * One screen of a [Menu], generated as a `multi_action` dialog at `data/<namespace>/dialog/<fileName>.json`.
 *
 * Its exit button goes back to the parent page, or closes the menu on the root page.
 */
open class MenuPage internal constructor(
	val dataPack: DataPack,
	val fileName: String,
	var title: ChatComponents,
	private val parent: MenuPage?,
) {
	private val body = mutableListOf<DialogBody>()
	private val buttons = mutableListOf<DialogLabelledAction>()

	/** Width of every button of this page, between 1 and 1024, the game defaults to 150. */
	var buttonWidth: Int? = null

	/** Number of button columns, the game defaults to 2. */
	var columns: Int? = null

	/** The dialog of this page, usable with `dialog show` or in click events. */
	val dialog = DialogArgument(fileName, dataPack.name)

	/** Label of the button leading to this page from a dialog list, such as the pause screen or the Smithed menu. */
	var externalTitle: ChatComponents? = null

	internal open val menu: Menu get() = parent!!.menu

	/**
	 * Adds a button running [onClick] as the clicking player, at their position, on the server.
	 * Buttons go through `/trigger`, so they work for players without operator rights.
	 *
	 * @param reopen Shows this page again once [onClick] ran, for toggles and counters.
	 */
	fun button(label: ChatComponents, tooltip: ChatComponents? = null, reopen: Boolean = false, onClick: Function.() -> Unit) {
		val value = menu.addHandler {
			onClick()
			if (reopen) dialogShow(self(), dialog)
		}
		buttons += labelledAction(label, tooltip, RunCommand("trigger ${menu.objective} set $value"))
	}

	/** Adds a button running [onClick] as the clicking player, see the [ChatComponents] overload. */
	fun button(label: String, color: Color? = null, tooltip: String? = null, reopen: Boolean = false, onClick: Function.() -> Unit) =
		button(textComponent(label, color), tooltip?.let(::textComponent), reopen, onClick)

	/** Adds a button opening [url] in the player's browser, after the game's confirmation screen. */
	fun link(label: ChatComponents, url: String, tooltip: ChatComponents? = null) {
		buttons += labelledAction(label, tooltip, OpenUrl(url))
	}

	/** Adds a button opening [url] in the player's browser, after the game's confirmation screen. */
	fun link(label: String, url: String, color: Color? = null) = link(textComponent(label, color), url)

	/** Adds a button opening the sub-page built by [block], whose exit button comes back here. */
	fun page(title: ChatComponents, fileName: String, block: MenuPage.() -> Unit): DialogArgument {
		val page = MenuPage(dataPack, fileName, title, this).apply(block)
		page.generate(ShowDialog(dialog), translatedTextComponent("gui.back"))
		buttons += labelledAction(title, null, ShowDialog(page.dialog))
		return page.dialog
	}

	/** Adds a button opening the sub-page built by [block], its file name is derived from [title]. */
	fun page(title: String, color: Color? = null, block: MenuPage.() -> Unit) =
		page(textComponent(title, color), "${fileName}_${title.lowercase().replace(NON_ID_CHARS, "_").trim('_')}", block)

	/** Adds a paragraph of text above the buttons, [width] ranges from 1 to 1024, the game defaults to 200. */
	fun text(contents: ChatComponents, width: Int? = null) {
		body += PlainMessage(contents, width)
	}

	/** Adds a paragraph of text above the buttons, [width] ranges from 1 to 1024, the game defaults to 200. */
	fun text(contents: String, color: Color? = null, width: Int? = null) = text(textComponent(contents, color), width)

	internal fun generate(exitAction: DialogAction?, exitLabel: ChatComponents) {
		require(buttons.isNotEmpty()) { "Menu page '$fileName' needs at least one button." }
		dataPack.dialogs += Dialog(
			fileName, MultiAction(
				title = title,
				externalTitle = externalTitle,
				body = body.ifEmpty { null },
				actions = buttons,
				exitAction = DialogLabelledAction(exitAction, exitLabel, width = 200),
				columns = columns,
			)
		)
	}

	private fun labelledAction(label: ChatComponents, tooltip: ChatComponents?, action: DialogAction) =
		DialogLabelledAction(action, label, tooltip, buttonWidth)

	private companion object {
		val NON_ID_CHARS = Regex("[^a-z0-9]+")
	}
}

/**
 * An interactive menu made of dialog pages, whose buttons run server-side functions through the [objective] trigger.
 *
 * Declaring a menu generates its dialogs, one function per button, a `load` function creating the trigger and a `tick`
 * function enabling it and dispatching clicks. Show it with [open], or list it on the pause screen with [pauseScreen].
 *
 * ```kotlin
 * val settings = menu("settings", "My Pack") {
 * 	text("Tweak the pack to your liking.")
 * 	button("Heal me") { effect(self()) { give(Effects.INSTANT_HEALTH) } }
 * 	page("Credits") { link("GitHub", "https://github.com/Ayfri/Kore") }
 * 	smithed = true
 * }
 *
 * function("open_settings") { settings.open() }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/helpers/menus
 */
class Menu internal constructor(dataPack: DataPack, val name: String, title: ChatComponents) :
	MenuPage(dataPack, name, title, null) {
	private val handlers = mutableListOf<FunctionArgument>()

	override val menu get() = this

	/** The `trigger` objective buttons set, `<namespace>.menu.<name>`. */
	val objective = "${dataPack.name}.menu.$name"

	/** Adds the menu to the pause screen, under the `#minecraft:pause_screen_additions` dialog tag. */
	var pauseScreen = false

	/**
	 * Lists the menu in the shared data pack menu of the [Smithed convention](https://docs.smithed.dev/conventions/data-pack-menu/),
	 * and turns the root exit button into a back button to it.
	 */
	var smithed = false

	/** Shows the root page to [targets]. */
	context(fn: Function)
	fun open(targets: EntityArgument = self()) = fn.dialogShow(targets, dialog)

	internal fun addHandler(block: Function.() -> Unit): Int {
		handlers += dataPack.generatedFunction("menu_${name}_${handlers.size + 1}", block = block)
		return handlers.size
	}

	internal fun generate() {
		if (smithed) generateSmithedMenu()
		else generate(null, translatedTextComponent("gui.done"))

		if (pauseScreen) addToPauseScreen(dialog)
		if (handlers.isEmpty()) return

		val dispatch = dataPack.generatedFunction("menu_${name}_dispatch") {
			handlers.forEachIndexed { index, handler ->
				execute {
					ifCondition { score(self(), objective, rangeOrInt(index + 1)) }
					run(handler)
				}
			}
			scoreboard.players.reset(self(), objective)
		}

		dataPack.load("menu_${name}_load") {
			scoreboard.objectives.add(objective, ScoreboardCriteria.TRIGGER)
		}

		dataPack.tick("menu_${name}_tick") {
			scoreboard.players.enable(allPlayers(), objective)
			execute {
				asTarget(allPlayers { scores { score(objective) greaterThanOrEqualTo 1 } })
				at(self())
				run(dispatch)
			}
		}
	}

	private fun addToPauseScreen(dialog: DialogArgument) =
		dataPack.addToTag<DialogTagArgument>("pause_screen_additions", "dialog", "minecraft") { add(dialog, required = false) }

	private fun generateSmithedMenu() {
		val smithedMenu = DialogArgument("data_packs", SMITHED_NAMESPACE)
		val dataPacks = translatedTextComponent("selectWorld.dataPacks")

		generate(ShowDialog(smithedMenu), translatedTextComponent("gui.back"))
		dataPack.addToTag<DialogTagArgument>("data_packs", "dialog", SMITHED_NAMESPACE) { add(dialog, required = false) }
		addToPauseScreen(smithedMenu)

		if (dataPack.dialogs.any { it.namespace == SMITHED_NAMESPACE && it.fileName == smithedMenu.name }) return
		dataPack.dialogs += Dialog(
			smithedMenu.name, DialogList(
				title = translatedTextComponent("menu.smithed.data_packs.title", listOf(dataPacks), "%s"),
				externalTitle = translatedTextComponent("menu.smithed.data_packs", listOf(dataPacks), "%s..."),
				dialogs = listOf(DialogTagArgument("data_packs", SMITHED_NAMESPACE)),
				exitAction = DialogLabelledAction(label = translatedTextComponent("gui.back"), width = 200),
			)
		).apply { namespace = SMITHED_NAMESPACE }
	}

	private companion object {
		const val SMITHED_NAMESPACE = "smithed"
	}
}

/** Declares a [Menu] whose root page is titled [title] and built by [init], see [Menu]. */
fun DataPack.menu(name: String, title: ChatComponents, init: Menu.() -> Unit) = Menu(this, name, title).apply(init).also { it.generate() }

/** Declares a [Menu] whose root page is titled [title] and built by [init], see [Menu]. */
fun DataPack.menu(name: String, title: String, color: Color? = null, init: Menu.() -> Unit) =
	menu(name, textComponent(title, color), init)
