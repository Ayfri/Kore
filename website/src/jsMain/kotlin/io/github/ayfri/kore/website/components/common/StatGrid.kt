package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.utils.Span
import io.github.ayfri.kore.website.utils.lineHeight
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.monoFont
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.Div

data class Stat(val value: String, val label: String)

/** Four [stats] side by side (two under md), split by 1 px lines. Pages rendering it inject [StatGridStyle] once. */
@Composable
fun StatGrid(stats: List<Stat>, vararg extraClasses: String) {
	Div({ classes(StatGridStyle.grid, *extraClasses) }) {
		stats.forEach { stat ->
			Div({ classes(StatGridStyle.stat) }) {
				Span(stat.value, StatGridStyle.value)
				Span(stat.label, StatGridStyle.label)
			}
		}
	}
}

object StatGridStyle : StyleSheet() {
	/** The 1 px lines are the grid background showing through the gaps. */
	val grid by style {
		backgroundColor(LandingVars.Border.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(1.2.cssRem)
		display(DisplayStyle.Grid)
		gap(1.px)
		gridTemplateColumns("repeat(4, minmax(0, 1fr))")
		overflow(Overflow.Hidden)

		mdMax(self) {
			gridTemplateColumns("repeat(2, minmax(0, 1fr))")
		}
	}

	val stat by style {
		backgroundColor(LandingVars.Card.value())
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.2.cssRem)
		justifyContent(JustifyContent.Center)
		padding(1.1.cssRem, 0.8.cssRem)
	}

	val value by style {
		color(LandingVars.AccentStrong.value())
		fontSize(1.9.cssRem)
		fontWeight(700)
		lineHeight(1.2)
		monoFont()
	}

	val label by style {
		color(LandingVars.Muted.value())
		fontSize(0.88.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
	}

	/** A denser grid for a page header. */
	val compact by style {
		borderRadius(1.cssRem)

		child(self, className(stat)) style {
			gap(0.15.cssRem)
			padding(0.65.cssRem, 1.cssRem)
		}

		desc(self, className(value)) style {
			fontSize(1.2.cssRem)
		}

		desc(self, className(label)) style {
			fontSize(0.78.cssRem)
		}
	}
}
