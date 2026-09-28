package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.silk.components.icons.lucide.*
import io.github.ayfri.kore.website.externals.monaco.CodeEditor
import io.github.ayfri.kore.website.externals.monaco.Monaco
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/** The `main.kt` tab: its editor actions over Monaco, which keeps the same instance for the whole visit. */
@Composable
fun EditorPane(
	initialCode: String?,
	editor: CodeEditor?,
	dirty: Boolean,
	errorCount: Int,
	onChange: (String) -> Unit,
	onCursor: (CursorInfo) -> Unit,
	onReady: (Monaco, CodeEditor) -> Unit,
	onReset: () -> Unit,
) {
	val maximized = PlaygroundLayout.maximizedPane == MaximizedPane.EDITOR

	Div({
		classes(PlaygroundStyle.pane)
		if (maximized) classes(PlaygroundStyle.paneMaximized)
	}) {
		Div({ classes(PlaygroundStyle.tabStrip) }) {
			Div({ classes(PlaygroundStyle.tab, PlaygroundStyle.tabActive) }) {
				KotlinIcon()
				Text(USER_FILE_NAME)

				when {
					errorCount > 0 -> Span({ classes(PlaygroundStyle.tabErrors) }) { Text(errorCount.toString()) }
					dirty -> Span({ classes(PlaygroundStyle.dirtyDot) }) { Span({ classes(PlaygroundStyle.srOnly) }) { Text("edited") } }
				}
			}

			Div({ classes(PlaygroundStyle.tabActions) }) {
				EditorAction(editor, "Undo", "undo", listOf(MOD_KEY, "Z")) { LucideUndo2() }
				EditorAction(editor, "Redo", "redo", listOf(MOD_KEY, "Y")) { LucideRedo2() }
				ToolSeparator()
				EditorAction(editor, "Find", "actions.find", listOf(MOD_KEY, "F")) { LucideSearch() }
				EditorAction(editor, "Replace", "editor.action.startFindReplaceAction", listOf(MOD_KEY, "H"), wide = true) { LucideTextSearch() }
				EditorAction(editor, "Go to line", "editor.action.gotoLine", listOf(MOD_KEY, "G"), wide = true) { LucideHash() }
				ToolSeparator()
				EditorAction(editor, "Fold all", "editor.foldAll", wide = true) { LucideChevronsDownUp() }
				EditorAction(editor, "Unfold all", "editor.unfoldAll", wide = true) { LucideChevronsUpDown() }

				ToolButton("Word wrap", { PlaygroundSettings.wordWrap = !PlaygroundSettings.wordWrap }, active = PlaygroundSettings.wordWrap) {
					LucideTextWrap()
				}

				ToolSeparator()
				EditorAction(editor, "Command palette", "editor.action.quickCommand", listOf("F1")) { LucideCommand() }
				ToolButton("Reset to the example", onReset, enabled = dirty) { LucideRotateCcw() }

				ToolButton(
					if (maximized) "Exit fullscreen" else "Open fullscreen",
					{ PlaygroundLayout.maximizedPane = PlaygroundLayout.maximizedPane.toggle(MaximizedPane.EDITOR) },
					active = maximized,
				) {
					if (maximized) LucideMinimize2() else LucideMaximize2()
				}
			}
		}

		initialCode?.let { value ->
			MonacoEditor(
				initialValue = value,
				className = PlaygroundStyle.editor,
				onChange = onChange,
				onCursor = onCursor,
				onReady = onReady,
			)
		}
	}
}

/** A button running one of Monaco's own actions, inert until the editor exists. [wide] drops it on narrow screens. */
@Composable
private fun EditorAction(
	editor: CodeEditor?,
	label: String,
	action: String,
	keys: List<String>? = null,
	wide: Boolean = false,
	icon: @Composable () -> Unit,
) = ToolButton(
	label,
	{ editor?.runAction(action) },
	enabled = editor != null,
	keys = keys,
	classes = if (wide) arrayOf(PlaygroundStyle.wideOnly) else emptyArray(),
	content = icon,
)
