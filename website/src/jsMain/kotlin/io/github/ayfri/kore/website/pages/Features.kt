package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.components.common.BrandIconStyle
import io.github.ayfri.kore.website.components.common.setDescription
import io.github.ayfri.kore.website.components.common.setKeywords
import io.github.ayfri.kore.website.components.features.*
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.components.mc.McUiStyle
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div

@Page
@Composable
fun FeaturesPage() {
	Style(HomePageStyle)
	Style(BrandIconStyle)
	Style(FeatureSectionsStyle)
	Style(FeatureVisualsStyle)
	Style(McUiStyle)

	PageLayout("Features - Commands, JSON Resources, Worldgen & Tooling") {
		setDescription("Every Kore feature in one place. Typed commands, loot tables, recipes, worldgen, gameplay helpers, Gradle plugin and mod jar export for Minecraft datapacks.")
		setKeywords(
			"kore features", "minecraft datapack generator features", "kotlin datapack dsl", "datapack worldgen generator",
			"loot table generator", "mcfunction generator", "datapack gradle plugin", "minecraft datapack library"
		)

		Div({ classes(HomePageStyle.page) }) {
			Div({ classes(FeatureSectionsStyle.layout) }) {
				FeaturesNav()
				Div {
					FeaturesHeader()
					ShowcaseSection()
					featureCategories.forEach { CategorySection(it) }
					LimitsSection()
				FeaturesCta()
				}
			}
		}
	}
}
