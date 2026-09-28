package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.silk.components.icons.lucide.LucideBookOpen
import com.varabyte.kobweb.silk.components.icons.lucide.LucideKeyboard
import com.varabyte.kobweb.silk.components.icons.lucide.LucideLibraryBig
import com.varabyte.kobweb.silk.components.icons.lucide.LucidePanelBottom
import com.varabyte.kobweb.silk.components.icons.lucide.LucideSettings2
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/** The icon strip on the left edge: sidebar views on top, panel, shortcuts and docs at the bottom. */
@Composable
fun ActivityBar(problemCount: Int, onCommand: (PlaygroundCommand) -> Unit) {
	val sidebarView = PlaygroundLayout.sidebarView.takeIf { PlaygroundLayout.sidebarOpen }

	Div({ classes(PlaygroundStyle.activityBar) }) {
		Div({ classes(PlaygroundStyle.activityGroup) }) {
			ActivityButton("Examples", sidebarView == SidebarView.EXAMPLES, { PlaygroundLayout.toggleSidebar(SidebarView.EXAMPLES) }) {
				LucideLibraryBig()
			}

			ActivityButton("Settings", sidebarView == SidebarView.SETTINGS, { PlaygroundLayout.toggleSidebar(SidebarView.SETTINGS) }) {
				LucideSettings2()
			}
		}

		Div({ classes(PlaygroundStyle.activityGroup) }) {
			ActivityButton(
				"Problems and build log",
				PlaygroundLayout.panelOpen,
				{ onCommand(PlaygroundCommand.TOGGLE_PANEL) },
				PlaygroundCommand.TOGGLE_PANEL.keys,
				problemCount,
			) {
				LucidePanelBottom()
			}

			ActivityButton("Keyboard shortcuts", false, { onCommand(PlaygroundCommand.SHORTCUTS) }) { LucideKeyboard() }

			A("/docs/home", {
				classes(PlaygroundStyle.activityButton)
				target(ATarget.Blank)
				title("Kore documentation")
				attr("aria-label", "Kore documentation")
			}) {
				LucideBookOpen()
			}
		}
	}
}

@Composable
private fun ActivityButton(
	label: String,
	active: Boolean,
	onClick: () -> Unit,
	keys: List<String>? = null,
	badge: Int = 0,
	icon: @Composable () -> Unit,
) = ToolButton(label, onClick, active = active, keys = keys, classes = arrayOf(PlaygroundStyle.activityButton)) {
	icon()
	if (badge > 0) Span({ classes(PlaygroundStyle.activityBadge) }) { Text(if (badge > 99) "99+" else badge.toString()) }
}
