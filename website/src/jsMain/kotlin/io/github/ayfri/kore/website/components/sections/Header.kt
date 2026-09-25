package io.github.ayfri.kore.website.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.calc
import com.varabyte.kobweb.compose.css.functions.saturate
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.lucide.LucideMenu
import com.varabyte.kobweb.silk.components.icons.lucide.LucideStar
import com.varabyte.kobweb.silk.components.icons.lucide.LucideX
import io.github.ayfri.kore.website.DISCORD_LINK
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.BrandIcon
import io.github.ayfri.kore.website.components.updates.GitHubRelease
import io.github.ayfri.kore.website.gitHubStars
import io.github.ayfri.kore.website.utils.*
import kotlin.js.Date
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.AlignSelf
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.A as DomA

private class NavTab(val name: String, val link: String, val section: String = link)

private val navTabs = listOf(
	NavTab("Features", "/features"),
	NavTab("Docs", "/docs/home", "/docs"),
	NavTab("Updates", "/updates"),
)

private const val GET_STARTED_LINK = "/docs/getting-started"

private fun GitHubRelease.isRecent(days: Int) = Date.now() - publishedTime <= days * 24 * 60 * 60 * 1000

@Composable
private fun NavLinks(activeTab: NavTab?, linkClass: String) = navTabs.forEach { tab ->
	DomA(tab.link, {
		classes(linkClass)
		if (tab == activeTab) {
			classes(HeaderStyle.activeLink)
			attr("aria-current", "page")
		}
	}) {
		Text(tab.name)
	}
}

@Composable
private fun ReleaseBadge(release: GitHubRelease, vararg extraClasses: String) = DomA("/updates#release-${release.id}", {
	classes(HeaderStyle.releaseBadge, *extraClasses)
}) {
	Span({ classes(HeaderStyle.releaseDot) })
	Text("${release.koreVersion} is out")
}

@Composable
fun Header(latestRelease: GitHubRelease? = null) {
	val recentRelease = latestRelease?.takeIf { it.isRecent(10) }
	val path = rememberPageContext().route.path
	val activeTab = navTabs.firstOrNull { path.startsWith(it.section) }
	var githubStars by remember { mutableStateOf(gitHubStars) }
	var menuOpen by remember { mutableStateOf(false) }

	LaunchedEffect(Unit) {
		val (owner, repo) = GITHUB_LINK.substringAfter("github.com/").split("/")
		githubStars = fetchGitHubStars(owner, repo)
	}

	Style(HeaderStyle)

	Header({ classes(HeaderStyle.header) }) {
		Div({ classes(HeaderStyle.bar) }) {
			DomA("/", {
				classes(HeaderStyle.brand)
				attr("aria-label", "Kore home")
			}) {
				Img("/logo.avif", "Kore Logo") { classes(HeaderStyle.logo) }
			}

			Nav({ classes(HeaderStyle.nav) }) {
				NavLinks(activeTab, HeaderStyle.navLink)
			}

			Div({ classes(HeaderStyle.actions) }) {
				recentRelease?.let { ReleaseBadge(it) }

				DomA(DISCORD_LINK, {
					classes(HeaderStyle.iconButton)
					attr("aria-label", "Discord")
					target(ATarget.Blank)
				}) {
					Img("/discord-mark.svg", "") { classes(HeaderStyle.discordMark) }
				}

				DomA(GITHUB_LINK, {
					classes(HeaderStyle.githubButton)
					target(ATarget.Blank)
				}) {
					BrandIcon("github")
					Text("GitHub")
					githubStars?.let { stars ->
						Span({ classes(HeaderStyle.githubStars) }) {
							LucideStar(Modifier.classNames(HeaderStyle.githubStar))
							Text(formatStarsCount(stars))
						}
					}
				}

				DomA(GET_STARTED_LINK, { classes(HeaderStyle.getStarted) }) {
					Text("Get started")
				}

				Button({
					classes(HeaderStyle.menuToggle)
					attr("aria-expanded", menuOpen.toString())
					attr("aria-label", if (menuOpen) "Close menu" else "Open menu")
					onClick { menuOpen = !menuOpen }
				}) {
					if (menuOpen) LucideX() else LucideMenu()
				}
			}
		}

		if (menuOpen) {
			Nav({ classes(HeaderStyle.mobilePanel) }) {
				NavLinks(activeTab, HeaderStyle.mobileLink)
				recentRelease?.let { ReleaseBadge(it, HeaderStyle.releaseBadgeMobile) }

				DomA(GET_STARTED_LINK, { classes(HeaderStyle.getStarted, HeaderStyle.getStartedMobile) }) {
					Text("Get started")
				}

				Div({ classes(HeaderStyle.mobileSocials) }) {
					DomA(GITHUB_LINK, {
						classes(HeaderStyle.mobileSocial)
						target(ATarget.Blank)
					}) {
						BrandIcon("github")
						Text("GitHub")
					}
					DomA(DISCORD_LINK, {
						classes(HeaderStyle.mobileSocial)
						target(ATarget.Blank)
					}) {
						Img("/discord-mark.svg", "") { classes(HeaderStyle.discordMark) }
						Text("Discord")
					}
				}
			}
		}
	}
}

