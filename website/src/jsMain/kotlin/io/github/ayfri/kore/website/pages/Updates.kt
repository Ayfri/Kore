package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.components.updates.GitHubService
import io.github.ayfri.kore.website.components.updates.ReleasesList
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.paddingX
import io.github.ayfri.kore.website.utils.paddingY
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*

@Page
@Composable
fun UpdatesPage() {
	Style(UpdatesPageStyle)

	PageLayout("Kore Releases | Latest Releases and Updates") {
		Div({
			classes(UpdatesPageStyle.container)
		}) {
			H1({
				classes(UpdatesPageStyle.pageTitle)
			}) {
				Text("Kore Releases")
			}

			P({
				classes(UpdatesPageStyle.description)
			}) {
				Text("The latest versions of Kore, automatically fetched from GitHub.")
			}

			val releases = GitHubService.getReleases()

			ReleasesList(releases)
		}
	}
}

object UpdatesPageStyle : StyleSheet() {
	val container by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		paddingX(5.percent)
		paddingY(1.cssRem)
		maxWidth(1200.px)
		marginX(auto)
		width(100.percent)

		mdMax(self) {
			paddingX(3.percent)
			paddingY(1.5.cssRem)
		}
	}

	val pageTitle by style {
		fontSize(2.5.cssRem)
		marginBottom(0.5.cssRem)
		textAlign(TextAlign.Center)
		fontWeight(700)

		mdMax(self) {
			fontSize(2.5.cssRem)
		}
	}

	val description by style {
		fontSize(1.2.cssRem)
		color(GlobalStyle.altTextColor)
		textAlign(TextAlign.Center)
		marginBottom(2.cssRem)
		maxWidth(800.px)
		lineHeight(1.5.number)

		mdMax(self) {
			fontSize(1.1.cssRem)
			marginBottom(2.cssRem)
		}
	}
}
