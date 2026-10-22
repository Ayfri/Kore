package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.mc.McChat
import io.github.ayfri.kore.website.components.mc.McChatLine
import io.github.ayfri.kore.website.components.mc.McChoiceDialogOverlay
import io.github.ayfri.kore.website.components.mc.McChoiceLine
import io.github.ayfri.kore.website.components.mc.McChoiceOption
import io.github.ayfri.kore.website.components.mc.McColor
import io.github.ayfri.kore.website.components.mc.McConversationScene
import io.github.ayfri.kore.website.components.mc.McQuest
import io.github.ayfri.kore.website.components.mc.McSpan
import io.github.ayfri.kore.website.components.mc.McUiStyle
import io.github.ayfri.kore.website.components.mc.mcPlainRows
import io.github.ayfri.kore.website.components.mc.mcSpeakerRows
import io.github.ayfri.kore.website.components.mc.rememberTextWidth
import io.github.ayfri.kore.website.utils.monoFont
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.*

private const val CONVERSATION_PANORAMA = 4
private const val CONVERSATION_SCENE_HEIGHT = 104
private const val CONVERSATION_VISIBLE_ROWS = 9
private const val CONVERSATION_WAIT_MS = 2000

private enum class ConversationChoiceStyle(val label: String) {
	CHAT("Chat"),
	DIALOG("Dialog"),
}

private enum class ConversationPhase(val label: String) {
	IDLE("idle"),
	PLAYING("playing lines"),
	CHOOSING("waiting for a choice"),
}

/** How long a `wait(2.seconds)` lasts: [factor] times faster, or not at all for 0. */
private enum class ConversationSpeed(val label: String, val factor: Int) {
	REAL("Real time", 1),
	FAST("Fast", 5),
	INSTANT("Instant", 0),
}

private sealed interface ConversationEntry {
	/** A line said by the quest giver, as `<Elder Mira> text`. */
	data class Said(val text: String) : ConversationEntry

	/** A `tellraw` without speaker. */
	data class Plain(val text: String, val color: String) : ConversationEntry

	/** The clickable options of the pending choice. */
	data object Choices : ConversationEntry
}

/** The per-player state of the quest giver of the page: the step of the conversation, and the quest score. */
private class QuestPlayer(private val scope: CoroutineScope, reducedMotion: Boolean) {
	val chat = mutableStateListOf<ConversationEntry>()
	var choiceStyle by mutableStateOf(ConversationChoiceStyle.CHAT)
	var phase by mutableStateOf(ConversationPhase.IDLE)
	var quest by mutableStateOf(0)
	var speed by mutableStateOf(if (reducedMotion) ConversationSpeed.INSTANT else ConversationSpeed.REAL)
	private var job: Job? = null

	val dialogOpen get() = phase == ConversationPhase.CHOOSING && choiceStyle == ConversationChoiceStyle.DIALOG

	/** `onRightClick`: the reminder once the quest is taken, else `intro.advance()`, which does nothing during the timed lines. */
	fun click() {
		when {
			quest == 1 -> chat += ConversationEntry.Plain(McQuest.REMINDER, McColor.GOLD)
			phase == ConversationPhase.IDLE -> play()
			phase == ConversationPhase.CHOOSING -> showChoice()
		}
	}

	fun pick(option: McChoiceOption) {
		if (phase != ConversationPhase.CHOOSING) return
		if (option == McQuest.ACCEPT) quest = 1
		chat += ConversationEntry.Said(if (option == McQuest.ACCEPT) McQuest.ACCEPTED else McQuest.DECLINED)
		phase = ConversationPhase.IDLE
	}

	fun reset() {
		job?.cancel()
		chat.clear()
		phase = ConversationPhase.IDLE
		quest = 0
	}

	fun switchStyle(style: ConversationChoiceStyle) {
		reset()
		choiceStyle = style
	}

	private fun play() {
		phase = ConversationPhase.PLAYING
		job = scope.launch {
			McQuest.INTRO.forEach {
				chat += ConversationEntry.Said(it)
				pause()
			}
			showChoice()
		}
	}

