package io.github.ayfri.kore.website

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.KobwebApp

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
	KobwebApp {
		content()
	}
}
