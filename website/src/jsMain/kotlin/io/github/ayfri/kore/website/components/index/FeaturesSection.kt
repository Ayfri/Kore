package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.borderColor
import com.varabyte.kobweb.compose.css.borderTop
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.css.textDecorationLine
import com.varabyte.kobweb.silk.components.icons.mdi.*
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.selectors.Nth
import org.jetbrains.compose.web.dom.*

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
			"Errors at build time, not on /reload",
			"A misspelled item, a wrong selector argument or an invalid loot table field stops the compiler. You find out in your editor instead of after loading the world and reading the log.",
		) { MdiArchitecture(style = IconStyle.ROUNDED) },
		Feature(
			"Autocomplete for the whole game",
			"Blocks, items, enchantments, sounds, advancements and every other registry are generated as enums straight from Minecraft's data. The API mirrors vanilla structure, so what you already know still applies.",
		) { MdiDataObject(style = IconStyle.ROUNDED) },
		Feature(
			"Refactor a pack the way you refactor code",
			"Rename a function and every call site follows. Share logic with real functions instead of copy-paste, split a large pack across files, and keep it reviewable in git.",
		) { MdiGroupAdd(style = IconStyle.ROUNDED) },
		Feature(
			"Documented feature by feature",
			"Every command, data-driven file and helper has a reference page with a copy-pastable example, plus guides for migrating an existing pack and a cookbook of common patterns.",
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
			"Kore is built by datapack developers, for datapack developers. Same output as a hand-written pack, without the stringly-typed guesswork.",
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

		P({
			classes(FeaturesSectionStyle.sectionFootnote)
		}) {
			A(href = "/docs/guides/why-kore", content = "Read the full comparison")
			Text(" with hand-written datapacks, Sandstone and beet, including when Kore is the wrong tool.")
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

	val sectionFootnote by style {
		color(Color("var(--landing-muted)"))
		fontSize(1.cssRem)
		marginTop(1.8.cssRem)

		"a" style {
			color(Color("var(--landing-accent)"))
			textDecorationLine(TextDecorationLine.None)
		}

		hover(child(self, type("a"))) style {
			textDecorationLine(TextDecorationLine.Underline)
		}
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
