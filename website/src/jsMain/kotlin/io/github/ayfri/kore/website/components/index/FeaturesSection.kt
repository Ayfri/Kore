package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle
import com.varabyte.kobweb.silk.components.icons.mdi.MdiArchitecture
import com.varabyte.kobweb.silk.components.icons.mdi.MdiDataObject
import com.varabyte.kobweb.silk.components.icons.mdi.MdiGroupAdd
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text

data class Feature(
	val title: String,
	val description: String,
	val icon: @Composable () -> Unit,
)

@Composable
fun FeaturesSection() {
	Style(FeaturesSectionStyle)

	val features = listOf(
		Feature(
			"Modern Architecture",
			"Write datapacks for recent Minecraft versions with Kotlin. Leverage powerful features like extension functions and sealed classes to build robust and maintainable code.",
		) { MdiArchitecture(style = IconStyle.ROUNDED) },
		Feature(
			"Intuitive API",
			"The API follows Minecraft's internal logic. Abstractions over commands and JSON formats minimize complexity, letting you focus on features rather than boilerplate.",
		) { MdiDataObject(style = IconStyle.ROUNDED) },
		Feature(
			"Collaborative & Open",
			"Kore is fully open-source and thrives on community. Whether solo or in a team, Kore provides the tools for any project size, backed by a growing ecosystem.",
		) { MdiGroupAdd(style = IconStyle.ROUNDED) },
	)

	Div({
		classes(FeaturesSectionStyle.featuresContainer)
	}) {
		H2({
			classes(FeaturesSectionStyle.sectionTitle)
		}) {
			Text("Why choose Kore?")
		}

		P(
			"Kore is built by datapack developers, for datapack developers. It focuses on developer experience, performance, and reliability.",
			FeaturesSectionStyle.sectionSubtitle
		)

		Div({
			classes(FeaturesSectionStyle.grid)
		}) {
			features.forEach { feature ->
				Div({
					classes(FeaturesSectionStyle.feature)
				}) {
					feature.icon()
					H2 {
						Text(feature.title)
					}
					P(feature.description)
				}
			}
		}
	}
}

object FeaturesSectionStyle : StyleSheet() {
	val sectionTitle by style {
		fontSize(2.2.cssRem)
		marginBottom(1.cssRem)
		textAlign(TextAlign.Center)
		width(100.percent)
	}

	val sectionSubtitle by style {
		color(GlobalStyle.altTextColor)
		fontSize(1.1.cssRem)
		marginBottom(2.5.cssRem)
		textAlign(TextAlign.Center)
		width(100.percent)
		marginX(auto)
		maxWidth(40.cssRem)
	}

	val grid by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns {
			repeat(3) { size(1.fr) }
		}
		justifyItems(JustifyItems.Center)
		gap(2.cssRem)

		mdMax(self) {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			gap(3.cssRem)
		}
	}

	val featuresContainer by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		paddingX(7.5.percent)
		paddingY(3.cssRem)

		marginY(3.cssRem)

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)

		lgMax(self) {
			paddingX(4.percent)
			marginTop(2.cssRem)
			marginBottom(4.cssRem)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val feature by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor.alpha(0.5))
		border(1.px, LineStyle.Solid, GlobalStyle.borderColor.alpha(0.2))
		borderRadius(GlobalStyle.roundingSection)
		padding(2.5.cssRem)

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		width(25.vw)
		textAlign(TextAlign.Center)
		transition(0.3.s, "transform", "background-color", "border-color")

		hover(self) style {
			transform { scale(1.02) }
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
			borderColor(GlobalStyle.logoRightColor.alpha(0.5))
		}

		className("material-icons-round") style {
			fontSize(2.5.cssRem)
			color(GlobalStyle.logoRightColor)
			marginBottom(1.cssRem)
		}

		"h2" {
			fontSize(1.6.cssRem)
			marginTop(1.cssRem)
			marginBottom(1.cssRem)
		}

		"p" {
			color(GlobalStyle.altTextColor)
			fontSize(1.05.cssRem)
			property("line-height", "1.5")
			marginBottom(0.px)
		}

		lgMax(self) {
			padding(1.25.cssRem)
			width(28.vw)
		}

		mdMax(self) {
			width(90.percent)
		}
	}
}
