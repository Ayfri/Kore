package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.silk.components.icons.lucide.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/**
 * The strip along the bottom edge: backend and build state and problem counts on the left, the caret and what the
 * editor holds on the right. Every item that has somewhere to go is a button.
 */
@Composable
fun StatusBar(
	backendConfigured: Boolean,
	activity: String?,
	errors: Int,
	warnings: Int,
	cursor: CursorInfo,
	output: OutputState,
	onGoToLine: () -> Unit,
) {
	Div({ classes(PlaygroundStyle.statusBar) }) {
		Div({ classes(PlaygroundStyle.statusGroup) }) {
			Span({
				classes(PlaygroundStyle.statusItem)
				if (activity != null) classes(PlaygroundStyle.statusItemBusy)
				title(if (backendConfigured) "Compiles go to the Kore compile backend" else "This deployment has no compile backend: only the examples run")
			}) {
				when {
					activity != null -> Span({ classes(PlaygroundStyle.spinnerSmall) })
					backendConfigured -> LucideServer()
					else -> LucideServerOff()
				}

				Text(activity ?: if (backendConfigured) "Backend" else "Examples only")
			}

			StatusButton("Show the problems", { PlaygroundLayout.showPanel(PanelTab.PROBLEMS) }) {
				Span({
					classes(PlaygroundStyle.statusCount)
					if (errors > 0) classes(PlaygroundStyle.levelError)
				}) {
					LucideCircleX()
					Text(errors.toString())
				}

				Span({
					classes(PlaygroundStyle.statusCount)
					if (warnings > 0) classes(PlaygroundStyle.levelWarning)
				}) {
					LucideTriangleAlert()
					Text(warnings.toString())
				}
			}

			(output as? OutputState.Ready)?.let { ready ->
				StatusButton("Show the build log", { PlaygroundLayout.showPanel(PanelTab.LOG) }, wide = true) {
					LucidePackage()
					Text("${ready.files.size} files, ${humanSize(ready.files.sumOf { it.content.length })}, ${ready.origin.describe()}")
				}
			}
		}

		Div({ classes(PlaygroundStyle.statusGroup) }) {
			StatusButton("Go to line", onGoToLine) {
				Text("Ln ${cursor.line}, Col ${cursor.column}")
				if (cursor.selected > 0) Text(" (${cursor.selected} selected)")
			}

			Span({ classes(PlaygroundStyle.statusItem, PlaygroundStyle.wideOnly) }) { Text("Tab Size: 4") }
			Span({ classes(PlaygroundStyle.statusItem, PlaygroundStyle.wideOnly) }) { Text("UTF-8") }
			Span({ classes(PlaygroundStyle.statusItem, PlaygroundStyle.wideOnly) }) { Text("Kotlin") }

			A("/updates", {
				classes(PlaygroundStyle.statusItem, PlaygroundStyle.statusLink)
				title("The Kore and Minecraft versions this playground compiles against")
			}) {
				LucideTag()
				Text("Kore ${AppGlobals["projectVersion"] ?: "?"} · MC ${AppGlobals["minecraftVersion"] ?: "?"}")
			}
		}
	}
}

@Composable
private fun StatusButton(label: String, onClick: () -> Unit, wide: Boolean = false, content: @Composable () -> Unit) = Button({
	classes(PlaygroundStyle.statusItem, PlaygroundStyle.statusButton)
	if (wide) classes(PlaygroundStyle.wideOnly)
	title(label)
	onClick { onClick() }
}) {
	content()
}
