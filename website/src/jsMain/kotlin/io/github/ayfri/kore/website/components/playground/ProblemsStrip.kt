package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/**
 * Compiler messages listed under the editor, in addition to the squiggles Monaco draws.
 *
 * Clicking an entry jumps the caret to the offending position, which is why diagnostics from the hidden
 * harness are dropped: they point at a file the visitor cannot see.
 */
@Composable
fun ProblemsStrip(diagnostics: List<PlaygroundDiagnostic>, onSelect: (PlaygroundDiagnostic) -> Unit) {
	val visible = diagnostics.filter { it.file == USER_FILE_NAME }
	if (visible.isEmpty()) return

	Div({ classes(PlaygroundStyle.problems) }) {
		visible.forEach { diagnostic ->
			Button({
				classes(PlaygroundStyle.problem)
				onClick { onSelect(diagnostic) }
			}) {
				Span({ classes(severityClass(diagnostic.severity)) }) {
					Text(if (diagnostic.severity == DiagnosticSeverity.ERROR) "error" else "warning")
				}

				Span({ classes(PlaygroundStyle.problemPosition) }) {
					Text("${diagnostic.startLine}:${diagnostic.startColumn}")
				}

				Span { Text(diagnostic.message) }
			}
		}
	}
}

private fun severityClass(severity: DiagnosticSeverity) = when (severity) {
	DiagnosticSeverity.ERROR -> PlaygroundStyle.problemError
	else -> PlaygroundStyle.problemWarning
}
