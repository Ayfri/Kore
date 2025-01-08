package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import io.github.ayfri.kore.website.GlobalStyle
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Text

@Composable
fun LinkButton(name: String, link: String, target: ATarget? = null, icon: @Composable () -> Unit = {}, vararg classes: String) = A(link, {
	classes(GlobalStyle.linkButton, *classes)
	target?.let { target(it) }
}) {
	icon()
	Text(name)
}