	private suspend fun pause() = if (speed.factor == 0) yield() else delay((CONVERSATION_WAIT_MS / speed.factor).toLong())

	/** A chat choice writes its title and options again each time, a dialog is already on screen. */
	private fun showChoice() {
		phase = ConversationPhase.CHOOSING
		if (choiceStyle == ConversationChoiceStyle.CHAT) chat += listOf(ConversationEntry.Said(McQuest.TITLE), ConversationEntry.Choices)
	}
}

/** The rows the entry takes in the chat once wrapped, null being the line of options. */
private fun ConversationEntry.rows(width: Int): List<List<McSpan>?> = when (this) {
	ConversationEntry.Choices -> listOf(null)
	is ConversationEntry.Plain -> mcPlainRows(text, color, width)
	is ConversationEntry.Said -> mcSpeakerRows(McQuest.SPEAKER, McColor.GOLD, text, width)
}

/**
 * Plays the quest giver of the Conversations page in a chat mockup: timed lines, then the choice as a chat line or a dialog, then the
 * reminder after accepting. It replays the documented Kotlin example, no Kore code runs here.
 */
@Composable
fun ConversationPlayer() {
	Style(ConversationPlayerStyle)

	val scope = rememberCoroutineScope()
	val player = remember { QuestPlayer(scope, window.matchMedia("(prefers-reduced-motion: reduce)").matches) }
	val width = rememberTextWidth()
	val rows = player.chat.flatMap { it.rows(width) }.takeLast(CONVERSATION_VISIBLE_ROWS)
	val choosing = player.phase == ConversationPhase.CHOOSING

	Div({ classes(DocControlsStyle.panel, ConversationPlayerStyle.player) }) {
		Div({ classes(ConversationPlayerStyle.controls) }) {
			ConversationField("Choice style") {
				PillGroup("Choice style", ConversationChoiceStyle.entries, player.choiceStyle, { it.label }) { player.switchStyle(it) }
			}
			ConversationField("Speed of the pauses") {
				PillGroup("Speed of the pauses", ConversationSpeed.entries, player.speed, { it.label }) { player.speed = it }
			}
		}

		McConversationScene(CONVERSATION_PANORAMA, CONVERSATION_SCENE_HEIGHT) {
			McChat(McUiStyle.hudChat) {
				rows.forEach { row ->
					if (row == null) McChoiceLine(McQuest.OPTIONS, player::pick.takeIf { choosing })
					else McChatLine(*row.toTypedArray())
				}
			}
			if (player.dialogOpen) McChoiceDialogOverlay(McQuest.TITLE, McQuest.OPTIONS, player::pick)
		}

		Div({ classes(ConversationPlayerStyle.actions) }) {
			Pill("Right-click Elder Mira") { player.click() }
			Pill("Reset") { player.reset() }
		}

		Div({ classes(ConversationPlayerStyle.readout) }) {
			Span { Text("conversation: ${player.phase.label}") }
			Span { Text("quest score: ${player.quest}") }
			Span { Text("inventory: ${if (player.quest == 1) "iron pickaxe" else "empty"}") }
		}

		Div({ classes(ConversationPlayerStyle.caption) }) {
			Text("A replay of the quest above, one player's state: a click during the lines does nothing, a click during the choice shows it again.")
		}
	}
}

@Composable
private fun ConversationField(label: String, content: @Composable () -> Unit) {
	Div({ classes(DocControlsStyle.control) }) {
		Span({ classes(DocControlsStyle.controlLabel) }) { Text(label) }
		content()
	}
}

private object ConversationPlayerStyle : StyleSheet() {
	val actions by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
	}

	val caption by style {
		color(LandingVars.Muted.value())
		fontSize(0.8.cssRem)
	}

	val controls by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.9.cssRem, 1.5.cssRem)
	}

	val player by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.9.cssRem)
		margin(1.5.cssRem, 0.px)
	}

	val readout by style {
		color(LandingVars.Muted.value())
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		fontSize(0.75.cssRem)
		gap(0.3.cssRem, 1.2.cssRem)
		monoFont()
	}
}
