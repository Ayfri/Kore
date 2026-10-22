package io.github.ayfri.kore.website.components.mc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.utils.inset
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.events.Event

/*
 * The choices of `Conversations.md` as the game draws them: clickable options in the chat and the buttons of a dialog.
 * The static mockups mirror the `help` examples of the page, the pieces are shared with the `ConversationPlayer` widget.
 */

private const val NARROW_VIEWPORT_PX = 640
private const val TEXT_WIDTH_NARROW = 150
private const val TEXT_WIDTH_WIDE = 260

/** An option of a choice: its [label] in [color], with a tooltip [hover]. */
internal data class McChoiceOption(val label: String, val color: String, val hover: String? = null)

/** The quest giver of the page's flagship example, whose lines are played by the `ConversationPlayer` widget. */
internal object McQuest {
	const val ACCEPTED = "Good luck, and come back alive."
	const val DECLINED = "I understand. The offer stays open."
	const val REMINDER = "Elder Mira: How is the pickaxe treating you?"
	const val SPEAKER = "Elder Mira"
	const val TITLE = "Will you take this pickaxe and look?"

	val ACCEPT = McChoiceOption("Accept", McColor.GREEN, "Take the pickaxe")
	val DECLINE = McChoiceOption("Decline", McColor.RED)
	val INTRO = listOf("Traveler, the mines have gone quiet.", "Something old woke up down there.")
	val OPTIONS = listOf(ACCEPT, DECLINE)
}

/** The width in GUI pixels the chat lines and dialog titles wrap at, narrower on small viewports so the scene holds them. */
@Composable
internal fun rememberTextWidth(): Int {
	var narrow by remember { mutableStateOf(window.innerWidth < NARROW_VIEWPORT_PX) }
	DisposableEffect(Unit) {
		val listener: (Event) -> Unit = { narrow = window.innerWidth < NARROW_VIEWPORT_PX }
		window.addEventListener("resize", listener)
		onDispose { window.removeEventListener("resize", listener) }
	}
	return if (narrow) TEXT_WIDTH_NARROW else TEXT_WIDTH_WIDE
}

/** The rows of a `<speaker> text` message wrapped to [width] GUI pixels, only the name being colored. */
internal fun mcSpeakerRows(speaker: String, color: String, text: String, width: Int): List<List<McSpan>> =
	McFont.wrap("<$speaker> $text", width).mapIndexed { index, row ->
		if (index == 0) listOf(McSpan("<"), McSpan(speaker, color), McSpan(row.removePrefix("<$speaker")))
		else listOf(McSpan(row))
	}

/** The rows of a [text] in one [color] wrapped to [width] GUI pixels. */
internal fun mcPlainRows(text: String, color: String, width: Int): List<List<McSpan>> =
	McFont.wrap(text, width).map { listOf(McSpan(it, color)) }

/** The world behind a conversation: a [panorama] face of [height] GUI pixels, or the blurred backdrop of a [dialog] sized by its content. */
@Composable
internal fun McConversationScene(panorama: Int, height: Int? = null, dialog: Boolean = false, content: @Composable () -> Unit) {
	Style(McConversationStyle)

	Div({
		classes(McUiStyle.world, DocMockupsStyle.scene)
		if (dialog) classes(McUiStyle.menuBackground, DocMockupsStyle.dialog)
		style {
			backgroundImage(mcPanorama(panorama))
			height?.let { height(gui(it)) }
		}
	}) { content() }
}

/**
 * The line of clickable [options], `[Accept]  [Decline]` for the default `ChatChoiceFormat`, drawn as one text like the game does.
 * Each option has an invisible target over its text for the hover tooltip and, with [onPick], the click.
 */
@Composable
internal fun McChoiceLine(options: List<McChoiceOption>, onPick: ((McChoiceOption) -> Unit)?) {
	val separator = "  "
	val spans = options.flatMapIndexed { index, option ->
		listOfNotNull(McSpan(separator).takeIf { index > 0 }, McSpan("[${option.label}]", option.color))
	}

	Div({ classes(McUiStyle.chatLine, McConversationStyle.choiceLine) }) {
		McText(spans)
		var start = 0
		options.forEachIndexed { index, option ->
			if (index > 0) start += McFont.width(separator)
			val width = McFont.width("[${option.label}]")
			McChoiceTarget(option, start, width, onPick)
			start += width
		}
	}
}

