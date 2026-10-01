package io.github.ayfri.kore.website.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCoffee
import com.varabyte.kobweb.silk.components.icons.lucide.LucideHeart
import io.github.ayfri.kore.website.DISCORD_LINK
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.WEBSITE_GITHUB_LINK
import io.github.ayfri.kore.website.components.common.BrandIcon
import io.github.ayfri.kore.website.utils.*
import kotlin.js.Date
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.AlignSelf
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.A as DomA

private const val COFFEE_LINK = "https://www.buymeacoffee.com/ayfri"
private const val KOBWEB_LINK = "https://kobweb.varabyte.com/"
private const val LICENSE_LINK = "$GITHUB_LINK/blob/master/LICENSE"

private class FooterColumn(val title: String, val links: List<Pair<String, String>>)

private val footerColumns = listOf(
	FooterColumn(
		"Learn", listOf(
			"Getting Started" to "/docs/getting-started",
			"Documentation" to "/docs/home",
			"Features" to "/features",
			"Playground" to "/playground",
			"Updates" to "/updates",
		)
	),
	FooterColumn(
		"Project", listOf(
			"GitHub Repository" to GITHUB_LINK,
			"Releases" to "$GITHUB_LINK/releases",
			"Issues" to "$GITHUB_LINK/issues",
			"Project Template" to "https://github.com/Kore-Minecraft/Kore-Template",
			"Website Code" to WEBSITE_GITHUB_LINK,
		)
	),
	FooterColumn(
		"Community", listOf(
			"Discord" to DISCORD_LINK,
			"Slack #kore" to "https://kotlinlang.slack.com/archives/C066G9BF66A",
			"Contact" to "mailto:pierre.ayfri@gmail.com",
		)
	),
)

@Composable
fun Footer() {
	Style(FooterStyle)

	Footer({ classes(FooterStyle.footer) }) {
		Div({ classes(SiteChromeStyle.container) }) {
			Div({ classes(FooterStyle.top) }) {
				Div({ classes(FooterStyle.brand) }) {
					DomA("/", { attr("aria-label", "Kore home") }) {
						Img("/logo.avif", "Kore Logo") { classes(FooterStyle.logo) }
					}
					P("Type-safe Minecraft datapacks, written in Kotlin.", FooterStyle.tagline)

					Div({ classes(FooterStyle.socials) }) {
						DomA(GITHUB_LINK, {
							classes(SiteChromeStyle.outlineButton, FooterStyle.social)
							attr("aria-label", "GitHub")
							externalTarget(GITHUB_LINK)
						}) {
							BrandIcon("github")
						}
						DomA(DISCORD_LINK, {
							classes(SiteChromeStyle.outlineButton, FooterStyle.social)
							attr("aria-label", "Discord")
							externalTarget(DISCORD_LINK)
						}) {
							BrandIcon("discord")
						}
					}

					DomA(COFFEE_LINK, {
						classes(FooterStyle.coffee)
						externalTarget(COFFEE_LINK)
					}) {
						LucideCoffee()
						Text("Buy me a coffee")
					}
				}

				footerColumns.forEach { column ->
					Div({ classes(FooterStyle.column) }) {
						H3 { Text(column.title) }
						Ul {
							column.links.forEach { (name, link) ->
								Li {
									DomA(link, {
										classes(FooterStyle.columnLink)
										externalTarget(link)
									}) {
										Text(name)
									}
								}
							}
						}
					}
				}
			}

			Div({ classes(FooterStyle.bottom) }) {
				P(attrs = { classes(FooterStyle.meta) }) {
					Text("© ${Date().getFullYear()} ")
					DomA("https://ayfri.com", { title("Hello :)") }) { Text("Ayfri") }
					Text(" · ")
					DomA(LICENSE_LINK, { externalTarget(LICENSE_LINK) }) { Text("GPL-3.0 License") }
					Text(" · ")
					DomA("/legal-notice") { Text("Legal Notice") }
					Text(" · Built with ")
					LucideHeart(Modifier.classNames(FooterStyle.heart))
					Text(" and ")
					DomA(KOBWEB_LINK, { externalTarget(KOBWEB_LINK) }) { Text("Kobweb") }
				}

				P("Not an official Minecraft product. Not approved by or associated with Mojang or Microsoft.", FooterStyle.disclaimer)
			}
		}
	}
}

