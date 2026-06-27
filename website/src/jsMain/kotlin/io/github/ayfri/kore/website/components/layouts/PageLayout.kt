package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.kore.website.CodeThemeStyle
import io.github.ayfri.kore.website.components.common.*
import io.github.ayfri.kore.website.components.sections.Footer
import io.github.ayfri.kore.website.components.sections.Header
import io.github.ayfri.kore.website.components.updates.GitHubService
import io.github.ayfri.kore.website.utils.loadPrism
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.dom.Main
import org.w3c.dom.url.URL

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
	Style(PageLayoutStyle)
	Style(CodeThemeStyle)

	val route = rememberPageContext().route
	val baseUrl = AppGlobals["websiteUrl"] ?: "https://kore.ayfri.com"
	val url = URL(baseUrl + route.path)
	url.search = ""
	url.hash = ""

	setCanonical(url.href)
	setTitle("$title - Kore | Minecraft Datapack Generator")
	setDescription("Kore is a modern, type-safe Kotlin datapack generator for Minecraft. Open-source and easy to use. Create datapacks without writing JSON or MCFunction by hand.")
	setKeywords(
		"Minecraft datapack generator", "datapack generator", "Minecraft datapack", "Kotlin DSL",
		"Kore", "datapack development", "Minecraft Java Edition", "MCFunction", "datapack library"
	)
	setType("website")

	setTwitterCard("summary_large_image")
	setTwitterCreator("@Ayfri_")

	setImage("$baseUrl/logo.png")

	val latestRelease = GitHubService.getReleases().maxByOrNull { it.publishedAt }

	Header(latestRelease)

	Main({
		classes(PageLayoutStyle.main)
	}) {
		content()
	}

	Footer()


	loadPrism()
}

object PageLayoutStyle : StyleSheet() {
	val main by style {
	}
}
