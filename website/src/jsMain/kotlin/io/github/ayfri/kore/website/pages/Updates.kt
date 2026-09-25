package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.components.common.BrandIconStyle
import io.github.ayfri.kore.website.components.common.setDescription
import io.github.ayfri.kore.website.components.common.setImage
import io.github.ayfri.kore.website.components.common.setKeywords
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.components.updates.GitHubService
import io.github.ayfri.kore.website.components.updates.ReleasesList
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div

@Page
@Composable
fun UpdatesPage() {
	Style(HomePageStyle)
	Style(BrandIconStyle)

	PageLayout("Kore Releases - Changelog & Version History") {
		// PageLayout writes the site-wide defaults, so page-specific meta must be set after it, inside the content.
		setDescription("Kore changelog and release history for the Minecraft datapack generator. Browse all versions, release notes, and updates automatically fetched from GitHub.")
		setImage("updates", "Latest Kore release and changelog")
		setKeywords("kore releases", "kore changelog", "datapack generator updates", "kore version history", "minecraft datapack library releases", "kore github releases")

		Div({ classes(HomePageStyle.page) }) {
			ReleasesList(GitHubService.getReleases())
		}
	}
}
