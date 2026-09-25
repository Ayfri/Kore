package io.github.ayfri.kore.website.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.calc
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCoffee
import com.varabyte.kobweb.silk.components.icons.lucide.LucideHeart
import io.github.ayfri.kore.website.DISCORD_LINK
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.WEBSITE_GITHUB_LINK
import io.github.ayfri.kore.website.components.common.BrandIcon
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

private class FooterColumn(val title: String, val links: List<Pair<String, String>>)

private val footerColumns = listOf(
	FooterColumn(
		"Learn", listOf(
			"Getting Started" to "/docs/getting-started",
			"Documentation" to "/docs/home",
			"Features" to "/features",
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
		Div({ classes(FooterStyle.container) }) {
			Div({ classes(FooterStyle.top) }) {
				Div({ classes(FooterStyle.brand) }) {
					DomA("/", { attr("aria-label", "Kore home") }) {
						Img("/logo.avif", "Kore Logo") { classes(FooterStyle.logo) }
					}
					P("Type-safe Minecraft datapacks, written in Kotlin.", FooterStyle.tagline)

					Div({ classes(FooterStyle.socials) }) {
						DomA(GITHUB_LINK, {
							classes(FooterStyle.social)
							attr("aria-label", "GitHub")
							target(ATarget.Blank)
						}) {
							BrandIcon("github")
						}
						DomA(DISCORD_LINK, {
							classes(FooterStyle.social)
							attr("aria-label", "Discord")
							target(ATarget.Blank)
						}) {
							Img("/discord-mark.svg", "") { classes(FooterStyle.discordMark) }
						}
					}

					DomA("https://www.buymeacoffee.com/ayfri", {
						classes(FooterStyle.coffee)
						target(ATarget.Blank)
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
										if (!link.startsWith("/")) target(ATarget.Blank)
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
					DomA("$GITHUB_LINK/blob/master/LICENSE", { target(ATarget.Blank) }) { Text("GPL-3.0 License") }
					Text(" · ")
					DomA("/legal-notice") { Text("Legal Notice") }
					Text(" · Built with ")
					LucideHeart(Modifier.classNames(FooterStyle.heart))
					Text(" and ")
					DomA("https://kobweb.varabyte.com/", { target(ATarget.Blank) }) { Text("Kobweb") }
				}

				P("Not an official Minecraft product. Not approved by or associated with Mojang or Microsoft.", FooterStyle.disclaimer)
			}
		}
	}
}

object FooterStyle : StyleSheet() {
	private val dividerColor =rgba(151, 176, 202, 0.14)

	val footer by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderTop(1.px, LineStyle.Solid, dividerColor)
		width(100.percent)
	}

	val container by style {
		boxSizing(BoxSizing.BorderBox)
		marginX(auto)
		maxWidth(100.percent)
		paddingX(1.25.cssRem)
		width(calc { 55.vw + 30.cssRem })
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
			property("grid-column", "1 / -1")
		}
	}

	val logo by style {
		display(DisplayStyle.Block)
		width(6.5.cssRem)
	}

	val tagline by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.95.cssRem)
		lineHeight(1.5.number)
		margin(0.px)
		maxWidth(18.cssRem)
	}

	val socials by style {
		display(DisplayStyle.Flex)
		gap(0.5.cssRem)
	}

	val social by style {
		alignItems(AlignItems.Center)
		border(1.px, LineStyle.Solid, dividerColor)
		borderRadius(GlobalStyle.roundingButton)
		boxSizing(BoxSizing.BorderBox)
		color(GlobalStyle.altTextColor)
		display(DisplayStyle.Flex)
		fontSize(1.05.cssRem)
		height(2.25.cssRem)
		justifyContent(JustifyContent.Center)
		width(2.25.cssRem)
		transition(0.15.s, "background-color", "border-color", "color")

		hover(self) style {
			backgroundColor(rgba(255, 255, 255, 0.06))
			borderColor(rgba(151, 176, 202, 0.3))
			color(GlobalStyle.textColor)
		}
	}

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
			property("transform", "translateY(-1px)")
		}
	}

	val discordMark by style {
		height(1.05.cssRem)
		opacity(0.85)
		width(1.05.cssRem)
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
			property("text-underline-offset", "3px")
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
		property("vertical-align", "-0.1em")
	}
}
