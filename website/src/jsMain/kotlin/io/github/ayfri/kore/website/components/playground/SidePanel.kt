package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.*
import com.varabyte.kobweb.silk.components.icons.lucide.LucideMinus
import com.varabyte.kobweb.silk.components.icons.lucide.LucidePlus
import com.varabyte.kobweb.silk.components.icons.lucide.LucideRotateCcw
import com.varabyte.kobweb.silk.components.icons.lucide.LucideSearch
import com.varabyte.kobweb.silk.components.icons.lucide.LucideX
import org.jetbrains.compose.web.dom.*

/** The sidebar next to the activity bar, showing whichever [SidebarView] is selected. */
@Composable
fun SidePanel(example: PlaygroundExample, dirty: Boolean, onSelectExample: (PlaygroundExample) -> Unit) {
	Aside({ classes(PlaygroundStyle.sidePanel) }) {
		Div({ classes(PlaygroundStyle.sectionHeader) }) {
			Span({ classes(PlaygroundStyle.sectionTitle) }) {
				Text(if (PlaygroundLayout.sidebarView == SidebarView.EXAMPLES) "Examples" else "Settings")
			}

			ToolButton("Close the sidebar", { PlaygroundLayout.sidebarOpen = false }, keys = PlaygroundCommand.TOGGLE_SIDEBAR.keys) { LucideX() }
		}

		when (PlaygroundLayout.sidebarView) {
			SidebarView.EXAMPLES -> ExamplesView(example, dirty, onSelectExample)
			SidebarView.SETTINGS -> SettingsView()
		}
	}
}

@Composable
private fun ExamplesView(current: PlaygroundExample, dirty: Boolean, onSelect: (PlaygroundExample) -> Unit) {
	var query by remember { mutableStateOf("") }
	val needle = query.trim().lowercase()
	val matches = playgroundExamplesByCategory.mapValues { (category, examples) ->
		examples.filter { needle in "${it.title} ${it.description} $category".lowercase() }
	}.filterValues { it.isNotEmpty() }

	Div({ classes(PlaygroundStyle.searchBox) }) {
		LucideSearch()
		SearchInput(query) {
			classes(PlaygroundStyle.searchInput)
			attr("placeholder", "Search ${playgroundExamples.size} examples")
			attr("aria-label", "Search the examples")
			onInput { query = it.value }
		}
	}

	Div({ classes(PlaygroundStyle.sideScroll) }) {
		if (matches.isEmpty()) Div({ classes(PlaygroundStyle.sideEmpty) }) { Text("No example matches \"$query\".") }

		matches.forEach { (category, examples) ->
			Div({ classes(PlaygroundStyle.sideCategory) }) {
				Text(category)
				Span({ classes(PlaygroundStyle.treeCount) }) { Text(examples.size.toString()) }
			}

			examples.forEach { example ->
				Button({
					classes(PlaygroundStyle.exampleEntry)
					if (example == current) classes(PlaygroundStyle.exampleEntryActive)
					onClick { onSelect(example) }
				}) {
					KotlinIcon()

					Span({ classes(PlaygroundStyle.exampleText) }) {
						Span({ classes(PlaygroundStyle.exampleTitle) }) {
							Text(example.title)
							if (example == current && dirty) Span({ classes(PlaygroundStyle.dirtyDot) }) {
								Span({ classes(PlaygroundStyle.srOnly) }) { Text("edited") }
							}
						}

						Span({ classes(PlaygroundStyle.exampleDescription) }) { Text(example.description) }
					}
				}
			}
		}
	}

	Div({ classes(PlaygroundStyle.sideFooter) }) {
		Text("Every example is compiled and run when the site is built, so its pack shows instantly.")
	}
}

@Composable
private fun SettingsView() = with(PlaygroundSettings) {
	Div({ classes(PlaygroundStyle.sideScroll) }) {
		Div({ classes(PlaygroundStyle.sideCategory) }) { Text("Editor") }

		Div({ classes(PlaygroundStyle.settingRow, PlaygroundStyle.settingRowStatic) }) {
			Span({ classes(PlaygroundStyle.settingText) }) {
				Span({ classes(PlaygroundStyle.settingLabel) }) { Text("Font size") }
			}

			Span({ classes(PlaygroundStyle.stepper) }) {
				ToolButton("Smaller", { fontSize-- }, enabled = fontSize > FONT_SIZES.first) { LucideMinus() }
				Span({ classes(PlaygroundStyle.stepperValue) }) { Text("${fontSize}px") }
				ToolButton("Larger", { fontSize++ }, enabled = fontSize < FONT_SIZES.last) { LucidePlus() }
			}
		}

		SettingSwitch("Word wrap", "Wrap long lines at the edge of the editor.", wordWrap) { wordWrap = it }
		SettingSwitch("Minimap", "A zoomed-out view of the code on the right.", minimap) { minimap = it }
		SettingSwitch("Sticky scroll", "Keep the enclosing blocks pinned while scrolling.", stickyScroll) { stickyScroll = it }
		SettingSwitch("Line numbers", null, lineNumbers) { lineNumbers = it }
		SettingSwitch("Show whitespace", "Draw tabs and spaces everywhere, not only in the selection.", renderWhitespace) { renderWhitespace = it }
		SettingSwitch("Font ligatures", "Join operators like -> and != into one glyph.", fontLigatures) { fontLigatures = it }

		Div({ classes(PlaygroundStyle.sideCategory) }) { Text("Build") }
		SettingSwitch("Live rebuild", "Type-check and rebuild once typing pauses. Off, only Run compiles.", autoBuild) { autoBuild = it }

		Div({ classes(PlaygroundStyle.sideCategory) }) { Text("Preview") }
		SettingSwitch("Pretty-print JSON", "Off, JSON shows minified, the way Minecraft reads it.", prettyJson) { prettyJson = it }

		Div({ classes(PlaygroundStyle.settingRow, PlaygroundStyle.settingRowStatic) }) {
			Span({ classes(PlaygroundStyle.settingText) }) {
				Span({ classes(PlaygroundStyle.settingLabel) }) { Text("JSON indent") }
				Span({ classes(PlaygroundStyle.settingDescription) }) { Text("Spaces per level of pretty-printed JSON.") }
			}

			Span({ classes(PlaygroundStyle.stepper) }) {
				JSON_INDENTS.forEach { spaces ->
					ToolButton(
						"$spaces spaces",
						{ jsonIndent = spaces },
						active = jsonIndent == spaces,
						enabled = prettyJson,
						classes = arrayOf(PlaygroundStyle.toolButtonLabelled),
					) {
						Text(spaces.toString())
					}
				}
			}
		}

		SettingSwitch("Wrap long lines", null, previewWrap) { previewWrap = it }
	}

	Div({ classes(PlaygroundStyle.sideFooter) }) {
		Button({
			classes(PlaygroundStyle.textButton)
			onClick { reset() }
		}) {
			LucideRotateCcw()
			Text("Restore the defaults")
		}
	}
}
