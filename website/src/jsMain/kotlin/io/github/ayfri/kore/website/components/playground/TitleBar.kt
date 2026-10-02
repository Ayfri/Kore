package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.silk.components.icons.lucide.*
import org.jetbrains.compose.web.dom.*

/**
 * The top bar of the IDE: what is open, Run with the live-rebuild switch, and the actions on the whole snippet.
 *
 * Running an untouched example needs no backend, so [canRun] is only false for an edited buffer on a deployment without
 * one; the button stays visible but inert, and the output pane explains why.
 */
@Composable
fun TitleBar(
	example: PlaygroundExample,
	dirty: Boolean,
	busy: Boolean,
	canRun: Boolean,
	hasPack: Boolean,
	shareLabel: String,
	onCommand: (PlaygroundCommand) -> Unit,
	onOpenExamples: () -> Unit,
) {
	val runKeys = PlaygroundCommand.RUN.keys.orEmpty()

	Div({ classes(PlaygroundStyle.titleBar) }) {
		Div({ classes(PlaygroundStyle.titleGroup) }) {
			Img("/monogram.png", "Kore") { classes(PlaygroundStyle.titleLogo) }
			H1({ classes(PlaygroundStyle.titleName) }) { Text("Playground") }
			Span({ classes(PlaygroundStyle.titleSlash) }) { Text("/") }

			Button({
				classes(PlaygroundStyle.titleExample)
				title("Browse the examples")
				onClick { onOpenExamples() }
			}) {
				KotlinIcon()
				Span({ classes(PlaygroundStyle.titleExampleName) }) { Text(example.title) }
				if (dirty) Span({ classes(PlaygroundStyle.dirtyDot) }) { Span({ classes(PlaygroundStyle.srOnly) }) { Text("edited") } }
				LucideChevronDown()
			}
		}

		Div({ classes(PlaygroundStyle.titleGroup) }) {
			Button({
				classes(PlaygroundStyle.runButton)
				if (!canRun || busy) attr("disabled", "")
				title("Run (${runKeys.joinToString("+")})")
				onClick { onCommand(PlaygroundCommand.RUN) }
			}) {
				if (busy) Span({ classes(PlaygroundStyle.spinnerSmall) }) else LucidePlay()
				Text(if (busy) "Running" else "Run")
				Span({ classes(PlaygroundStyle.wideOnly) }) { Keys(runKeys) }
			}

			Button({
				classes(PlaygroundStyle.liveChip)
				if (PlaygroundSettings.autoBuild) classes(PlaygroundStyle.liveChipOn)
				attr("role", "switch")
				attr("aria-checked", PlaygroundSettings.autoBuild.toString())
				title(if (PlaygroundSettings.autoBuild) "Live rebuild is on: an edit rebuilds once you stop typing" else "Live rebuild is off: only Run compiles")
				onClick { onCommand(PlaygroundCommand.TOGGLE_AUTO_BUILD) }
			}) {
				if (PlaygroundSettings.autoBuild) LucideZap() else LucideZapOff()
				Span({ classes(PlaygroundStyle.wideOnly) }) { Text("Live") }
			}

			ToolSeparator()

			ToolButton("Open a Kotlin file", { onCommand(PlaygroundCommand.OPEN_FILE) }, keys = PlaygroundCommand.OPEN_FILE.keys, classes = arrayOf(PlaygroundStyle.wideOnly)) {
				LucideFolderOpen()
			}

			ToolButton("Save the draft in this browser", { onCommand(PlaygroundCommand.SAVE) }, keys = PlaygroundCommand.SAVE.keys, classes = arrayOf(PlaygroundStyle.wideOnly)) {
				LucideSave()
			}

			ToolButton("Download main.kt", { onCommand(PlaygroundCommand.DOWNLOAD_SOURCE) }, classes = arrayOf(PlaygroundStyle.wideOnly)) {
				LucideFileDown()
			}

			ToolButton("Copy a share link", { onCommand(PlaygroundCommand.SHARE) }, classes = arrayOf(PlaygroundStyle.toolButtonLabelled)) {
				LucideShare2()
				Span({ classes(PlaygroundStyle.wideOnly) }) { Text(shareLabel) }
			}

			ToolButton(
				"Download the datapack",
				{ onCommand(PlaygroundCommand.DOWNLOAD_ZIP) },
				enabled = hasPack,
				keys = PlaygroundCommand.DOWNLOAD_ZIP.keys,
				classes = arrayOf(PlaygroundStyle.toolButtonLabelled),
			) {
				LucideDownload()
				Span({ classes(PlaygroundStyle.wideOnly) }) { Text(".zip") }
			}

			ToolSeparator()

			ToolButton(
				if (PlaygroundLayout.focusMode) "Exit focus mode" else "Focus mode, the IDE takes the whole window",
				{ onCommand(PlaygroundCommand.FOCUS_MODE) },
				active = PlaygroundLayout.focusMode,
			) {
				if (PlaygroundLayout.focusMode) LucideMinimize() else LucideMaximize()
			}
		}
	}
}
