package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.RadialGradient
import com.varabyte.kobweb.compose.css.functions.radialGradient
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.SegmentedStyle
import io.github.ayfri.kore.website.components.common.StatGridStyle
import io.github.ayfri.kore.website.components.index.*
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.components.mc.McUiStyle
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.lineHeight
import io.github.ayfri.kore.website.utils.smMax
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Page
@Composable
fun HomePage() {
	Style(HomePageStyle)
	Style(HomeSectionStyle)
	Style(McUiStyle)
	Style(SegmentedStyle)
	Style(StatGridStyle)

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
		color(LandingVars.Text.value())
		// `clip` rather than `hidden`, which would make this the scroll container and break every sticky element inside.
		overflow(Overflow.Clip)
		paddingBottom(2.5.cssRem)
		position(Position.Relative)

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
							add(LandingVars.Accent.value().alpha(0.12), 0.percent)
							add(Color.transparent, 40.percent)
						}
					)
				),
			)
		)

		":is(h1, h2, h3)" style {
			fontWeight(600)
		}

		"p" style {
			lineHeight(1.7)
		}
	}

	val content by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
	}
}
