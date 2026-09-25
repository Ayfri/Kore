package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.lucide.LucideArrowUpRight
import io.github.ayfri.kore.website.components.features.FeatureSectionsStyle
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

	Section({ classes(FeatureSectionsStyle.section) }) {
		SectionHeader(
			"Covers the whole game, not just commands",
			"From a single /give to a custom dimension, everything a datapack can hold has a typed API. Pick an area to see it in action.",
		)

		Div({ classes(FeatureSectionsStyle.stats, ExploreSectionStyle.stats) }) {
			featureStats.forEach { stat ->
				Div({ classes(FeatureSectionsStyle.stat) }) {
					Span(stat.value, FeatureSectionsStyle.statValue)
					Span(stat.label, FeatureSectionsStyle.statLabel)
				}
			}
		}

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
		marginX(auto)
	}

	/** Tiles share 1 px borders like the stats above, drawn by the grid background showing through the gaps. */
	val grid by style {
		backgroundColor(Color("var(--landing-border)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
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
		backgroundColor(Color("var(--landing-card)"))
		color(Color("var(--landing-text)"))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.7.cssRem)
		minHeight(8.cssRem)
		padding(1.3.cssRem)
		position(Position.Relative)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "background-color")

		"> svg" style {
			color(Color("var(--landing-muted)"))
			position(Position.Absolute)
			right(1.cssRem)
			top(1.2.cssRem)
			transition(0.2.s, "color", "transform")
		}

		hover(self) style {
			backgroundColor(Color("var(--landing-surface-2)"))
			color(Color("var(--landing-text)"))
		}

		child(hover(self), type("svg")) style {
			color(Color("var(--landing-accent-strong)"))
			transform { translate(2.px, (-2).px) }
		}
	}

	val label by style {
		alignItems(org.jetbrains.compose.web.css.AlignItems.Center)
		color(Color("var(--landing-accent)"))
		display(DisplayStyle.Flex)
		fontSize(0.85.cssRem)
		fontWeight(500)
		gap(0.5.cssRem)
	}

	val headline by style {
		fontSize(1.05.cssRem)
		fontWeight(600)
		lineHeight(1.35.number)
		paddingRight(1.2.cssRem)
	}
}
