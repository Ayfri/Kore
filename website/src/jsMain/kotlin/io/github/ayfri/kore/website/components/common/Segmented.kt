package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

/**
 * A bar of mutually exclusive buttons, one per [options] entry, [selected] tinted and `aria-pressed`.
 * [extra] draws after an option's label, e.g. a progress bar. Pages rendering it inject [SegmentedStyle] once.
 *
 * ```kotlin
 * Segmented(SortOrder.entries, order, { order = it }, { it.label }, SegmentedStyle.flat)
 * ```
 */
@Composable
fun <T> Segmented(
	options: List<T>,
	selected: T,
	onSelect: (T) -> Unit,
	label: (T) -> String,
	vararg barClasses: String,
	extra: @Composable (T) -> Unit = {},
) {
	Div({ classes(SegmentedStyle.bar, *barClasses) }) {
		options.forEach { option ->
			Button({
				classes(SegmentedStyle.option)
				if (option == selected) classes(SegmentedStyle.selected)
				attr("aria-pressed", "${option == selected}")
				onClick { onSelect(option) }
			}) {
				Text(label(option))
				extra(option)
			}
		}
	}
}

object SegmentedStyle : StyleSheet() {
	/** A framed pill bar, as wide as its options and scrolling sideways when they overflow. */
	val bar by style {
		backgroundColor(LandingVars.Card.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(999.px)
		display(DisplayStyle.Flex)
		gap(0.25.cssRem)
		maxWidth(100.percent)
		overflowX(Overflow.Auto)
		padding(0.25.cssRem)
		scrollbarWidth(ScrollbarWidth.None)
		width(Width.FitContent)
	}

	val option by style {
		backgroundColor(Color.transparent)
		border(0.px)
		borderRadius(999.px)
		color(LandingVars.Muted.value())
		cursor(Cursor.Pointer)
		flexShrink(0)
		fontFamily("inherit")
		fontSize(0.82.cssRem)
		overflow(Overflow.Hidden)
		padding(0.35.cssRem, 0.85.cssRem)
		position(Position.Relative)
		transition(0.2.s, "background-color", "color")

		hover(self) style {
			color(LandingVars.Text.value())
		}
	}

	val selected by style {
		backgroundColor(LandingVars.Accent.value().alpha(0.2))
		color(LandingVars.Text.value())
	}

	/** Drops the frame and wraps the options, for a bar sitting in a header that already frames it. */
	val flat by style {
		backgroundColor(Color.transparent)
		border(0.px)
		borderRadius(0.px)
		flexWrap(FlexWrap.Wrap)
		overflowX(Overflow.Visible)
		padding(0.px)

		child(self, className(option)) style {
			borderRadius(0.5.cssRem)
			fontSize(0.92.cssRem)
			fontWeight(500)
			padding(0.5.cssRem, 1.cssRem)
		}

		child(self, className(selected)) style {
			backgroundColor(LandingVars.Text.value().alpha(0.07))
		}
	}
}
