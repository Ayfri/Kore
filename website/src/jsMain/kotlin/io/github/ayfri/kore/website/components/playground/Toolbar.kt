package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.silk.components.icons.lucide.LucideChevronDown
import com.varabyte.kobweb.silk.components.icons.lucide.LucideLink
import com.varabyte.kobweb.silk.components.icons.lucide.LucidePlay
import com.varabyte.kobweb.silk.components.icons.lucide.LucideRotateCcw
import io.github.ayfri.kore.website.components.common.Button
import io.github.ayfri.kore.website.components.common.ButtonColor
import io.github.ayfri.kore.website.components.common.ButtonVariant
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/**
 * Actions above the workspace: run the snippet, swap in an example, share the buffer.
 *
 * Running needs a compile backend, so [canRun] is false whenever none is configured for the deployment;
 * the button stays visible but inert, and the output pane explains why.
 */
@Composable
fun PlaygroundToolbar(
	selectedExample: PlaygroundExample?,
	busy: Boolean,
	canRun: Boolean,
	shareLabel: String,
	onRun: () -> Unit,
	onSelectExample: (PlaygroundExample) -> Unit,
	onReset: () -> Unit,
	onShare: () -> Unit,
) {
	val runnable = canRun && !busy

	Div({ classes(PlaygroundStyle.toolbar) }) {
		Div({ classes(PlaygroundStyle.toolbarGroup) }) {
			Button(
				name = if (busy) "Running..." else "Run",
				onClick = { if (runnable) onRun() },
				color = ButtonColor.PRIMARY,
				icon = { LucidePlay() },
				classes = playgroundButtonClasses(runnable),
			)

			ExamplePicker(selectedExample, onSelectExample)
		}

		Div({ classes(PlaygroundStyle.toolbarGroup) }) {
			Button(
				name = "Reset",
				onClick = onReset,
				variant = ButtonVariant.GHOST,
				icon = { LucideRotateCcw() },
				classes = arrayOf(PlaygroundStyle.toolbarButton),
			)

			Button(
				name = shareLabel,
				onClick = onShare,
				variant = ButtonVariant.GHOST,
				icon = { LucideLink() },
				classes = arrayOf(PlaygroundStyle.toolbarButton),
			)

			Span({ classes(PlaygroundStyle.badge) }) {
				Text("Kore ${AppGlobals["projectVersion"] ?: "?"} - MC ${AppGlobals["minecraftVersion"] ?: "?"}")
			}
		}
	}
}

/** The example list outgrew a row of buttons, so it lives in a menu grouped by category. */
@Composable
private fun ExamplePicker(selectedExample: PlaygroundExample?, onSelectExample: (PlaygroundExample) -> Unit) {
	var open by remember { mutableStateOf(false) }

	Div({ classes(PlaygroundStyle.picker) }) {
		Button(
			name = selectedExample?.title ?: "Examples",
			onClick = { open = !open },
			variant = ButtonVariant.OUTLINE,
			icon = { LucideChevronDown() },
			classes = arrayOf(PlaygroundStyle.toolbarButton),
		)

		if (open) {
			Div({
				classes(PlaygroundStyle.pickerBackdrop)
				onClick { open = false }
			})

			Div({ classes(PlaygroundStyle.pickerMenu) }) {
				playgroundExamplesByCategory.forEach { (category, examples) ->
					Div({ classes(PlaygroundStyle.pickerCategory) }) { Text(category) }

					examples.forEach { example ->
						Div({
							classes(*pickerEntryClasses(example == selectedExample))
							onClick {
								onSelectExample(example)
								open = false
							}
						}) {
							Span({ classes(PlaygroundStyle.pickerTitle) }) { Text(example.title) }
							Span({ classes(PlaygroundStyle.pickerDescription) }) { Text(example.description) }
						}
					}
				}
			}
		}
	}
}

private fun pickerEntryClasses(active: Boolean) = when {
	active -> arrayOf(PlaygroundStyle.pickerEntry, PlaygroundStyle.pickerEntryActive)
	else -> arrayOf(PlaygroundStyle.pickerEntry)
}

private fun playgroundButtonClasses(enabled: Boolean) = when {
	enabled -> arrayOf(PlaygroundStyle.toolbarButton)
	else -> arrayOf(PlaygroundStyle.toolbarButton, PlaygroundStyle.disabledButton)
}
