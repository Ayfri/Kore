package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCircleCheck
import com.varabyte.kobweb.silk.components.icons.lucide.LucideKeyboard
import com.varabyte.kobweb.silk.components.icons.lucide.LucideX
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/** Every shortcut of the page and of the editor. Closed by its button, a click outside or Escape. */
@Composable
fun ShortcutsDialog(onClose: () -> Unit) {
	Div({
		classes(PlaygroundStyle.dialogBackdrop)
		onClick { onClose() }
	})

	Div({
		classes(PlaygroundStyle.dialog)
		attr("role", "dialog")
		attr("aria-modal", "true")
		attr("aria-label", "Keyboard shortcuts")
	}) {
		Div({ classes(PlaygroundStyle.dialogHeader) }) {
			H2 {
				LucideKeyboard()
				Text("Keyboard shortcuts")
			}

			ToolButton("Close", onClose, keys = listOf("Esc")) { LucideX() }
		}

		Div({ classes(PlaygroundStyle.dialogColumns) }) {
			Div {
				H3 { Text("Playground") }
				PlaygroundCommand.entries.mapNotNull { command -> command.keys?.let { command.label to it } }.forEach { (label, keys) -> ShortcutRow(label, keys) }
				ShortcutRow("Leave fullscreen", listOf("Esc"))
			}

			Div {
				H3 { Text("Editor") }
				EDITOR_SHORTCUTS.forEach { (label, keys) -> ShortcutRow(label, keys) }
			}
		}

		Div({ classes(PlaygroundStyle.dialogFooter) }) {
			Text("Every playground action is also in the command palette, as ")
			Span({ classes(PlaygroundStyle.inlineCode) }) { Text("Kore: ...") }
			Text(", press F1 in the editor.")
		}
	}
}

@Composable
private fun ShortcutRow(label: String, keys: List<String>) = Div({ classes(PlaygroundStyle.shortcutRow) }) {
	Span { Text(label) }
	Keys(keys)
}

/** A short confirmation floating over the status bar, cleared by the page after a moment. */
@Composable
fun Toast(message: String) = Div({
	classes(PlaygroundStyle.toast)
	attr("role", "status")
}) {
	LucideCircleCheck()
	Text(message)
}
