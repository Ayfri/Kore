package io.github.ayfri.kore.website.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.blur
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
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.BrandIcon
import io.github.ayfri.kore.website.components.common.ButtonStyle
import io.github.ayfri.kore.website.components.layouts.PageTransitions
import io.github.ayfri.kore.website.components.updates.GitHubRelease
import io.github.ayfri.kore.website.gitHubStars
import io.github.ayfri.kore.website.utils.*
import kotlin.js.Date
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.A as DomA

private class NavTab(val name: String, val link: String, val section: String = link)

private val navTabs = listOf(
	NavTab("Features", "/features"),
	NavTab("Docs", "/docs/home", "/docs"),
	NavTab("Playground", "/playground"),
	NavTab("Updates", "/updates"),
)

private const val GET_STARTED_LINK = "/docs/getting-started"

private fun GitHubRelease.isRecent(days: Int) = Date.now() - publishedTime <= days * 24 * 60 * 60 * 1000

@Composable
private fun NavLinks(activeTab: NavTab?, linkClass: String, showIndicator: Boolean = false) = navTabs.forEach { tab ->
	DomA(tab.link, {
		classes(linkClass)
		if (tab == activeTab) {
			classes(HeaderStyle.activeLink)
			attr("aria-current", "page")
		}
	}) {
		Text(tab.name)
		if (showIndicator && tab == activeTab) Span({ classes(HeaderStyle.navIndicator) })
	}
}

@Composable
private fun ReleaseLink(release: GitHubRelease, linkClass: String) = DomA("/updates#release-${release.id}", { classes(linkClass) }) {
	Text("New in ")
	Span({ classes(HeaderStyle.releaseVersion) }) { Text(release.koreVersion ?: release.name) }
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
		Div({ classes(SiteChromeStyle.container, HeaderStyle.bar) }) {
			DomA("/", {
				classes(HeaderStyle.brand)
				attr("aria-label", "Kore home")
			}) {
				Img("/logo.avif", "Kore Logo") { classes(HeaderStyle.logo) }
			}

			Nav({ classes(HeaderStyle.nav) }) {
				NavLinks(activeTab, HeaderStyle.navLink, showIndicator = true)
			}

			Div({ classes(HeaderStyle.actions) }) {
				recentRelease?.let { ReleaseLink(it, HeaderStyle.releaseLink) }

				DomA(DISCORD_LINK, {
					classes(HeaderStyle.iconButton)
					attr("aria-label", "Discord")
					externalTarget(DISCORD_LINK)
				}) {
					BrandIcon("discord")
				}

				DomA(GITHUB_LINK, {
					classes(SiteChromeStyle.outlineButton, HeaderStyle.githubButton)
					externalTarget(GITHUB_LINK)
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

				DomA(GET_STARTED_LINK, { classes(ButtonStyle.primaryContained, HeaderStyle.getStarted) }) {
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
				recentRelease?.let { ReleaseLink(it, HeaderStyle.mobileLink) }

				DomA(GET_STARTED_LINK, { classes(ButtonStyle.primaryContained, HeaderStyle.getStarted, HeaderStyle.getStartedMobile) }) {
					Text("Get started")
				}

				Div({ classes(HeaderStyle.mobileSocials) }) {
					DomA(GITHUB_LINK, {
						classes(SiteChromeStyle.outlineButton, HeaderStyle.mobileSocial)
						externalTarget(GITHUB_LINK)
					}) {
						BrandIcon("github")
						Text("GitHub")
					}
					DomA(DISCORD_LINK, {
						classes(SiteChromeStyle.outlineButton, HeaderStyle.mobileSocial)
						externalTarget(DISCORD_LINK)
					}) {
						BrandIcon("discord")
						Text("Discord")
					}
				}
			}
		}
	}
}

object HeaderStyle : StyleSheet() {
	private val dividerColor = LandingVars.Border.value()
	private val hoverBackground = SiteChromeStyle.hoverBackground

	val header by style {
		backdropFilter(BackdropFilter.list(BackdropFilter.of(saturate(160.percent)), BackdropFilter.of(blur(12.px))))
		backgroundColor(rgba(20, 22, 27, 0.72))
		borderBottom(1.px, LineStyle.Solid, dividerColor)
		position(Position.Sticky)
		top(0.px)
		viewTransitionClass(PageTransitions.CHROME_CLASS)
		viewTransitionName(PageTransitions.SITE_HEADER)
		zIndex(50)
	}

	val bar by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(2.cssRem)
		height(4.5.cssRem)
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
		position(Position.Relative)
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

	/** Glides to the new tab when a navigation changes section. */
	val navIndicator by style {
		backgroundColor(GlobalStyle.logoRightColor)
		borderRadius(1.px)
		bottom(0.1.cssRem)
		height(2.px)
		left(0.75.cssRem)
		position(Position.Absolute)
		right(0.75.cssRem)
		viewTransitionClass(PageTransitions.MORPH_CLASS)
		viewTransitionName(PageTransitions.NAV_INDICATOR)
	}

	val actions by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.5.cssRem)
		marginLeft(auto)
	}

	/** The underline propagates to the version span, so one teal line runs under the whole label on hover. */
	val releaseLink by style {
		borderRight(1.px, LineStyle.Solid, dividerColor)
		color(GlobalStyle.altTextColor)
		fontSize(0.85.cssRem)
		marginRight(0.5.cssRem)
		paddingRight(1.cssRem)
		textDecorationColor(Color.transparent)
		textDecorationLine(TextDecorationLine.Underline)
		textUnderlineOffset(TextUnderlineOffset.of(5.px))
		whiteSpace(WhiteSpace.NoWrap)
		transition(0.15.s, "color", "text-decoration-color")

		hover(self) style {
			color(GlobalStyle.textColor)
			textDecorationColor(GlobalStyle.logoRightColor)
		}

		lgMax(self) {
			display(DisplayStyle.None)
		}
	}

	val releaseVersion by style {
		color(GlobalStyle.textColor)
		fontSize(0.8.cssRem)
		fontWeight(600)
		monoFont()
	}

	val iconButton by style {
		alignItems(AlignItems.Center)
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.textColor)
		display(DisplayStyle.Flex)
		fontSize(1.1.cssRem)
		height(2.25.cssRem)
		justifyContent(JustifyContent.Center)
		width(2.25.cssRem)
		transition(0.15.s, "background-color")

		hover(self) style {
			backgroundColor(hoverBackground)
			color(GlobalStyle.textColor)
		}

		mdMax(self) {
			display(DisplayStyle.None)
		}
	}

	val getStarted by style {
		alignItems(AlignItems.Center)
		borderRadius(GlobalStyle.roundingButton)
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Flex)
		fontSize(0.9.cssRem)
		fontWeight(600)
		height(2.25.cssRem)
		justifyContent(JustifyContent.Center)
		paddingX(0.9.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
		transition(0.15.s, "background-color")

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
		fontSize(0.9.cssRem)
		fontWeight(600)
		height(2.25.cssRem)
		paddingX(0.75.cssRem)

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
		fontVariantNumeric(FontVariantNumeric.TabularNums)
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
		flex(1)
		fontSize(0.95.cssRem)
		fontWeight(600)
		padding(0.6.cssRem)
	}
}
