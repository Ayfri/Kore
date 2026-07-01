package io.github.ayfri.kore.website.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.functions.calc
import com.varabyte.kobweb.compose.css.textDecorationLine
import com.varabyte.kobweb.compose.css.zIndex
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.silk.components.icons.mdi.MdiMenu
import com.varabyte.kobweb.silk.components.icons.mdi.MdiStar
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.updates.GitHubRelease
import io.github.ayfri.kore.website.gitHubStars
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import kotlin.js.Date

val tabs = mapOf(
	"Docs" to "/docs/home",
	"Getting Started" to "/docs/getting-started",
	"Updates" to "/updates",
)

@Composable
fun HeaderButton(name: String, link: String) = Div {
	A(link, name) {
		classes(HeaderStyle.headerLink)
	}
}

private fun latestReleaseAnchor(release: GitHubRelease) = "/updates#release-${release.id}"

private fun GitHubRelease.isRecent(days: Int): Boolean {
	val millisecondsPerDay = 24 * 60 * 60 * 1000
	return Date.now() - publishedDate.getTime() <= days * millisecondsPerDay
}

@Composable
fun Header(latestRelease: GitHubRelease? = null) {
	val recentLatestRelease = latestRelease?.takeIf { it.isRecent(10) }
	var githubStars by remember { mutableStateOf(gitHubStars) }

	LaunchedEffect(Unit) {
		val (owner, repo) = GITHUB_LINK.substringAfter("github.com/").split("/")
		githubStars = fetchGitHubStars(owner, repo)
	}

	Style(HeaderStyle)

	Header({
		classes(HeaderStyle.header)
	}) {
		Div({ classes(HeaderStyle.container) }) {
			Div({
				classes(HeaderStyle.linksListDesktop)
			}) {
				org.jetbrains.compose.web.dom.A("/") {
					Img("/logo.png", "Kore Logo") {
						classes(HeaderStyle.logo)
					}
				}

				tabs.forEach { (name, link) -> HeaderButton(name, link) }

				recentLatestRelease?.let { release ->
					A(latestReleaseAnchor(release), {
						classes(HeaderStyle.releaseBadge)
					}) {
						Text("New ${release.getKoreVersion()} release!")
					}
				}
			}

			Div({
				classes(HeaderStyle.githubLink)
			}) {
				A(GITHUB_LINK, {
					classes(HeaderStyle.githubButton)
					target(ATarget.Blank)
				}) {
					Img(src = "/github-mark-white.svg", alt = "GitHub Logo") {
						classes(HeaderStyle.githubLogo)
					}
					Text("GitHub")

					githubStars?.let { stars ->
						MdiStar(Modifier.color(Color("#e3b341")).fontSize(0.85.cssRem).margin(left = 0.1.cssRem))
						Span({ classes(HeaderStyle.githubStarsCount) }) {
							Text(formatStarsCount(stars))
						}
					}
				}
			}

			Div({
				classes(HeaderStyle.mobileMenu)
			}) {
				val openMenuInputName = "open-menu"

				Label(forId = openMenuInputName, {
					classes(HeaderStyle.mobileMenuLabel)
				}) {
					MdiMenu()
				}

				Input(InputType.Checkbox) {
					id(openMenuInputName)
					hidden()
					classes(HeaderStyle.mobileMenuInput)
				}

				Div({
					classes(HeaderStyle.linksListMobile)
				}) {
					tabs.forEach { (name, link) -> HeaderButton(name, link) }

					recentLatestRelease?.let { release ->
						A(latestReleaseAnchor(release), {
							classes(HeaderStyle.releaseBadge, HeaderStyle.releaseBadgeMobile)
						}) {
							Text("New ${release.getKoreVersion()} release!")
						}
					}
				}

				org.jetbrains.compose.web.dom.A("/") {
					Img("/logo.png", "Kore Logo") {
						classes(HeaderStyle.logo)
					}
				}
			}
		}
	}
}

