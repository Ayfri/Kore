package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.mdi.*
import io.github.ayfri.kore.website.utils.lgMax
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*

data class CommunityPlatform(
	val name: String,
	val description: String,
	val link: String,
	val icon: @Composable () -> Unit,
)

@Composable
fun CommunitySection() {
	Style(CommunitySectionStyle)

	val platforms = listOf(
		CommunityPlatform(
			"Discord",
			"Join our active Discord community to chat with other Kore developers, get help, and share your projects.",
			"https://discord.gg/BySjRNQ9Je",
		) { MdiDiscord(style = IconStyle.ROUNDED) },
		CommunityPlatform(
			"GitHub",
			"Contribute to Kore, report issues, or explore the source code on our GitHub repository.",
			"https://github.com/Ayfri/Kore",
		) { MdiDataObject(style = IconStyle.ROUNDED) },
		CommunityPlatform(
			"Docs",
			"Browse guided documentation, API references, and practical examples to ship faster with Kore.",
			"/docs/getting-started",
		) { MdiBook(style = IconStyle.ROUNDED) },
		CommunityPlatform(
			"Examples",
			"Explore real Kotlin datapack snippets and ready-to-adapt examples directly from the project.",
			"https://github.com/Ayfri/Kore/tree/master/examples",
		) { MdiFunctions(style = IconStyle.ROUNDED) },
	)

	Section({
		classes(CommunitySectionStyle.communitySection)
	}) {
		Div({
			classes(CommunitySectionStyle.header)
		}) {
			H2 {
				Text("Join Our Community")
			}

			P({
				classes(CommunitySectionStyle.subtitle)
			}) {
				Text("Connect with other Kore developers and get involved")
			}
		}

		Div({
			classes(CommunitySectionStyle.platformsContainer)
		}) {
			platforms.forEach { platform ->
				A(href = platform.link, attrs = {
					classes(CommunitySectionStyle.platformCard)
				}) {
					Div({
						classes(CommunitySectionStyle.iconContainer)
					}) {
						platform.icon()
					}

					H3 {
						Text(platform.name)
					}

					P {
						Text(platform.description)
					}
				}
			}
		}
	}
}

object CommunitySectionStyle : StyleSheet() {
	val communitySection by style {
		padding(3.cssRem, 5.vw)
		marginX(auto)
		maxWidth(90.cssRem)
		position(Position.Relative)
	}

	val header by style {
		textAlign(TextAlign.Left)
		marginBottom(2.cssRem)

		"h2" style {
			fontSize(2.6.cssRem)
			marginBottom(0.6.cssRem)
		}

		mdMax(self) {
			textAlign(TextAlign.Center)
		}
	}

	val subtitle by style {
		color(Color("var(--landing-muted)"))
		fontSize(1.1.cssRem)
	}

	val platformsContainer by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("repeat(auto-fit, minmax(16rem, 1fr))")
		gap(1.5.cssRem)
		justifyContent(JustifyContent.Center)
		marginX(auto)
		maxWidth(72.cssRem)

		lgMax(self) {
			gap(1.5.cssRem)
		}

		mdMax(self) {
			gridTemplateColumns("1fr")
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val platformCard by style {
		backgroundColor(Color("var(--landing-card)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.2.cssRem)
		color(Color("var(--landing-text)"))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		padding(1.9.cssRem)
		textAlign(TextAlign.Left)
		textDecorationLine(TextDecorationLine.None)
		transition(0.35.s, "transform", "border-color", "box-shadow")
		property("box-shadow", "0 18px 40px rgba(5, 12, 20, 0.35)")

		hover(self) style {
			transform { translateY((-8).px) }
			borderColor(Color("rgba(8, 182, 214, 0.6)"))
			property("box-shadow", "0 26px 65px rgba(5, 12, 20, 0.5)")
		}

		"h3" style {
			fontSize(1.6.cssRem)
			marginBottom(0.4.cssRem)
			marginTop(0.px)
		}

		"p" style {
			color(Color("var(--landing-muted)"))
			fontSize(1.02.cssRem)
			property("line-height", "1.6")
			marginBottom(0.px)
		}

		mdMax(self) {
			textAlign(TextAlign.Center)
			alignItems(AlignItems.Center)
		}
	}

	val iconContainer by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)
		width(3.4.cssRem)
		height(3.4.cssRem)
		borderRadius(1.1.cssRem)
		backgroundColor(Color("rgba(8, 182, 214, 0.14)"))

		"i" {
			color(Color("var(--landing-text)"))
			fontSize(1.6.cssRem)
		}

		className("material-icons-round") style {
			color(Color("var(--landing-text)"))
			fontSize(2.4.cssRem)
		}
	}
}