object HeaderStyle : StyleSheet() {
	private val dividerColor = rgba(151, 176, 202, 0.14)
	private val hoverBackground = rgba(255, 255, 255, 0.06)

	val header by style {
		backdropFilter(BackdropFilter.list(BackdropFilter.of(saturate(160.percent)), BackdropFilter.of(blur(12.px))))
		backgroundColor(rgba(20, 22, 27, 0.72))
		borderBottom(1.px, LineStyle.Solid, dividerColor)
		position(Position.Sticky)
		top(0.px)
		zIndex(50)
	}

	val bar by style {
		alignItems(AlignItems.Center)
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Flex)
		gap(2.cssRem)
		height(4.5.cssRem)
		marginX(auto)
		maxWidth(100.percent)
		paddingX(1.25.cssRem)
		width(calc { 55.vw + 30.cssRem })
	}

	val brand by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexShrink(0)
	}

	val logo by style {
		display(DisplayStyle.Block)
		width(6.cssRem)
	}

	val nav by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.25.cssRem)

		mdMax(self) {
			display(DisplayStyle.None)
		}
	}

	val navLink by style {
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.altTextColor)
		fontSize(0.95.cssRem)
		fontWeight(500)
		padding(0.45.cssRem, 0.75.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
		transition(0.15.s, "color", "background-color")

		hover(self) style {
			backgroundColor(hoverBackground)
			color(GlobalStyle.textColor)
		}
	}

	val activeLink by style {
		color(GlobalStyle.textColor)
		fontWeight(600)

		hover(self) style {
			color(GlobalStyle.textColor)
		}
	}

	val actions by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.5.cssRem)
		property("margin-left", "auto")
	}

	val releaseBadge by style {
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.linkColor.alpha(0.1))
		border(1.px, LineStyle.Solid, GlobalStyle.linkColor.alpha(0.35))
		borderRadius(999.px)
		color(GlobalStyle.linkColorHover)
		display(DisplayStyle.Flex)
		fontSize(0.8.cssRem)
		fontWeight(600)
		gap(0.45.cssRem)
		marginRight(0.5.cssRem)
		padding(0.25.cssRem, 0.7.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
		transition(0.15.s, "background-color", "border-color")

		hover(self) style {
			backgroundColor(GlobalStyle.linkColor.alpha(0.2))
			borderColor(GlobalStyle.linkColorHover.alpha(0.6))
			color(GlobalStyle.linkColorHover)
		}

		lgMax(self) {
			display(DisplayStyle.None)
		}
	}

	val releaseBadgeMobile by style {
		alignSelf(AlignSelf.FlexStart)
		display(DisplayStyle.Flex)
		margin(0.5.cssRem, 0.75.cssRem)

		lgMax(self) {
			display(DisplayStyle.Flex)
		}
	}

	val releaseDot by style {
		backgroundColor(GlobalStyle.linkColorHover)
		borderRadius(50.percent)
		height(0.4.cssRem)
		width(0.4.cssRem)
		property("box-shadow", "0 0 0 3px ${GlobalStyle.linkColorHover.alpha(0.2)}")
	}

	val iconButton by style {
		alignItems(AlignItems.Center)
		borderRadius(GlobalStyle.roundingButton)
		display(DisplayStyle.Flex)
		height(2.25.cssRem)
		justifyContent(JustifyContent.Center)
		width(2.25.cssRem)
		transition(0.15.s, "background-color")

		hover(self) style {
			backgroundColor(hoverBackground)
		}

		mdMax(self) {
			display(DisplayStyle.None)
		}
	}

	val discordMark by style {
		height(1.1.cssRem)
		opacity(0.85)
		width(1.1.cssRem)
	}

	val getStarted by style {
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.buttonBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		boxSizing(BoxSizing.BorderBox)
		color(GlobalStyle.textColor)
		display(DisplayStyle.Flex)
		fontSize(0.9.cssRem)
		fontWeight(600)
		height(2.25.cssRem)
		justifyContent(JustifyContent.Center)
		paddingX(0.9.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
		transition(0.15.s, "background-color")

		hover(self) style {
			backgroundColor(GlobalStyle.buttonBackgroundColorHover)
			color(GlobalStyle.textColor)
		}

		mdMax(self) {
			display(DisplayStyle.None)
		}
	}

	val getStartedMobile by style {
		fontSize(1.cssRem)
		height(2.75.cssRem)
		marginTop(0.5.cssRem)

		mdMax(self) {
			display(DisplayStyle.Flex)
		}
	}

	val githubButton by style {
		alignItems(AlignItems.Center)
		border(1.px, LineStyle.Solid, dividerColor)
		borderRadius(GlobalStyle.roundingButton)
		boxSizing(BoxSizing.BorderBox)
		color(GlobalStyle.textColor)
		display(DisplayStyle.Flex)
		fontSize(0.9.cssRem)
		fontWeight(600)
		gap(0.5.cssRem)
		height(2.25.cssRem)
		paddingX(0.75.cssRem)
		transition(0.15.s, "background-color", "border-color")

		hover(self) style {
			backgroundColor(hoverBackground)
			borderColor(rgba(151, 176, 202, 0.3))
			color(GlobalStyle.textColor)
		}

		mdMax(self) {
			display(DisplayStyle.None)
		}
	}

	val githubStars by style {
		alignItems(AlignItems.Center)
		borderLeft(1.px, LineStyle.Solid, dividerColor)
		color(GlobalStyle.altTextColor)
		display(DisplayStyle.Flex)
		gap(0.3.cssRem)
		marginLeft(0.2.cssRem)
		paddingLeft(0.6.cssRem)
		property("font-variant-numeric", "tabular-nums")
	}

	val githubStar by style {
		color(Color("#e3b341"))
		fill(Color("#e3b341"))
		fontSize(0.85.cssRem)
	}

	val menuToggle by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(0.px)
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.textColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.None)
		fontSize(1.4.cssRem)
		height(2.5.cssRem)
		justifyContent(JustifyContent.Center)
		width(2.5.cssRem)

		hover(self) style {
			backgroundColor(hoverBackground)
		}

		mdMax(self) {
			display(DisplayStyle.Flex)
		}
	}

	val mobilePanel by style {
		borderTop(1.px, LineStyle.Solid, dividerColor)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.15.cssRem)
		padding(0.75.cssRem, 0.75.cssRem, 1.cssRem)

		mdMin(self) {
			display(DisplayStyle.None)
		}
	}

	val mobileLink by style {
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.altTextColor)
		fontSize(1.05.cssRem)
		fontWeight(500)
		padding(0.7.cssRem, 0.75.cssRem)

		hover(self) style {
			backgroundColor(hoverBackground)
			color(GlobalStyle.textColor)
		}
	}

	val mobileSocials by style {
		borderTop(1.px, LineStyle.Solid, dividerColor)
		display(DisplayStyle.Flex)
		gap(0.5.cssRem)
		marginTop(0.5.cssRem)
		paddingTop(0.75.cssRem)
	}

	val mobileSocial by style {
		alignItems(AlignItems.Center)
		border(1.px, LineStyle.Solid, dividerColor)
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.textColor)
		display(DisplayStyle.Flex)
		flex(1)
		fontSize(0.95.cssRem)
		fontWeight(600)
		gap(0.5.cssRem)
		justifyContent(JustifyContent.Center)
		padding(0.6.cssRem)

		hover(self) style {
			backgroundColor(hoverBackground)
			color(GlobalStyle.textColor)
		}
	}
}
