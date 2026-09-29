package io.github.ayfri.kore.website.components.playground

import io.github.ayfri.kore.website.externals.monaco.CodeEditor
import io.github.ayfri.kore.website.externals.monaco.Monaco
import io.github.ayfri.kore.website.utils.jsObject
import kotlinx.browser.window
import org.w3c.dom.events.KeyboardEvent

private val isMac = "Mac" in window.navigator.userAgent

/** The modifier every playground shortcut uses, as the visitor's keyboard labels it. */
val MOD_KEY = if (isMac) "⌘" else "Ctrl"

/**
 * A page action reachable from its button, a keyboard shortcut and Monaco's command palette (F1), where it is listed as
 * `Kore: <label>` with its keys.
 *
 * [code] is a `KeyboardEvent.code`, which is also the name of the matching `monaco.KeyCode` entry, so one string drives
 * both the page-level shortcut and the editor keybinding.
 */
enum class PlaygroundCommand(val label: String, val code: String? = null, val shift: Boolean = false) {
	DOWNLOAD_SOURCE("Download main.kt"),
	DOWNLOAD_ZIP("Download the datapack", "KeyS", shift = true),
	FOCUS_MODE("Toggle focus mode"),
	OPEN_FILE("Open a Kotlin file", "KeyO"),
	RESET("Reset to the example"),
	RUN("Run", "Enter"),
	SAVE("Save the draft", "KeyS"),
	SHARE("Copy a share link"),
	SHORTCUTS("Show keyboard shortcuts"),
	TOGGLE_AUTO_BUILD("Toggle live rebuild"),
	TOGGLE_PANEL("Toggle the bottom panel", "KeyJ"),
	TOGGLE_SIDEBAR("Toggle the sidebar", "KeyB");

	/** The keys as printed on a keycap, `null` for a command only reachable through its button and the palette. */
	val keys get() = code?.let { listOfNotNull(MOD_KEY, "Shift".takeIf { shift }, it.removePrefix("Key")) }

	fun matches(event: KeyboardEvent) =
		code == event.code && (event.ctrlKey || event.metaKey) && event.shiftKey == shift && !event.altKey

	companion object {
		fun of(event: KeyboardEvent) = entries.firstOrNull { it.matches(event) }
	}
}

/** Editor shortcuts Monaco provides on its own, listed next to the playground's in the shortcuts dialog. */
val EDITOR_SHORTCUTS = listOf(
	"Command palette" to listOf("F1"),
	"Find" to listOf(MOD_KEY, "F"),
	"Replace" to listOf(MOD_KEY, "H"),
	"Go to line" to listOf(MOD_KEY, "G"),
	"Toggle line comment" to listOf(MOD_KEY, "/"),
	"Select next occurrence" to listOf(MOD_KEY, "D"),
	"Move line up or down" to listOf("Alt", "↑↓"),
	"Copy line down" to listOf("Shift", "Alt", "↓"),
	"Delete line" to listOf(MOD_KEY, "Shift", "K"),
	"Fold or unfold region" to listOf(MOD_KEY, "Shift", "[ ]"),
	"Suggest" to listOf(MOD_KEY, "Space"),
	"Parameter hints" to listOf(MOD_KEY, "Shift", "Space"),
	"Quick fix, import a name" to listOf("Alt", "Enter"),
	"Optimize imports" to listOf("Shift", "Alt", "O"),
)

/**
 * Lists every [PlaygroundCommand] in the editor's command palette, bound to its keys while the editor has focus, and
 * Run in the context menu. Monaco stops a key it handles from reaching the page, so a shortcut never fires twice.
 */
fun CodeEditor.registerCommands(monaco: Monaco, execute: (PlaygroundCommand) -> Unit) = PlaygroundCommand.entries.forEach { command ->
	addAction(jsObject {
		id = "kore.${command.name.lowercase()}"
		label = "Kore: ${command.label}"
		run = { execute(command) }

		command.code?.let { code ->
			val modifiers = monaco.KeyMod.CtrlCmd or (if (command.shift) monaco.KeyMod.Shift else 0)
			keybindings = arrayOf(modifiers or monaco.KeyCode.asDynamic()[code] as Int)
		}

		if (command == PlaygroundCommand.RUN) {
			contextMenuGroupId = "navigation"
			contextMenuOrder = 0.0
		}
	})
}
