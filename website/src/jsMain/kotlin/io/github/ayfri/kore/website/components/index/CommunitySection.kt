package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle
import com.varabyte.kobweb.silk.components.icons.mdi.MdiDiscord
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.paddingY
import io.github.ayfri.kore.website.utils.transition
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
		) {
			Img(src = "/github-mark-white.svg", alt = "GitHub Logo")
		},
		CommunityPlatform(
			"Slack",
			"Connect with us on the Kotlin Slack in the #kore channel for more technical discussions.",
			"https://kotlinlang.slack.com/archives/C066G9BF66A",
		) {
			Img(src = "/slack-logo.svg", alt = "Slack Logo")
		},
	)

	Section({
		classes(CommunitySectionStyle.communitySection)
	}) {
		H2 {
			Text("Join Our Community")
		}

		P({
			classes(CommunitySectionStyle.subtitle)
		}) {
			Text("Connect with other Kore developers and get involved")
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
		paddingY(6.cssRem)
		textAlign(TextAlign.Center)

		"h2" style {
			fontSize(3.cssRem)
			marginBottom(1.cssRem)
		}

		mdMax(type("h2")) {
			fontSize(2.5.cssRem)
		}
	}

	val subtitle by style {
		color(GlobalStyle.altTextColor)
		fontSize(1.2.cssRem)
		marginBottom(4.cssRem)
	}

	val platformsContainer by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(2.cssRem)
		justifyContent(JustifyContent.Center)
		marginX(auto)
		maxWidth(90.percent)

		mdMax(self) {
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
		}
	}

	val platformCard by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderRadius(GlobalStyle.roundingSection)
		color(GlobalStyle.textColor)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		padding(2.cssRem)
		textAlign(TextAlign.Center)
		textDecorationLine(TextDecorationLine.None)
		transition(0.3.s, "translate", "background-color")
		width(30.percent)

		hover(self) style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
			translateY((-5).px)
		}

		"h3" style {
			fontSize(1.5.cssRem)
			marginBottom(1.cssRem)
			marginTop(1.cssRem)
		}

		"p" style {
			color(GlobalStyle.altTextColor)
			fontSize(1.cssRem)
			lineHeight(1.6.number)
		}

		mdMax(self) {
			width(90.percent)
		}
	}

	val iconContainer by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)

		className("material-icons-round") style {
			color("white")
			fontSize(3.cssRem)
		}

		"img" {
			height(3.cssRem)
			width(3.cssRem)
		}
	}
}
