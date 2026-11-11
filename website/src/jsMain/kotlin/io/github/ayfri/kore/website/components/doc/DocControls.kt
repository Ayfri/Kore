package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.McSprite
import io.github.ayfri.kore.website.components.common.McSpriteStyle
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.monoFont
import io.github.ayfri.kore.website.utils.smMax
import kotlin.math.round
import org.jetbrains.compose.web.attributes.maxLength
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.step
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.*

/** [content] under the mono [label], for a control without a `<label>` target of its own. */
@Composable
fun ControlField(label: String, content: @Composable () -> Unit) {
	Div({ classes(DocControlsStyle.control) }) {
		Span({ classes(DocControlsStyle.controlLabel) }) { Text(label) }
		content()
	}
}

/** A real vanilla value a widget can load: its pill [label], and what the caption says, a [note] about the data found in each [source] file or field. */
interface DocPreset {
	val label: String
	val note: String
	val source: List<String>
}

/** A mono [title] over the pills of [presets], and once one is [selected] the caption of where it comes from, flagged when [edited] since. */
@Composable
fun <T : DocPreset> PresetRow(title: String, groupLabel: String, presets: List<T>, selected: T?, edited: Boolean, onSelect: (T) -> Unit) {
	Div({ classes(DocControlsStyle.presets) }) {
		Span({ classes(DocControlsStyle.controlLabel) }) { Text(title) }
		PillGroup(groupLabel, presets, selected, { it.label }, onSelect = onSelect)
		selected?.let {
			Div({ classes(DocControlsStyle.caption) }) {
				Text("from ")
				it.source.forEachIndexed { index, source ->
					if (index > 0) Text(", ")
					Span({ classes(DocControlsStyle.captionFile) }) { Text(source) }
				}
				Text(": ${it.note}${if (edited) " (edited)" else ""}")
			}
		}
	}
}

/** A pill button, a toggle with `aria-pressed` when [pressed] is not null, drawing the vanilla texture [sprite] before the text. */
@Composable
fun Pill(text: String, pressed: Boolean? = null, sprite: String? = null, onClick: () -> Unit) {
	Button({
		classes(DocControlsStyle.pill)
		if (pressed == true) classes(DocControlsStyle.pillActive)
		pressed?.let { attr("aria-pressed", it.toString()) }
		attr("type", "button")
		onClick { onClick() }
	}) {
		sprite?.let { McSprite(it, null) }
		Text(text)
	}
}

/** Pills of which exactly [selected] is pressed, null pressing none, each drawing the texture [sprite] returns for its option. */
@Composable
fun <T : Any> PillGroup(label: String, options: List<T>, selected: T?, text: (T) -> String, sprite: (T) -> String? = { null }, onSelect: (T) -> Unit) {
	Div({
		classes(DocControlsStyle.group)
		attr("aria-label", label)
		attr("role", "group")
	}) {
		options.forEach { option -> Pill(text(option), option == selected, sprite(option)) { onSelect(option) } }
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

/** This value on the grid of [step] that starts at [min], then within `min..max`. Six decimals drop float noise like `0.15000000000000002`. */
private fun Double.snapped(min: Double, max: Double, step: Double) =
	(round((this - min) / step) * step + min).let { round(it * 1e6) / 1e6 }.coerceAtMost(max).coerceAtLeast(min) + 0.0

/** A range slider and a number input editing the same [value], labelled by [label]. [onChange] only gets values within `min..max` and on the [step]. */
@Composable
fun SliderField(inputId: String, label: String, value: Double, min: Double, max: Double, step: Double, onChange: (Double) -> Unit) {
	Div({ classes(DocControlsStyle.control) }) {
		Label(inputId, { classes(DocControlsStyle.controlLabel) }) { Text(label) }
		Div({ classes(DocControlsStyle.controlRow) }) {
			RangeInput(value, min, max, step) {
				classes(DocControlsStyle.slider)
				attr("aria-label", "$label slider")
				onInput { event -> event.value?.toDouble()?.takeUnless { it.isNaN() }?.let { onChange(it.snapped(min, max, step)) } }
			}
			NumberInput(value, min, max) {
				id(inputId)
				classes(DocControlsStyle.number)
				step(step)
				onInput { event -> event.value?.toDouble()?.takeUnless { it.isNaN() }?.let { onChange(it.snapped(min, max, step)) } }
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

/** This text as a Kotlin string literal for the code a widget shows, the characters that would end or interpolate it escaped. */
internal fun String.kotlin() = "\"" + replace("\\", "\\\\").replace("\"", "\\\"").replace("\$", "\\\$").replace("\n", "\\n").replace("\r", "\\r") + "\""

/** Controls shared by the interactive doc widgets, injected by `MarkdownLayout`. */
object DocControlsStyle : StyleSheet() {
	val caption by style {
		color(LandingVars.Muted.value())
		fontSize(0.8.cssRem)
	}

	val captionFile by style {
		color(LandingVars.Text.value())
		monoFont()
	}

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
		alignItems(AlignItems.Center)
		backgroundColor(LandingVars.Card.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(999.px)
		color(LandingVars.Muted.value())
		cursor(Cursor.Pointer)
		display(DisplayStyle.LegacyInlineFlex)
		fontSize(0.8.cssRem)
		monoFont()
		padding(0.3.cssRem, 0.8.cssRem)

		child(self, className(McSpriteStyle.sprite)) style {
			height(16.px)
			width(16.px)
		}
	}

	val pillActive by style {
		backgroundColor(LandingVars.Accent.value().alpha(0.18))
		border(1.px, LineStyle.Solid, LandingVars.Accent.value())
		color(LandingVars.Text.value())
	}

	val presets by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.5.cssRem)
	}

	val readout by style {
		color(LandingVars.Text.value())
		fontSize(0.9.cssRem)
		monoFont()
	}

	val slider by style {
		accentColor(AccentColor.of(LandingVars.Accent.value()))
		cursor(Cursor.Pointer)
		flexGrow(1)
		margin(0.px)
		minWidth(0.px)
	}

	val stack by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.9.cssRem)
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

	val widget by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		margin(1.5.cssRem, 0.px)
		minWidth(0.px)
		width(100.percent)
	}
}