object HeaderStyle : StyleSheet() {
	val header by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		height(4.5.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		padding(0.5.cssRem, 1.cssRem)

		position(Position.Sticky)
		top(0.px)
		property("backdrop-filter", "saturate(150%) blur(8px)")
		backgroundColor(rgba(24, 26, 31, 0.6))
		property("border-bottom", "1px solid ${GlobalStyle.tertiaryBackgroundColor}")
		zIndex(50)

		"div" style {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
		}
	}

	val container by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(1.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		marginX(auto)
		width(calc { 55.vw + 30.cssRem })

		maxWidth(100.percent)
	}

	val linksListDesktop by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		flexWrap(FlexWrap.Wrap)
		gap(1.5.cssRem)

		mdMax(type("div") + self) {
			display(DisplayStyle.None)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val mobileMenu by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)

		child(self, type("a")) style {
			position(Position.Absolute)
			left(50.percent)
			transform {
				translateX((-45).percent)
			}
			top(1.cssRem)
		}

		mdMin(type("div") + self) {
			display(DisplayStyle.None)
		}
	}

	val mobileMenuLabel by style {
		zIndex(10)

		className("material-icons") style {
			fontSize(2.2.cssRem)
		}

		mdMin(self) {
			display(DisplayStyle.None)
		}
	}

	val mobileMenuInput by style {
		display(DisplayStyle.None)

		adjacent(self + checked, type("div")) style {
			display(DisplayStyle.Flex)
			top(0.cssRem)
		}
	}

	val logo by style {
		marginRight(1.2.cssRem)
		width(6.8.cssRem)

		mdMax(self) {
			marginRight(0.px)
			width(6.2.cssRem)
		}
	}

	val linksListMobile by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		flexDirection(FlexDirection.Column)
		position(Position.Absolute)
		gap(1.cssRem)
		left(0.px)
		paddingTop(5.cssRem)
		paddingBottom(1.cssRem)
		width(100.percent)
		top((-16).cssRem)

		transition(0.35.s, AnimationTimingFunction.EaseInOut, "top")
	}

	val headerLink by style {
		color(GlobalStyle.textColor)
		fontSize(1.3.cssRem)
		fontWeight(700)
		textDecorationLine(TextDecorationLine.Underline)
		textDecorationColor(Color.transparent)
		transition(0.2.s, "text-decoration-color")

		hover(self) style {
			color(GlobalStyle.textColor)
			textDecorationColor(GlobalStyle.linkColorHover)
		}
	}

	val releaseBadge by style {
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.linkColor.alpha(0.3))
		borderRadius(999.px)
		color(Color.white)
		display(DisplayStyle.Flex)
		fontSize(0.9.cssRem)
		fontWeight(700)
		lineHeight(1.2.number)
		padding(0.35.cssRem, 0.7.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "background-color")

		hover(self) style {
			backgroundColor(GlobalStyle.linkColorHover.alpha(0.5))
			color(Color.white)
		}
	}

	val releaseBadgeMobile by style {
		justifyContent(JustifyContent.Center)
		marginX(1.cssRem)
	}

	val githubLink by style {
		mdMax(type("div") + self) {
			display(DisplayStyle.None)
		}
	}

	val githubButton by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(0.5.cssRem)
		justifyContent(JustifyContent.Center)
		fontSize(1.05.cssRem)
		fontWeight(700)
		padding(0.6.cssRem, 0.8.cssRem)
		borderRadius(GlobalStyle.roundingButton)
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		color(GlobalStyle.textColor)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "background")

		hover(self) style {
			backgroundColor(GlobalStyle.secondaryBackgroundColor)
		}
	}

	val githubLogo by style {
		height(1.4.cssRem)
		width(1.4.cssRem)
	}

	val githubStarsCount by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.9.cssRem)
		fontWeight(600)
	}
}
