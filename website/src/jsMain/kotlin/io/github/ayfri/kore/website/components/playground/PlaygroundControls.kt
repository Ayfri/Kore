package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.Composable
import io.github.ayfri.kore.website.components.common.BrandIcon
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/** The Kotlin logo marking a Kotlin source, tinted like the site's keywords. */
@Composable
fun KotlinIcon() = Span({ classes(PlaygroundStyle.kotlinIcon) }) { BrandIcon("kotlin") }

/** The keys of a shortcut as key caps. */
@Composable
fun Keys(keys: List<String>) = Span({ classes(PlaygroundStyle.keys) }) {
	keys.forEach { key -> Span({ classes(PlaygroundStyle.kbd) }) { Text(key) } }
}

/**
 * An icon button of the IDE chrome. [label] is its tooltip and accessible name, followed by the shortcut when it has one.
 * [active] draws it pressed, for a button that toggles something on.
 */
@Composable
fun ToolButton(
	label: String,
	onClick: () -> Unit,
	active: Boolean = false,
	enabled: Boolean = true,
	keys: List<String>? = null,
	vararg classes: String,
	content: @Composable () -> Unit,
) = Button({
	classes(PlaygroundStyle.toolButton, *classes)
	if (active) classes(PlaygroundStyle.toolButtonActive)
	if (!enabled) attr("disabled", "")
	attr("aria-label", label)
	if (active) attr("aria-pressed", "true")
	title(keys?.let { "$label (${it.joinToString("+")})" } ?: label)
	onClick { if (enabled) onClick() }
}) {
	content()
}

/** A thin vertical rule between groups of [ToolButton]s. */
@Composable
fun ToolSeparator() = Div({ classes(PlaygroundStyle.toolSeparator) })

/** An on/off switch row of the settings panel. */
@Composable
fun SettingSwitch(label: String, description: String?, checked: Boolean, onChange: (Boolean) -> Unit) = Button({
	classes(PlaygroundStyle.settingRow)
	attr("role", "switch")
	attr("aria-checked", checked.toString())
	onClick { onChange(!checked) }
}) {
	Span({ classes(PlaygroundStyle.settingText) }) {
		Span({ classes(PlaygroundStyle.settingLabel) }) { Text(label) }
		description?.let { Span({ classes(PlaygroundStyle.settingDescription) }) { Text(it) } }
	}

	Span({
		classes(PlaygroundStyle.switchTrack)
		if (checked) classes(PlaygroundStyle.switchTrackOn)
	}) {
		Span({ classes(PlaygroundStyle.switchThumb) })
	}
}
