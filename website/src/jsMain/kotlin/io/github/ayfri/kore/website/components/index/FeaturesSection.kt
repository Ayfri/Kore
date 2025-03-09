package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle
import com.varabyte.kobweb.silk.components.icons.mdi.MdiArchitecture
import com.varabyte.kobweb.silk.components.icons.mdi.MdiDataObject
import com.varabyte.kobweb.silk.components.icons.mdi.MdiGroupAdd
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
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
			"Modern",
			"Write datapacks for recent Minecraft versions with a modern programming language leveraging Kotlin features for robust code.",
		) { MdiArchitecture(style = IconStyle.ROUNDED) },
		Feature(
			"Easy to use",
			"Intuitive API and abstractions over vanilla minimize complexity for simple datapack development.",
		) { MdiDataObject(style = IconStyle.ROUNDED) },
		Feature(
			"Open Source",
			"Active community and contributions provide freedom and support for any open source project.",
		) { MdiGroupAdd(style = IconStyle.ROUNDED) },
	)

	Div({
		classes(FeaturesSectionStyle.featuresContainer)
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

object FeaturesSectionStyle : StyleSheet() {
	val featuresContainer by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		paddingX(7.5.percent)
		paddingY(5.cssRem)

		marginY(7.cssRem)

		display(DisplayStyle.Grid)
		gridTemplateColumns {
			repeat(3) { size(1.fr) }
		}
		justifyItems(JustifyItems.Center)

		lgMax(self) {
			paddingX(4.percent)
			marginTop(4.cssRem)
			marginBottom(6.cssRem)
		}

		mdMax(self) {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			gap(3.cssRem)
		}
	}

	val feature by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingSection)
		padding(2.cssRem)

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		width(25.vw)
		textAlign(TextAlign.Center)

		className("material-icons-round") style {
			fontSize(2.cssRem)
			color(GlobalStyle.altTextColor)
		}

		"p" {
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
