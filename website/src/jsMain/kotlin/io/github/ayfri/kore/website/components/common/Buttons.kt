package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.A
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target

@Composable
fun LinkButton(name: String, link: String, target: ATarget? = null, vararg classes: String) = A(link, name) {
	classes(GlobalStyle.linkButton, *classes)
	target?.let { target(it) }
}
