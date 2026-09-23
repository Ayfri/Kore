package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Span

/** A monochrome brand icon from `/icons/<name>.svg`, tinted with the current text color through a CSS mask. */
@Composable
fun BrandIcon(name: String) {
	Span({
		classes(BrandIconStyle.icon)
		attr("aria-hidden", "true")
		style {
			property("mask-image", "url(/icons/$name.svg)")
			property("-webkit-mask-image", "url(/icons/$name.svg)")
		}
	})
}

object BrandIconStyle : StyleSheet() {
	val icon by style {
		backgroundColor(Color("currentColor"))
		display(DisplayStyle.InlineBlock)
		flexShrink(0)
		height(1.em)
		opacity(0.85)
		width(1.em)
		property("align-self", "center")
		property("vertical-align", "-0.125em")
		property("mask-position", "center")
		property("mask-repeat", "no-repeat")
		property("mask-size", "contain")
		property("-webkit-mask-position", "center")
		property("-webkit-mask-repeat", "no-repeat")
		property("-webkit-mask-size", "contain")
	}
}
