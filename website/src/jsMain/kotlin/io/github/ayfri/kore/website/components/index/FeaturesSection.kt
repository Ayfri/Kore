package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.borderColor
import com.varabyte.kobweb.compose.css.borderTop
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.silk.components.icons.mdi.*
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.selectors.Nth
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text

data class Feature(
	val title: String,
	val description: String,
	val icon: @Composable () -> Unit,
)

@Composable
fun FeaturesSection() {
	Style(FeaturesSectionStyle)

	val features = listOf(
		Feature(
			"Modern Architecture",
			"Write datapacks for recent Minecraft versions with Kotlin. Leverage powerful features like extension functions and sealed classes to build robust and maintainable code.",
		) { MdiArchitecture(style = IconStyle.ROUNDED) },
		Feature(
			"Intuitive API",
			"The API follows Minecraft's internal logic. Abstractions over commands and JSON formats minimize complexity, letting you focus on features rather than boilerplate.",
		) { MdiDataObject(style = IconStyle.ROUNDED) },
		Feature(
			"Collaborative & Open",
			"Kore is fully open-source and thrives on community. Whether solo or in a team, Kore provides the tools for any project size, backed by a growing ecosystem.",
		) { MdiGroupAdd(style = IconStyle.ROUNDED) },
		Feature(
			"Rich Documentation",
			"Move quickly with practical guides, examples, and reference pages that keep the onboarding smooth for both new and experienced datapack developers.",
		) { MdiBook(style = IconStyle.ROUNDED) },
	)

	Div({
		classes(FeaturesSectionStyle.featuresContainer)
	}) {
		H2({
			classes(FeaturesSectionStyle.sectionTitle)
		}) {
			Text("Why choose Kore?")
		}

		P(
			"Kore is built by datapack developers, for datapack developers. It focuses on developer experience, performance, and reliability.",
			FeaturesSectionStyle.sectionSubtitle
		)

		Div({
			classes(FeaturesSectionStyle.grid)
		}) {
			features.forEach { feature ->
				Div({
					classes(FeaturesSectionStyle.feature)
				}) {
					Div({
						classes(FeaturesSectionStyle.featureIcon)
					}) {
						feature.icon()
					}
					H2 {
						Text(feature.title)
					}
					P(feature.description)
				}
			}
		}
	}
}

object FeaturesSectionStyle : StyleSheet() {
	@OptIn(ExperimentalComposeWebApi::class)
	val fadeInUp by keyframes {
		from {
			opacity(0)
			transform { translateY(18.px) }
		}
		to {
			opacity(1)
			transform { translateY(0.px) }
		}
	}

	val featuresContainer by style {
		marginX(auto)
		maxWidth(85.cssRem)
		padding(2.5.cssRem, 5.vw)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.2.cssRem)

		mdMax(self) {
			padding(2.5.cssRem, 5.vw)
		}
	}

	val sectionTitle by style {
		fontSize(2.6.cssRem)
		marginBottom(0.5.cssRem)
		textAlign(TextAlign.Left)
	}

	val sectionSubtitle by style {
		color(Color("var(--landing-muted)"))
		fontSize(1.1.cssRem)
		marginBottom(1.5.cssRem)
		textAlign(TextAlign.Left)
		maxWidth(40.cssRem)
	}

	val grid by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("repeat(auto-fit, minmax(15.5rem, 1fr))")
		gap(1.35.cssRem)

		lgMax(self) {
			gridTemplateColumns("repeat(auto-fit, minmax(15rem, 1fr))")
		}

		mdMax(self) {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Stretch)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val feature by style {
		backgroundColor(Color("var(--landing-card)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.2.cssRem)
		borderTop(2.px, LineStyle.Solid, Color("var(--landing-accent)"))
		padding(1.8.cssRem)

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.FlexStart)
		gap(1.cssRem)
		textAlign(TextAlign.Left)
		transition(0.35.s, "transform", "border-color", "box-shadow")
		property("box-shadow", "0 18px 40px rgba(5, 12, 20, 0.3)")
		animation(fadeInUp) {
			duration(0.6.s)
			timingFunction(AnimationTimingFunction.EaseOut)
			fillMode(AnimationFillMode.Both)
		}

		hover(self) style {
			transform { translateY((-6).px) }
			borderColor(Color("rgba(8, 182, 214, 0.5)"))
			property("box-shadow", "0 26px 60px rgba(5, 12, 20, 0.45)")
		}

		self + nthOfType(Nth.Functional(b = 1)) style {
			property("animation-delay", "0.05s")
		}

		self + nthOfType(Nth.Functional(b = 2)) style {
			property("animation-delay", "0.12s")
			borderTop(2.px, LineStyle.Solid, Color("var(--landing-gold)"))
		}

		self + nthOfType(Nth.Functional(b = 3)) style {
			property("animation-delay", "0.18s")
			borderTop(2.px, LineStyle.Solid, Color("var(--landing-accent-strong)"))
		}

		"h2" {
			fontSize(1.6.cssRem)
			marginTop(0.px)
			marginBottom(0.px)
		}

		"p" {
			color(Color("var(--landing-muted)"))
			fontSize(1.05.cssRem)
			property("line-height", "1.6")
			marginBottom(0.px)
		}
	}

	val featureIcon by style {
		width(3.1.cssRem)
		height(3.1.cssRem)
		borderRadius(0.9.cssRem)
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		backgroundColor(Color("rgba(8, 182, 214, 0.15)"))

		className("material-icons-round") style {
			fontSize(2.2.cssRem)
			color(Color("var(--landing-accent)"))
		}
	}
}
