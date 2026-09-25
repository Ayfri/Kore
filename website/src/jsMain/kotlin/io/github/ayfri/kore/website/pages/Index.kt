package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.RadialGradient
import com.varabyte.kobweb.compose.css.functions.radialGradient
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.components.common.BrandIconStyle
import io.github.ayfri.kore.website.components.features.FeatureSectionsStyle
import io.github.ayfri.kore.website.components.features.FeatureVisualsStyle
import io.github.ayfri.kore.website.components.index.*
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.components.mc.McUiStyle
import io.github.ayfri.kore.website.utils.initMCFunctionHighlighting
import io.github.ayfri.kore.website.utils.smMax
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Page
@Composable
fun HomePage() {
	Style(HomePageStyle)
	Style(BrandIconStyle)
	Style(FeatureSectionsStyle)
	Style(FeatureVisualsStyle)
	Style(McUiStyle)

	LaunchedEffect(Unit) {
		initMCFunctionHighlighting()
	}

	PageLayout("Minecraft Datapack Generator") {
		Div({ classes(HomePageStyle.page) }) {
			Div({ classes(HomePageStyle.content) }) {
				HeroSection()
				IntroSection()
				ExploreSection()
				GetStartedSection()
				FaqSection()
				CtaSection()
			}
		}
	}
}

object HomePageStyle : StyleSheet() {
	init {
		smMax(child(className("code-toolbar"), type("pre"))) {
			fontSize(0.82.cssRem)
		}
	}

	val page by style {
		position(Position.Relative)
		// `clip` rather than `hidden`, which would make this the scroll container and break every sticky element inside.
		property("overflow", "clip")
		paddingBottom(2.5.cssRem)

		property("--landing-accent", "#08b6d6")
		property("--landing-accent-strong", "#1fd2f2")
		property("--landing-gold", "#fec907")
		property("--landing-surface", "#0f141b")
		property("--landing-surface-2", "#141c26")
		property("--landing-card", "#151c26")
		property("--landing-border", "rgba(151, 176, 202, 0.18)")
		property("--landing-muted", "#a6b4bd")
		property("--landing-text", "#f7f9fc")
		property("--landing-radius", "20px")

		fontFamily("IBM Plex Sans", "Inter", "Segoe UI", "sans-serif")
		color(Color("var(--landing-text)"))

		// `Background.list` takes its layers bottom-to-top, the opposite of the CSS order.
		background(
			Background.list(
				Background.of(
					BackgroundImage.of(
						radialGradient(RadialGradient.Shape.Circle(), CSSPosition(88.percent, 12.percent)) {
							add(rgba(254, 201, 7, 0.08), 0.percent)
							add(Color.transparent, 38.percent)
						}
					)
				),
				Background.of(
					BackgroundImage.of(
						radialGradient(RadialGradient.Shape.Circle(), CSSPosition(12.percent, 8.percent)) {
							add(rgba(8, 182, 214, 0.12), 0.percent)
							add(Color.transparent, 40.percent)
						}
					)
				),
			)
		)

		"h1, h2, h3" style {
			fontFamily("Sora", "Segoe UI", "sans-serif")
			fontWeight(600)
		}

		"p" style {
			lineHeight(1.7.number)
		}
	}

	val content by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
	}
}
