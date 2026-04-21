package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.components.index.*
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.utils.smMax
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Div

@Page
@Composable
fun HomePage() = PageLayout("Home") {
	Style(HomePageStyle)

	Div({
		classes(HomePageStyle.page)
	}) {
		Div({ classes(HomePageStyle.glow, HomePageStyle.glowTop) })
		Div({ classes(HomePageStyle.glow, HomePageStyle.glowBottom) })

		Div({
			classes(HomePageStyle.content)
		}) {
			// Hero section with code examples
			HeroSection()

			// Quick installation guide
			InstallationSection()

			// Core features section
			FeaturesSection()

			// Masonry grid with code examples
			Masonry(masonryItems)

			// Community platforms section
			CommunitySection()

			// FAQ section
			FaqSection()

			// Call to action section
			CtaSection()
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
		overflow(Overflow.Hidden)
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
		property("color", "var(--landing-text)")

		property(
			"background",
			"radial-gradient(circle at 12% 8%, rgba(8, 182, 214, 0.18) 0%, transparent 40%), " +
				"radial-gradient(circle at 88% 12%, rgba(254, 201, 7, 0.12) 0%, transparent 38%)"
		)

		"h1" style {
			fontFamily("Sora", "Space Grotesk", "Segoe UI", "sans-serif")
			fontWeight(700)
		}

		"h2" style {
			fontFamily("JetBrains Mono", "IBM Plex Mono", "Consolas", "monospace")
			fontWeight(700)
			letterSpacing((-0.5).px)
		}

		"h3" style {
			fontFamily("JetBrains Mono", "IBM Plex Mono", "Consolas", "monospace")
			fontWeight(700)
		}

		"p" style {
			lineHeight(1.7.number)
		}
	}

	val content by style {
		position(Position.Relative)
		zIndex(1)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.75.cssRem)
		height(auto)
		overflow(Overflow.Visible)
	}

	val glow by style {
		position(Position.Absolute)
		borderRadius(999.px)
		property("filter", "blur(120px)")
		opacity(0.55)
		pointerEvents(PointerEvents.None)
		zIndex(0)
	}

	val glowTop by style {
		top((-12).cssRem)
		left((-10).cssRem)
		width(30.cssRem)
		height(30.cssRem)
		property("background", "radial-gradient(circle, rgba(8, 182, 214, 0.65) 0%, rgba(8, 182, 214, 0) 70%)")
	}

	val glowBottom by style {
		bottom((-16).cssRem)
		right((-8).cssRem)
		width(34.cssRem)
		height(34.cssRem)
		property("background", "radial-gradient(circle, rgba(254, 201, 7, 0.55) 0%, rgba(254, 201, 7, 0) 72%)")
	}
}

