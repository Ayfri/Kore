package io.github.ayfri.kore.website

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import io.github.ayfri.kore.website.pages.PageNotFound
import org.jetbrains.compose.web.css.Style

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
	KobwebApp {
		Style(GlobalStyle)
		content()
	}
}

@InitKobweb
fun initKobweb(context: InitKobwebContext) {
	context.router.setErrorHandler {
		if (it != 404) return@setErrorHandler

		context.router.renderActivePage {
			PageNotFound()
		}
	}
}
