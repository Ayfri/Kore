package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.monoFont
import io.github.ayfri.kore.website.utils.smMax
import org.jetbrains.compose.web.attributes.maxLength
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.step
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.*

/** A pill button, a toggle with `aria-pressed` when [pressed] is not null. */
@Composable
fun Pill(text: String, pressed: Boolean? = null, onClick: () -> Unit) {
	Button({
		classes(DocControlsStyle.pill)
		if (pressed == true) classes(DocControlsStyle.pillActive)
		pressed?.let { attr("aria-pressed", it.toString()) }
		attr("type", "button")
		onClick { onClick() }
	}) { Text(text) }
}

/** Pills of which exactly [selected] is pressed, null pressing none. */
@Composable
fun <T : Any> PillGroup(label: String, options: List<T>, selected: T?, text: (T) -> String, onSelect: (T) -> Unit) {
	Div({
		classes(DocControlsStyle.group)
		attr("aria-label", label)
		attr("role", "group")
	}) {
		options.forEach { option -> Pill(text(option), option == selected) { onSelect(option) } }
	}
}

/** Pills that each press on their own, every one of [selected] being pressed. */
@Composable
fun <T : Any> ToggleGroup(label: String, options: List<T>, selected: Set<T>, text: (T) -> String, onToggle: (T) -> Unit) {
	Div({
		classes(DocControlsStyle.group)
		attr("aria-label", label)
		attr("role", "group")
	}) {
		options.forEach { option -> Pill(text(option), option in selected) { onToggle(option) } }
	}
}

/** A range slider and a number input editing the same [value], labelled by [label]. */
@Composable
fun SliderField(inputId: String, label: String, value: Double, min: Double, max: Double, step: Double, onChange: (Double) -> Unit) {
	Div({ classes(DocControlsStyle.control) }) {
		Label(inputId, { classes(DocControlsStyle.controlLabel) }) { Text(label) }
		Div({ classes(DocControlsStyle.controlRow) }) {
			RangeInput(value, min, max, step) {
				classes(DocControlsStyle.slider)
				attr("aria-label", "$label slider")
				onInput { event -> event.value?.toDouble()?.takeUnless { it.isNaN() }?.let(onChange) }
			}
			NumberInput(value, min, max) {
				id(inputId)
				classes(DocControlsStyle.number)
				step(step)
				onInput { event -> event.value?.toDouble()?.takeUnless { it.isNaN() }?.let(onChange) }
			}
		}
	}
}

/** A single-line text input labelled by [label]. */
@Composable
fun TextField(inputId: String, label: String, value: String, placeholder: String = "", maxLength: Int? = null, onChange: (String) -> Unit) {
	Div({ classes(DocControlsStyle.control) }) {
		Label(inputId, { classes(DocControlsStyle.controlLabel) }) { Text(label) }
		TextInput(value) {
			id(inputId)
			classes(DocControlsStyle.text)
			attr("autocomplete", "off")
			attr("spellcheck", "false")
			if (placeholder.isNotEmpty()) placeholder(placeholder)
			maxLength?.let { maxLength(it) }
			onInput { onChange(it.value) }
		}
	}
}

/** Controls shared by the interactive doc widgets, injected by `MarkdownLayout`. */
object DocControlsStyle : StyleSheet() {
	val control by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.3.cssRem)
		minWidth(0.px)
	}

	val controlLabel by style {
		color(LandingVars.Muted.value())
		fontSize(0.75.cssRem)
		monoFont()
	}

	val controlRow by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.7.cssRem)
		minWidth(0.px)
	}

	val grid by style {
		display(DisplayStyle.Grid)
		gap(0.9.cssRem, 1.5.cssRem)
		gridTemplateColumns("repeat(2, minmax(0, 1fr))")

		smMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val group by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.35.cssRem)
	}

	val number by style {
		backgroundColor(LandingVars.Surface.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.4.cssRem)
		boxSizing(BoxSizing.BorderBox)
		color(LandingVars.Text.value())
		fontSize(0.85.cssRem)
		monoFont()
		padding(0.3.cssRem, 0.5.cssRem)
		width(5.5.cssRem)
	}

	val panel by style {
		backgroundColor(LandingVars.Pane.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.8.cssRem)
		minWidth(0.px)
		padding(1.cssRem)
	}

	val pill by style {
		backgroundColor(LandingVars.Card.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(999.px)
		color(LandingVars.Muted.value())
		cursor(Cursor.Pointer)
		fontSize(0.8.cssRem)
		monoFont()
		padding(0.3.cssRem, 0.8.cssRem)
	}

	val pillActive by style {
		backgroundColor(LandingVars.Accent.value().alpha(0.18))
		border(1.px, LineStyle.Solid, LandingVars.Accent.value())
		color(LandingVars.Text.value())
	}

	val slider by style {
		accentColor(AccentColor.of(LandingVars.Accent.value()))
		cursor(Cursor.Pointer)
		flexGrow(1)
		margin(0.px)
		minWidth(0.px)
	}

	val text by style {
		backgroundColor(LandingVars.Surface.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.4.cssRem)
		boxSizing(BoxSizing.BorderBox)
		color(LandingVars.Text.value())
		fontSize(0.85.cssRem)
		monoFont()
		padding(0.3.cssRem, 0.5.cssRem)
		width(100.percent)
	}
}