object FooterStyle : StyleSheet() {
	private val dividerColor = LandingVars.Border.value()

	val footer by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderTop(1.px, LineStyle.Solid, dividerColor)
		width(100.percent)
	}

	val top by style {
		display(DisplayStyle.Grid)
		gap(2.5.cssRem)
		gridTemplateColumns("minmax(0, 1.6fr) repeat(3, minmax(0, 1fr))")
		paddingY(3.5.cssRem)

		mdMax(self) {
			gridTemplateColumns("repeat(3, minmax(0, 1fr))")
			paddingY(2.5.cssRem)
		}

		smMax(self) {
			gap(2.cssRem, 1.5.cssRem)
			gridTemplateColumns("repeat(2, minmax(0, 1fr))")
		}
	}

	val brand by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)

		mdMax(self) {
			gridColumn(1, -1)
		}
	}

	val logo by style {
		display(DisplayStyle.Block)
		width(6.5.cssRem)
	}

	val tagline by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.95.cssRem)
		lineHeight(1.5)
		margin(0.px)
		maxWidth(18.cssRem)
	}

	val socials by style {
		display(DisplayStyle.Flex)
		gap(0.5.cssRem)
	}

	val social by style {
		color(GlobalStyle.altTextColor)
		fontSize(1.05.cssRem)
		height(2.25.cssRem)
		width(2.25.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val coffee by style {
		alignItems(AlignItems.Center)
		alignSelf(AlignSelf.FlexStart)
		backgroundColor(Color("#ffdd00"))
		borderRadius(GlobalStyle.roundingButton)
		color(Color("#0d0c22"))
		display(DisplayStyle.Flex)
		fontSize(0.9.cssRem)
		fontWeight(700)
		gap(0.5.cssRem)
		padding(0.55.cssRem, 0.95.cssRem)
		transition(0.15.s, "background-color", "transform")

		hover(self) style {
			backgroundColor(Color("#ffe433"))
			color(Color("#0d0c22"))
			transform { translateY((-1).px) }
		}
	}

	val column by style {
		"h3" style {
			color(GlobalStyle.textColor)
			fontSize(0.8.cssRem)
			fontWeight(600)
			letterSpacing(0.08.em)
			margin(0.px, 0.px, 1.cssRem)
			textTransform(TextTransform.Uppercase)
		}

		"ul" style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			gap(0.6.cssRem)
			listStyle(ListStyle.None)
			margin(0.px)
			padding(0.px)
		}
	}

	val columnLink by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.92.cssRem)
		transition(0.15.s, "color")

		hover(self) style {
			color(GlobalStyle.textColor)
		}
	}

	val bottom by style {
		borderTop(1.px, LineStyle.Solid, dividerColor)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem, 2.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		paddingY(1.5.cssRem)

		"p" style {
			fontSize(0.82.cssRem)
			margin(0.px)
		}
	}

	val meta by style {
		color(GlobalStyle.altTextColor)

		type("a") style {
			color(GlobalStyle.altTextColor)
			textDecorationColor(Color.transparent)
			textDecorationLine(TextDecorationLine.Underline)
			textUnderlineOffset(TextUnderlineOffset.of(3.px))
			transition(0.15.s, "color", "text-decoration-color")

			hover(self) style {
				color(GlobalStyle.textColor)
				textDecorationColor(GlobalStyle.altTextColor)
			}
		}
	}

	val disclaimer by style {
		color(GlobalStyle.altTextColor.alpha(0.6))
	}

	val heart by style {
		color(Color("#cf3f3f"))
		fill(Color("#cf3f3f"))
		fontSize(0.85.em)
		verticalAlign((-0.1).em)
	}
}