@Composable
private fun McChoiceTarget(option: McChoiceOption, start: Int, width: Int, onPick: ((McChoiceOption) -> Unit)?) {
	var hovered by remember { mutableStateOf(false) }

	Div({
		classes(McConversationStyle.optionTarget)
		attr("aria-label", option.label)
		if (onPick != null) {
			classes(McConversationStyle.clickable)
			attr("role", "button")
			attr("tabindex", "0")
			onClick { onPick(option) }
			onKeyDown { event ->
				if (event.key == "Enter" || event.key == " ") {
					event.preventDefault()
					onPick(option)
				}
			}
		}
		style {
			left(gui(2 + start))
			width(gui(width))
		}
		onBlur { hovered = false }
		onFocus { hovered = true }
		onMouseEnter { hovered = true }
		onMouseLeave { hovered = false }
	}) {
		option.hover?.takeIf { hovered }?.let { hover ->
			Div({ classes(McConversationStyle.chatTip) }) { McTooltip { McLine(hover) } }
		}
	}
}

/** A multi action dialog with one full width button per option, as `dialog { columns = 1 }` lays them out. */
@Composable
internal fun McChoiceDialog(title: String, options: List<McChoiceOption>, onPick: ((McChoiceOption) -> Unit)?) {
	McDialog(title, titleWidth = rememberTextWidth()) {
		Div({
			classes(DocMockupsStyle.buttonGrid)
			style { gridTemplateColumns("minmax(0, ${gui(150)})") }
		}) {
			options.forEach { McChoiceButton(it, onPick) }
		}
	}
}

/** A dialog opened over the whole [McConversationScene], blurring what is behind like the game does. */
@Composable
internal fun McChoiceDialogOverlay(title: String, options: List<McChoiceOption>, onPick: ((McChoiceOption) -> Unit)?) {
	Div({ classes(McUiStyle.menuBackground, McConversationStyle.dialogOverlay) }) { McChoiceDialog(title, options, onPick) }
}

@Composable
private fun McChoiceButton(option: McChoiceOption, onPick: ((McChoiceOption) -> Unit)?) {
	var hovered by remember { mutableStateOf(false) }

	Div({
		classes(McConversationStyle.dialogButton)
		onBlur { hovered = false }
		onFocus { hovered = true }
		onMouseEnter { hovered = true }
		onMouseLeave { hovered = false }
	}) {
		McButton(option.label, color = option.color) { onPick?.invoke(option) }
		option.hover?.takeIf { hovered }?.let { hover ->
			Div({ classes(McConversationStyle.dialogTip) }) { McTooltip { McLine(hover) } }
		}
	}
}

@Composable
private fun McConversationFigure(caption: String, content: @Composable () -> Unit) {
	Div({ classes(DocMockupsStyle.figure) }) {
		content()
		P({ classes(DocMockupsStyle.caption) }) { Text(caption) }
	}
}

/** The chat choice of the `help` example, `Accept` carrying its tooltip. */
@Composable
fun ConversationChatMockup() {
	val width = rememberTextWidth()

	McConversationFigure("In game: the chat choice of the help conversation, hover Accept to see its tooltip") {
		McConversationScene(panorama = 4, height = 46) {
			McChat(McUiStyle.hudChat) {
				mcSpeakerRows("Mira", McColor.GOLD, "Will you help?", width).forEach { McChatLine(*it.toTypedArray()) }
				McChoiceLine(McQuest.OPTIONS, null)
			}
		}
	}
}

/** The dialog choice of the `help` example, one button per row as `columns = 1` and `canCloseWithEscape = false` leave it. */
@Composable
fun ConversationDialogMockup() {
	McConversationFigure("In game: the dialog choice of the help conversation, the buttons wear their option's color") {
		McConversationScene(panorama = 2, dialog = true) {
			McChoiceDialog("Will you help?", listOf(McChoiceOption("Accept", McColor.GREEN), McQuest.DECLINE), null)
		}
	}
}

private object McConversationStyle : StyleSheet() {
	/** Tooltips are drawn by the game at the mouse, here above the line so they stay inside the scene. */
	val chatTip by style {
		bottom(100.percent)
		left(0.px)
		marginBottom(gui(2))
		pointerEvents(PointerEvents.None)
		position(Position.Absolute)
		zIndex(2)
	}

	val choiceLine by style {
		position(Position.Relative)
	}

	val clickable by style {
		cursor(Cursor.Pointer)
	}

	val dialogButton by style {
		position(Position.Relative)
	}

	val dialogOverlay by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		isolation(Isolation.Isolate)
		position(Position.Absolute)
		inset(0.px)
	}

	val dialogTip by style {
		pointerEvents(PointerEvents.None)
		position(Position.Absolute)
		right(gui(4))
		top(gui(-16))
		zIndex(2)
	}

	val optionTarget by style {
		bottom(0.px)
		position(Position.Absolute)
		top(0.px)
	}
}
