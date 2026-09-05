package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontStyle
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.fontStyle
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.setDescription
import io.github.ayfri.kore.website.components.common.setKeywords
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.components.updates.GitHubService
import io.github.ayfri.kore.website.components.updates.ReleasesList
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.paddingX
import io.github.ayfri.kore.website.utils.paddingY
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Page
@Composable
fun UpdatesPage() {
	Style(UpdatesPageStyle)

	setDescription("Kore changelog and release history for the Minecraft datapack generator. Browse all versions, release notes, and updates automatically fetched from GitHub.")
	setKeywords("kore releases", "kore changelog", "datapack generator updates", "kore version history", "minecraft datapack library releases", "kore github releases")

	PageLayout("Kore Releases - Changelog & Version History") {
		Div({
			classes(UpdatesPageStyle.container)
		}) {
			Div({
				classes(UpdatesPageStyle.hero)
			}) {
				H1({
					classes(UpdatesPageStyle.pageTitle)
				}) {
					Text("Kore Releases")
				}

				P({
					classes(UpdatesPageStyle.description)
				}) {
					Text("Browse all Kore releases, release notes, and version history, automatically fetched from GitHub.")
				}

				P({
					classes(UpdatesPageStyle.note)
				}) {
					Text("Changelogs focus on what changed between two minor Minecraft releases. If there were no changes in-between, you may see a short or empty entry.")
				}

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
		maxWidth(1320.px)
		marginX(auto)
		width(100.percent)

		mdMax(self) {
			paddingX(3.percent)
			paddingY(1.5.cssRem)
		}
	}

	val hero by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(0.7.cssRem)
		marginBottom(1.3.cssRem)

		mdMax(self) {
			gap(0.6.cssRem)
			marginBottom(1.2.cssRem)
		}
	}

	val pageTitle by style {
		fontSize(2.25.cssRem)
		textAlign(TextAlign.Center)
		fontWeight(700)
	}

	val description by style {
		fontSize(1.2.cssRem)
		color(GlobalStyle.altTextColor)
		textAlign(TextAlign.Center)
		marginBottom(0.2.cssRem)
		maxWidth(800.px)
		lineHeight(1.5.number)

		mdMax(self) {
			fontSize(1.1.cssRem)
			marginBottom(1.2.cssRem)
		}
	}

	val note by style {
		fontSize(0.95.cssRem)
		fontStyle(FontStyle.Italic)
		color(GlobalStyle.altTextColor)
		margin(0.px)
		maxWidth(860.px)
		lineHeight(1.45.number)
		opacity(0.9)
		textAlign(TextAlign.Center)

		mdMax(self) {
			fontSize(0.9.cssRem)
		}
	}
}
