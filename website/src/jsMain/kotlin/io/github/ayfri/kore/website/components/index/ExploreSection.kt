package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.lucide.LucideArrowUpRight
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.StatGrid
import io.github.ayfri.kore.website.components.features.featureCategories
import io.github.ayfri.kore.website.components.features.featureStats
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.A as DomA

@Composable
fun ExploreSection() {
	Style(ExploreSectionStyle)

	Section({ classes(HomeSectionStyle.section) }) {
		SectionHeader(
			"Covers the whole game, not just commands",
			"From a single /give to a custom dimension, everything a datapack can hold has a typed API. Pick an area to see it in action.",
		)

		StatGrid(featureStats, ExploreSectionStyle.stats)

		Div({ classes(ExploreSectionStyle.grid) }) {
			featureCategories.forEach { category ->
				DomA("/features#${category.id}", { classes(ExploreSectionStyle.tile) }) {
					Span({ classes(ExploreSectionStyle.label) }) {
						category.icon()
						Text(category.title)
					}
					Span(category.headline, ExploreSectionStyle.headline)
					LucideArrowUpRight()
				}
			}
		}
	}
}

object ExploreSectionStyle : StyleSheet() {
	val stats by style {
		marginBottom(2.cssRem)
		marginTop(0.8.cssRem)
		marginX(auto)
		maxWidth(52.cssRem)
	}

	/** Tiles share 1 px borders like the stats above, drawn by the grid background showing through the gaps. */
	val grid by style {
		backgroundColor(LandingVars.Border.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(1.2.cssRem)
		display(DisplayStyle.Grid)
		gap(1.px)
		gridTemplateColumns("repeat(4, minmax(0, 1fr))")
		overflow(Overflow.Hidden)

		lgMax(self) {
			gridTemplateColumns("repeat(2, minmax(0, 1fr))")
		}

		xsMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val tile by style {
		backgroundColor(LandingVars.Card.value())
		color(LandingVars.Text.value())
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.7.cssRem)
		minHeight(8.cssRem)
		padding(1.3.cssRem)
		position(Position.Relative)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "background-color")

		"> svg" style {
			color(LandingVars.Muted.value())
			position(Position.Absolute)
			right(1.cssRem)
			top(1.2.cssRem)
			transition(0.2.s, "color", "transform")
		}

		hover(self) style {
			backgroundColor(LandingVars.Pane.value())
			color(LandingVars.Text.value())
		}

		child(hover(self), type("svg")) style {
			color(LandingVars.AccentStrong.value())
			transform { translate(2.px, (-2).px) }
		}
	}

	val label by style {
		alignItems(org.jetbrains.compose.web.css.AlignItems.Center)
		color(LandingVars.Accent.value())
		display(DisplayStyle.Flex)
		fontSize(0.85.cssRem)
		fontWeight(500)
		gap(0.5.cssRem)
	}

	val headline by style {
		fontSize(1.05.cssRem)
		fontWeight(600)
		lineHeight(1.35)
		paddingRight(1.2.cssRem)
	}
}
