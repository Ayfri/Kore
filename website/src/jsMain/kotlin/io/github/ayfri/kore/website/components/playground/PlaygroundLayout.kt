package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class SidebarView {
	EXAMPLES,
	SETTINGS,
}

enum class PanelTab {
	HARNESS,
	LOG,
	PROBLEMS,
}

/** Which pane, if any, is currently taking over the viewport. */
enum class MaximizedPane {
	EDITOR,
	NONE,
	OUTPUT;

	fun toggle(pane: MaximizedPane) = if (this == pane) NONE else pane
}

/** How the IDE frame is arranged. Panel visibility and sizes are kept across visits, the fullscreen states are not. */
object PlaygroundLayout {
	const val DEFAULT_SPLIT = 0.55
	val SPLIT_RANGE = 0.2..0.8
	private const val DEFAULT_PANEL_HEIGHT = 180
	val PANEL_HEIGHTS = 90..900

	/** The file tree of the output pane, hidden to give a narrow pane's width to the preview. */
	var explorerOpen by persisted("explorerOpen", true)
	var panelHeight by persisted("panelHeight", DEFAULT_PANEL_HEIGHT, PANEL_HEIGHTS)
	var panelOpen by persisted("panelOpen", true)
	var panelTab by persisted("panelTab", PanelTab.PROBLEMS)
	var sidebarOpen by persisted("sidebarOpen", true)
	var sidebarView by persisted("sidebarView", SidebarView.EXAMPLES)

	/** Share of the workspace width given to the editor. */
	var splitFraction by persisted("split", DEFAULT_SPLIT, SPLIT_RANGE)

	/** The whole IDE covers the viewport, site header included. */
	var focusMode by mutableStateOf(false)
	var maximizedPane by mutableStateOf(MaximizedPane.NONE)

	/** Opens [view], or closes the sidebar when it already shows [view], like an IDE activity bar. */
	fun toggleSidebar(view: SidebarView) {
		sidebarOpen = !(sidebarOpen && sidebarView == view)
		sidebarView = view
	}

	fun showPanel(tab: PanelTab) {
		panelTab = tab
		panelOpen = true
	}
}
