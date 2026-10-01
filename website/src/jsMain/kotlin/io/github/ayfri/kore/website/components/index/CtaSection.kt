package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.RadialGradient
import com.varabyte.kobweb.compose.css.functions.radialGradient
import com.varabyte.kobweb.silk.components.icons.lucide.LucideBookOpenText
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFlaskConical
import com.varabyte.kobweb.silk.components.icons.lucide.LucideSquareCode
import io.github.ayfri.kore.website.DISCORD_LINK
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.components.common.*
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.A as DomA

private class CommunityLink(val name: String, val link: String, val icon: @Composable () -> Unit)

private val communityLinks = listOf(
	CommunityLink("Discord", DISCORD_LINK) { BrandIcon("discord") },
	CommunityLink("GitHub", GITHUB_LINK) { BrandIcon("github") },
	CommunityLink("Examples", "https://github.com/Kore-Minecraft/examples") { LucideFlaskConical() },
	CommunityLink("Playground", "/playground") { LucideSquareCode() },
	CommunityLink("Docs", "/docs/home") { LucideBookOpenText() },
)

@Composable
fun CtaSection() {
	Style(CtaSectionStyle)

	Section({ classes(CtaSectionStyle.cta) }) {
		H2 { Text("Build your first datapack in Kotlin") }

		P("The getting started guide takes you from an empty folder to a pack running in your world. Already have a datapack? Port it one piece at a time, the rest keeps working next to it.")

		Div({ classes(CtaSectionStyle.actions) }) { CtaActions() }

		Nav({ classes(CtaSectionStyle.community) }) {
			communityLinks.forEach { link ->
				DomA(link.link, {
					classes(CtaSectionStyle.communityLink)
					externalTarget(link.link)
				}) {
					link.icon()
					Text(link.name)
				}
			}
		}
	}
}

/** The buttons closing the home and Features pages, [playground] adding the playground between the two guides. */
@Composable
fun CtaActions(playground: Boolean = false) {
	LinkButton("Get started", "/docs/getting-started", color = ButtonColor.PRIMARY)
	if (playground) LinkButton("Open the playground", "/playground", variant = ButtonVariant.OUTLINE)
	LinkButton("Migrate an existing pack", "/docs/guides/from-datapacks-to-kore", variant = ButtonVariant.OUTLINE)
}

object CtaSectionStyle : StyleSheet() {
	val cta by style {
		alignItems(AlignItems.Center)
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(1.4.cssRem)
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		marginX(auto)
		marginY(2.cssRem)
		maxWidth(70.cssRem)
		padding(3.5.cssRem, 2.cssRem, 2.cssRem)
		textAlign(TextAlign.Center)
		width(90.percent)
		background(
			Background.list(
				LandingVars.Card.value(),
				Background.of(
					BackgroundImage.of(
						radialGradient(RadialGradient.Shape.Ellipse(), CSSPosition(50.percent, 0.percent)) {
							add(LandingVars.Accent.value().alpha(0.12), 0.percent)
							add(Color.transparent, 60.percent)
						}
					)
				),
			)
		)

		"h2" style {
			fontSize(2.3.cssRem)
			letterSpacing((-0.5).px)
			lineHeight(1.15)
			margin(0.px, 0.px, 1.cssRem)
			textWrap(TextWrap.Balance)
		}

		"p" style {
			color(LandingVars.Muted.value())
			fontSize(1.05.cssRem)
			margin(0.px, 0.px, 1.8.cssRem)
			maxWidth(38.cssRem)
			textWrap(TextWrap.Pretty)
		}

		smMax(self) {
			padding(2.5.cssRem, 1.2.cssRem, 1.5.cssRem)

			"h2" style { fontSize(1.7.cssRem) }
		}
	}

	val actions by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(1.cssRem)
		justifyContent(JustifyContent.Center)
	}

	val community by style {
		borderTop(1.px, LineStyle.Solid, LandingVars.Border.value())
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem, 1.8.cssRem)
		justifyContent(JustifyContent.Center)
		marginTop(2.5.cssRem)
		paddingTop(1.5.cssRem)
		width(100.percent)
	}

	val communityLink by style {
		alignItems(AlignItems.Center)
		color(LandingVars.Muted.value())
		display(DisplayStyle.Flex)
		gap(0.5.cssRem)
		transition(0.2.s, "color")

		hover(self) style {
			color(LandingVars.Text.value())
		}
	}
}
