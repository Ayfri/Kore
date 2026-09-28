package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.silk.components.icons.lucide.*
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.externals.Prism
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private const val HARNESS_ID = "playground-harness"

/**
 * The panel under the editors: compiler problems, the build log, and the hidden harness compiled next to `main.kt`.
 *
 * Problems from the harness are dropped, they point at a file the visitor cannot see. [typeChecked] tells an empty list
 * after a type-check apart from one nothing has checked yet.
 */
@Composable
fun BottomPanel(
	diagnostics: List<PlaygroundDiagnostic>,
	typeChecked: Boolean,
	backendConfigured: Boolean,
	onSelect: (PlaygroundDiagnostic) -> Unit,
) {
	val problems = diagnostics.filter { it.file == USER_FILE_NAME }
	val tab = PlaygroundLayout.panelTab

	LaunchedEffect(tab, BuildLog.entries.size) { if (tab == PanelTab.LOG) BuildLog.markSeen() }
	LaunchedEffect(tab) { if (tab == PanelTab.HARNESS) document.getElementById(HARNESS_ID)?.let { Prism.highlightAllUnder(it) } }

	Div({ classes(PlaygroundStyle.bottomPanel) }) {
		Div({ classes(PlaygroundStyle.tabStrip, PlaygroundStyle.panelTabs) }) {
			Div({ classes(PlaygroundStyle.tabGroup) }) {
				PanelTabButton(PanelTab.PROBLEMS, "Problems", problems.size) { LucideCircleAlert() }
				PanelTabButton(PanelTab.LOG, "Build log", BuildLog.unseenIssues) { LucideScrollText() }
				PanelTabButton(PanelTab.HARNESS, "Harness", 0) { LucideFileCode() }
			}

			Div({ classes(PlaygroundStyle.tabActions) }) {
				if (tab == PanelTab.LOG) ToolButton("Clear the log", { BuildLog.clear() }, enabled = BuildLog.entries.isNotEmpty()) { LucideTrash2() }
				ToolButton("Close the panel", { PlaygroundLayout.panelOpen = false }, keys = PlaygroundCommand.TOGGLE_PANEL.keys) { LucideX() }
			}
		}

		Div({ classes(PlaygroundStyle.panelBody) }) {
			when (tab) {
				PanelTab.PROBLEMS -> Problems(problems, typeChecked, backendConfigured, onSelect)
				PanelTab.LOG -> Log()
				PanelTab.HARNESS -> Harness()
			}
		}
	}
}

@Composable
private fun PanelTabButton(tab: PanelTab, label: String, count: Int, icon: @Composable () -> Unit) = Button({
	classes(PlaygroundStyle.tab, PlaygroundStyle.tabButton)
	if (PlaygroundLayout.panelTab == tab) classes(PlaygroundStyle.tabActive)
	attr("role", "tab")
	attr("aria-selected", (PlaygroundLayout.panelTab == tab).toString())
	onClick { PlaygroundLayout.panelTab = tab }
}) {
	icon()
	Text(label)
	if (count > 0) Span({ classes(PlaygroundStyle.tabCount) }) { Text(count.toString()) }
}

@Composable
private fun Problems(problems: List<PlaygroundDiagnostic>, typeChecked: Boolean, backendConfigured: Boolean, onSelect: (PlaygroundDiagnostic) -> Unit) {
	if (problems.isEmpty()) {
		Div({ classes(PlaygroundStyle.panelEmpty) }) {
			when {
				!backendConfigured -> Text("Type-checking needs the compile backend, which this deployment does not have.")
				typeChecked -> {
					Span({ classes(PlaygroundStyle.levelSuccess) }) { LucideCircleCheck() }
					Text("No problems in $USER_FILE_NAME.")
				}

				else -> Text("Problems show up here once an edit is type-checked.")
			}
		}
		return
	}

	problems.sortedWith(compareBy({ it.severity != DiagnosticSeverity.ERROR }, { it.startLine }, { it.startColumn })).forEach { diagnostic ->
		Button({
			classes(PlaygroundStyle.panelRow, PlaygroundStyle.panelRowButton)
			title("Go to line ${diagnostic.startLine}")
			onClick { onSelect(diagnostic) }
		}) {
			Span({ classes(PlaygroundStyle.levelIcon, levelClass(diagnostic.severity.level)) }) { LevelIcon(diagnostic.severity.level) }
			Span({ classes(PlaygroundStyle.panelMessage) }) { Text(diagnostic.message) }
			Span({ classes(PlaygroundStyle.panelMeta) }) { Text("$USER_FILE_NAME [${diagnostic.startLine}, ${diagnostic.startColumn}]") }
		}
	}
}

@Composable
private fun Log() {
	if (BuildLog.entries.isEmpty()) {
		Div({ classes(PlaygroundStyle.panelEmpty) }) { Text("Compiles, runs and their timings are logged here.") }
		return
	}

	// Newest first, so the latest step is always in view without scrolling a growing list.
	BuildLog.entries.asReversed().forEach { entry ->
		Div({ classes(PlaygroundStyle.panelRow) }) {
			Span({ classes(PlaygroundStyle.logTime) }) { Text(entry.time) }
			Span({ classes(PlaygroundStyle.levelIcon, levelClass(entry.level)) }) { LevelIcon(entry.level) }
			Span({ classes(PlaygroundStyle.panelMessage) }) {
				Text(entry.message)
				entry.detail?.let { Span({ classes(PlaygroundStyle.panelMeta) }) { Text(it) } }
			}
		}
	}
}

@Composable
private fun Harness() {
	Div({ classes(PlaygroundStyle.panelNote) }) {
		Text("Compiled next to $USER_FILE_NAME and never shown in the editor: it calls your playground() and hands the files to the page.")
	}

	Div({
		classes(PlaygroundStyle.harness)
		id(HARNESS_ID)
	}) {
		CodeBlock(PLAYGROUND_HARNESS, "kotlin", "line-numbers")
	}
}

private val DiagnosticSeverity.level
	get() = when (this) {
		DiagnosticSeverity.ERROR -> LogLevel.ERROR
		DiagnosticSeverity.INFO -> LogLevel.INFO
		DiagnosticSeverity.WARNING -> LogLevel.WARNING
	}

private fun levelClass(level: LogLevel) = when (level) {
	LogLevel.ERROR -> PlaygroundStyle.levelError
	LogLevel.INFO -> PlaygroundStyle.levelInfo
	LogLevel.SUCCESS -> PlaygroundStyle.levelSuccess
	LogLevel.WARNING -> PlaygroundStyle.levelWarning
}

@Composable
private fun LevelIcon(level: LogLevel) = when (level) {
	LogLevel.ERROR -> LucideCircleX()
	LogLevel.INFO -> LucideInfo()
	LogLevel.SUCCESS -> LucideCircleCheck()
	LogLevel.WARNING -> LucideTriangleAlert()
}
