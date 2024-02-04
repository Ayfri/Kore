package io.github.ayfri.kore.website

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.KobwebApp
import org.jetbrains.compose.web.css.Style

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
	KobwebApp {
		Style(GlobalStyle)
		content()
	}
}
