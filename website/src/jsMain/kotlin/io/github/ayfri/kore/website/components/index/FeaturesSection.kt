package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.silk.components.icons.lucide.LucideBugOff
import com.varabyte.kobweb.silk.components.icons.lucide.LucideGitBranch
import com.varabyte.kobweb.silk.components.icons.lucide.LucideLibraryBig
import com.varabyte.kobweb.silk.components.icons.lucide.LucideTextCursorInput
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
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
		) { LucideBugOff() },
		Feature(
			"Autocomplete for the whole game",
			"Blocks, items, enchantments, sounds, advancements and every other registry are generated as enums straight from Minecraft's data. The API mirrors vanilla structure, so what you already know still applies.",
		) { LucideTextCursorInput() },
		Feature(
			"Refactor a pack the way you refactor code",
			"Rename a function and every call site follows. Share logic with real functions instead of copy-paste, split a large pack across files, and keep it reviewable in git.",
		) { LucideGitBranch() },
		Feature(
			"Documented feature by feature",
			"Every command, data-driven file and helper has a reference page with a copy-pastable example, plus guides for migrating an existing pack and a cookbook of common patterns.",
		) { LucideLibraryBig() },
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
						classes(FeaturesSectionStyle.featureHeader)
					}) {
						Div({
							classes(FeaturesSectionStyle.featureIcon)
						}) {
							feature.icon()
						}
						H2 {
							Text(feature.title)
						}
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
	val featuresContainer by style {
		marginX(auto)
		maxWidth(85.cssRem)
		padding(2.5.cssRem, 5.vw)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)

		mdMax(self) {
			padding(2.5.cssRem, 5.vw)
		}
	}

	val sectionTitle by style {
		fontSize(2.6.cssRem)
		marginTop(0.px)
		marginBottom(0.5.cssRem)
		textAlign(TextAlign.Left)
	}

	val sectionSubtitle by style {
		color(Color("var(--landing-muted)"))
		fontSize(1.1.cssRem)
		lineHeight(1.6.number)
		marginBottom(1.8.cssRem)
		textAlign(TextAlign.Left)
		maxWidth(42.cssRem)
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

	// Two columns rather than four: the descriptions are two or three lines, so wider cards keep them from towering.
	val grid by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("repeat(2, minmax(0, 1fr))")
		gap(1.2.cssRem)

		mdMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val feature by style {
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.2.cssRem)
		padding(1.5.cssRem, 1.6.cssRem)
		backgroundImage(
			linearGradient(135.deg) {
				add(rgba(21, 28, 38, 0.95), 0.percent)
				add(rgba(15, 20, 27, 0.92), 60.percent)
				add(rgba(8, 182, 214, 0.09), 100.percent)
			}
		)
		boxShadow(0.px, 18.px, 40.px, 0.px, rgba(5, 12, 20, 0.35))
		transition(0.35.s, "transform", "border-color", "box-shadow")
		textAlign(TextAlign.Left)

		hover(self) style {
			transform { translateY((-6).px) }
			borderColor(Color("rgba(8, 182, 214, 0.55)"))
			boxShadow(0.px, 26.px, 65.px, 0.px, rgba(5, 12, 20, 0.5))
		}

		"p" {
			color(Color("var(--landing-muted)"))
			fontSize(1.02.cssRem)
			lineHeight(1.6.number)
			marginTop(0.9.cssRem)
			marginBottom(0.px)
		}

		smMax(self) {
			padding(1.3.cssRem)
		}
	}

	// Icon sits on the title line instead of stacked above it, so a two-line description does not leave the card half empty.
	val featureHeader by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(0.9.cssRem)

		"h2" {
			fontSize(1.35.cssRem)
			lineHeight(1.3.number)
			marginTop(0.px)
			marginBottom(0.px)
		}
	}

	val featureIcon by style {
		width(2.6.cssRem)
		height(2.6.cssRem)
		flexShrink(0)
		borderRadius(0.85.cssRem)
		border(1.px, LineStyle.Solid, Color("rgba(8, 182, 214, 0.3)"))
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		backgroundImage(
			linearGradient(160.deg) {
				add(rgba(8, 182, 214, 0.24), 0.percent)
				add(rgba(8, 182, 214, 0.06), 100.percent)
			}
		)

		"svg" style {
			color(Color("var(--landing-accent-strong)"))
			fontSize(1.35.cssRem)
		}
	}
}
