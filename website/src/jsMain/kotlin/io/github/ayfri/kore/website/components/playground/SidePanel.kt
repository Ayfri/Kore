package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.*
import com.varabyte.kobweb.silk.components.icons.lucide.LucideAppWindow
import com.varabyte.kobweb.silk.components.icons.lucide.LucideBraces
import com.varabyte.kobweb.silk.components.icons.lucide.LucideChevronRight
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCloudLightning
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCrosshair
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFileCode
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFolder
import com.varabyte.kobweb.silk.components.icons.lucide.LucideGamepad2
import com.varabyte.kobweb.silk.components.icons.lucide.LucideGem
import com.varabyte.kobweb.silk.components.icons.lucide.LucideGrid3x3
import com.varabyte.kobweb.silk.components.icons.lucide.LucideHammer
import com.varabyte.kobweb.silk.components.icons.lucide.LucideHand
import com.varabyte.kobweb.silk.components.icons.lucide.LucideHourglass
import com.varabyte.kobweb.silk.components.icons.lucide.LucideListOrdered
import com.varabyte.kobweb.silk.components.icons.lucide.LucideMessageSquareText
import com.varabyte.kobweb.silk.components.icons.lucide.LucideMinus
import com.varabyte.kobweb.silk.components.icons.lucide.LucidePanelRight
import com.varabyte.kobweb.silk.components.icons.lucide.LucidePlus
import com.varabyte.kobweb.silk.components.icons.lucide.LucideRefreshCw
import com.varabyte.kobweb.silk.components.icons.lucide.LucideRotateCcw
import com.varabyte.kobweb.silk.components.icons.lucide.LucideSearch
import com.varabyte.kobweb.silk.components.icons.lucide.LucideSprout
import com.varabyte.kobweb.silk.components.icons.lucide.LucideSword
import com.varabyte.kobweb.silk.components.icons.lucide.LucideTags
import com.varabyte.kobweb.silk.components.icons.lucide.LucideTrophy
import com.varabyte.kobweb.silk.components.icons.lucide.LucideUsers
import com.varabyte.kobweb.silk.components.icons.lucide.LucideVariable
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
	var collapsed by remember { mutableStateOf(emptySet<String>()) }
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
			// A search shows every match, folded categories included.
			val open = needle.isNotEmpty() || category !in collapsed

			Button({
				classes(PlaygroundStyle.sideCategory, PlaygroundStyle.sideCategoryToggle)
				attr("aria-expanded", open.toString())
				onClick { collapsed = if (category in collapsed) collapsed - category else collapsed + category }
			}) {
				LucideChevronRight()
				CategoryIcon(category)
				Text(category)
				Span({ classes(PlaygroundStyle.treeCount) }) { Text(examples.size.toString()) }
			}

			if (open) examples.forEach { example ->
				val active = example == current

				Button({
					classes(PlaygroundStyle.exampleEntry)
					if (active) classes(PlaygroundStyle.exampleEntryActive)
					if (!active) title(example.description)
					onClick { onSelect(example) }
				}) {
					ExampleIcon(example.slug)

					Span({ classes(PlaygroundStyle.exampleText) }) {
						Span({ classes(PlaygroundStyle.exampleTitle) }) {
							Text(example.title)
							if (active && dirty) Span({ classes(PlaygroundStyle.dirtyDot) }) {
								Span({ classes(PlaygroundStyle.srOnly) }) { Text("edited") }
							}
						}

						if (active) Span({ classes(PlaygroundStyle.exampleDescription) }) { Text(example.description) }
					}
				}
			}
		}
	}

	Div({ classes(PlaygroundStyle.sideFooter) }) {
		Text("Every example is compiled and run when the site is built, so its pack shows instantly.")
	}
}

/** Categories come as labels from `:playground-examples`, so one added there without an icon here gets a plain folder. */
@Composable
private fun CategoryIcon(category: String) = when (category) {
	"Basics" -> LucideSprout()
	"Data-driven" -> LucideBraces()
	"Gameplay" -> LucideGamepad2()
	else -> LucideFolder()
}

/** What each example is about at a glance, by slug; one added in `:playground-examples` without an icon here gets a code file. */
@Composable
private fun ExampleIcon(slug: String) = when (slug) {
	"advancement" -> LucideTrophy()
	"custom-item" -> LucideSword()
	"dialogs" -> LucideAppWindow()
	"hello-world" -> LucideHand()
	"item-modifiers" -> LucideHammer()
	"load-and-tick" -> LucideRefreshCw()
	"loot-table" -> LucideGem()
	"macros" -> LucideVariable()
	"predicates" -> LucideCloudLightning()
	"raycast" -> LucideCrosshair()
	"recipes" -> LucideGrid3x3()
	"scheduling" -> LucideHourglass()
	"scoreboards" -> LucideListOrdered()
	"selectors" -> LucideUsers()
	"sidebar" -> LucidePanelRight()
	"tags" -> LucideTags()
	"text-components" -> LucideMessageSquareText()
	else -> LucideFileCode()
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
		SettingSwitch("Add imports on the fly", "Import a Kore name once it's typed, when a single declaration fits.", autoImport) { autoImport = it }

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
