package io.github.ayfri.kore.website.utils

import com.varabyte.kobweb.compose.css.BackgroundClip
import com.varabyte.kobweb.compose.css.CSSColor
import com.varabyte.kobweb.compose.css.backgroundClip
import com.varabyte.kobweb.compose.css.backgroundImage
import com.varabyte.kobweb.compose.css.functions.linearGradient
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword
import org.jetbrains.compose.web.css.selectors.CSSSelector.PseudoElement.after

typealias CSSTimeValue = CSSSizeValue<out CSSUnitTime>

fun StyleScope.marginX(value: CSSNumeric) {
	marginLeft(value)
	marginRight(value)
}

fun StyleScope.marginX(value: CSSAutoKeyword) {
	property("margin-left", value)
	property("margin-right", value)
}

fun StyleScope.marginY(value: CSSNumeric) {
	marginTop(value)
	marginBottom(value)
}

fun StyleScope.marginY(value: CSSAutoKeyword) {
	property("margin-top", value)
	property("margin-bottom", value)
}

fun StyleScope.paddingX(value: CSSNumeric) {
	paddingLeft(value)
	paddingRight(value)
}

fun StyleScope.paddingX(value: CSSAutoKeyword) {
	property("padding-left", value)
	property("padding-right", value)
}

fun StyleScope.paddingY(value: CSSNumeric) {
	paddingTop(value)
	paddingBottom(value)
}

fun StyleScope.paddingY(value: CSSAutoKeyword) {
	property("padding-top", value)
	property("padding-bottom", value)
}

fun StyleScope.transition(
	duration: CSSTimeValue,
	vararg properties: String,
) = transition(
	duration,
	AnimationTimingFunction.EaseInOut,
	null,
	*properties
)

fun StyleScope.transition(
	duration: CSSTimeValue,
	function: AnimationTimingFunction = AnimationTimingFunction.EaseInOut,
	vararg properties: String,
) = transition(
	duration,
	function,
	null,
	*properties
)

fun StyleScope.transition(
	duration: CSSTimeValue,
	delay: CSSTimeValue,
	vararg properties: String,
) = transition(
	duration,
	AnimationTimingFunction.EaseInOut,
	delay,
	*properties
)

@OptIn(ExperimentalComposeWebApi::class)
fun StyleScope.transition(
	duration: CSSTimeValue,
	function: AnimationTimingFunction = AnimationTimingFunction.EaseInOut,
	delay: CSSTimeValue? = null,
	vararg properties: String,
) = transitions {
	delay?.let { defaultDelay(it) }
	defaultDuration(duration)
	defaultTimingFunction(function)
	properties(*properties)
}

fun StyleScope.textGradient(
	color1: CSSColorValue,
	color2: CSSColorValue,
) {
	backgroundImage(linearGradient(90.deg, color1, color2))
	backgroundClip(BackgroundClip.Text)
	property("-webkit-text-fill-color", "transparent")
}

fun StyleScope.scrollbarColor(
	thumbColor: CSSColorValue,
	trackColor: CSSColorValue,
) {
	property("scrollbar-color", "$thumbColor $trackColor")
	property("-webkit-scrollbar-color", "$thumbColor $trackColor")
}

enum class ScrollbarFaceColor {
	AUTO,
	DARK,
	LIGHT,
}

fun StyleScope.scrollbarColor(
	color: ScrollbarFaceColor,
) {
	property("scrollbar-color", color.name.lowercase())
}

enum class ScrollbarGutter {
	AUTO,
	STABLE,
	BOTH_EDGES,
}

fun StyleScope.scrollbarGutter(
	gutter: ScrollbarGutter,
) {
	property("scrollbar-gutter", gutter.name.lowercase())
}

enum class ScrollbarWidth {
	AUTO,
	THIN,
	NONE,
}

fun StyleScope.scrollbarWidth(
	width: ScrollbarWidth,
) {
	property("scrollbar-width", width.name.lowercase())
}


inline val SelectorsScope.placeholder get() = selector("::placeholder")

fun CSSColorValue.alpha(alpha: String) = Color(toString() + alpha)
fun CSSColorValue.alpha(alpha: Double) = Color(toString() + js("Math.floor(255 * alpha).toString(16).padStart(2, '0')"))
