package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.components.layouts.PageLayout
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Page
@Composable
fun HomePage() {
	PageLayout("Home") {
		P {
			Text("Welcome to Kobweb!")
		}
	}
}
