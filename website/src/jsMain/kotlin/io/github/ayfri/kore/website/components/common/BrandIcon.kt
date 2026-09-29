package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignSelf
import com.varabyte.kobweb.compose.css.functions.url
import io.github.ayfri.kore.website.utils.maskImage
import io.github.ayfri.kore.website.utils.maskPosition
import io.github.ayfri.kore.website.utils.maskRepeat
import io.github.ayfri.kore.website.utils.maskSize
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Span

/** A monochrome brand icon from `/icons/<name>.svg`, tinted with the current text color through a CSS mask. */
@Composable
fun BrandIcon(name: String) {
	Span({
		classes(BrandIconStyle.icon)
		attr("aria-hidden", "true")
		style { maskImage(url("/icons/$name.svg")) }
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
		alignSelf(AlignSelf.Center)
		verticalAlign((-0.125).em)
		maskPosition(BackgroundPosition.of(CSSPosition.Center))
		maskRepeat(BackgroundRepeat.NoRepeat)
		maskSize(BackgroundSize.Contain)
	}
}
