package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.components.common.BrandIconStyle
import io.github.ayfri.kore.website.components.common.setDescription
import io.github.ayfri.kore.website.components.common.setKeywords
import io.github.ayfri.kore.website.components.features.*
import io.github.ayfri.kore.website.components.index.CtaSection
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.components.mc.McUiStyle
import io.github.ayfri.kore.website.utils.initMCFunctionHighlighting
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

	LaunchedEffect(Unit) {
		initMCFunctionHighlighting()
	}

	PageLayout("Features - Commands, JSON Resources, Worldgen & Tooling") {
		setDescription("Every Kore feature in one place. Typed commands, loot tables, recipes, worldgen, gameplay helpers, Gradle plugin and mod jar export for Minecraft datapacks.")
		setKeywords(
			"kore features", "minecraft datapack generator features", "kotlin datapack dsl", "datapack worldgen generator",
			"loot table generator", "mcfunction generator", "datapack gradle plugin", "minecraft datapack library"
		)

		Div({ classes(HomePageStyle.page) }) {
			Div({ classes(HomePageStyle.content) }) {
				FeaturesHero()
				ShowcaseSection()
				featureCategories.forEachIndexed { index, category -> CategorySection(category, index) }
				LimitsSection()
				CtaSection()
			}
		}
	}
}
