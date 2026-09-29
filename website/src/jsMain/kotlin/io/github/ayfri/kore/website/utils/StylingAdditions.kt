package io.github.ayfri.kore.website.utils

import com.varabyte.kobweb.compose.css.BackgroundClip
import com.varabyte.kobweb.compose.css.BackgroundPosition
import com.varabyte.kobweb.compose.css.BackgroundRepeat
import com.varabyte.kobweb.compose.css.BackgroundSize
import com.varabyte.kobweb.compose.css.CSSLengthNumericValue
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.Content
import com.varabyte.kobweb.compose.css.backgroundClip
import com.varabyte.kobweb.compose.css.backgroundImage
import com.varabyte.kobweb.compose.css.functions.CSSImage
import com.varabyte.kobweb.compose.css.functions.CSSUrl
import com.varabyte.kobweb.compose.css.functions.linearGradient
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword

typealias CSSTimeValue = CSSSizeValue<out CSSUnitTime>

/** The line-height unit, whose type Compose HTML declares without a `Number` shortcut. */
val Number.lh get(): CSSSizeValue<CSSUnit.lh> = CSSUnitValueTyped(toFloat(), CSSUnit.lh)

/** CSS `round()`, [value] rounded to the nearest multiple of [interval], missing next to Kobweb's `clamp`, `min` and `max`. */
data class CSSRound<T : CSSUnit>(val value: CSSNumeric, val interval: CSSNumericValue<T>) : CSSNumericValue<T> {
	override fun toString() = "round($value, $interval)"
}

fun <T : CSSUnit> round(value: CSSNumeric, interval: CSSNumericValue<T>) = CSSRound(value, interval)

/** CSS `calc-size(auto, size)`, the element's auto size as a value `height` transitions can animate, capped at [max] when given. */
fun autoSize(max: CSSLengthNumericValue? = null) =
	"calc-size(auto, ${max?.let { "min(size, $it)" } ?: "size"})".unsafeCast<CSSNumeric>()

/** A `counter()` reference for [content], e.g. line numbers counted with [counterIncrement]. */
fun counter(name: String) = "counter($name)".unsafeCast<Content.Listable>()

/**
 * Sets `animation-delay`, which Compose HTML and Kobweb only expose through the `animation` shorthand and through
 * `Modifier.animation`'s [com.varabyte.kobweb.compose.ui.modifiers.AnimationScope], neither of which fits a plain
 * `StyleSheet` rule that overrides the delay alone.
 */
fun StyleScope.animationDelay(vararg delays: CSSTimeValue) = property("animation-delay", delays.joinToString())

/** Stacks several images in one `background-image`, the first drawn on top, which Kobweb's single-image setter can't express. */
fun StyleScope.backgroundImages(vararg images: CSSUrl) = backgroundImage(images.joinToString())

fun StyleScope.borderBottomWidth(width: CSSLengthNumericValue) = property("border-bottom-width", width)

fun StyleScope.borderLeftColor(color: CSSColorValue) = property("border-left-color", color)

fun StyleScope.borderTopColor(color: CSSColorValue) = property("border-top-color", color)

/** `clip-path: inset(...)`, [edges] given like `margin`'s, which Kobweb has no builder for. */
fun StyleScope.clipPathInset(vararg edges: CSSLengthOrPercentageNumericValue) = property("clip-path", "inset(${edges.joinToString(" ")})")

enum class ContentVisibility {
	AUTO,
	HIDDEN,
	VISIBLE,
}

fun StyleScope.contentVisibility(visibility: ContentVisibility) = property("content-visibility", visibility.name.lowercase())

fun StyleScope.counterIncrement(name: String) = property("counter-increment", name)

fun StyleScope.counterReset(name: String) = property("counter-reset", name)

/**
 * Sets the SVG `fill` presentation property, which neither Compose HTML nor Kobweb exposes as a CSS builder. Lucide
 * icons ship as stroke-only outlines, so a solid glyph needs `fill` overridden from CSS.
 */
fun StyleScope.fill(color: CSSColorValue) = property("fill", color)

enum class Hyphens {
	AUTO,
	MANUAL,
	NONE,
}

fun StyleScope.hyphens(hyphens: Hyphens) = property("hyphens", hyphens.name.lowercase())

/** Marks every property [block] sets `!important`, the only way a stylesheet rule beats an inline style. */
fun StyleScope.important(block: StyleScope.() -> Unit) {
	val scope = this
	object : StyleScope {
		override fun property(propertyName: String, value: StylePropertyValue) = scope.property(propertyName, value, true)
		override fun variable(variableName: String, value: StylePropertyValue) = scope.variable(variableName, value)
	}.block()
}

fun StyleScope.inset(value: CSSLengthOrPercentageNumericValue) = property("inset", value)

/** A unitless `line-height`: Compose's `1.6.number` serializes as `1.6number`, which browsers drop. */
fun StyleScope.lineHeight(value: Number) = property("line-height", value)

/** Cuts text after [lines] lines with an ellipsis, through the `-webkit-box` layout browsers still require for it. */
fun StyleScope.lineClamp(lines: Int) {
	property("display", "-webkit-box")
	property("-webkit-box-orient", "vertical")
	property("-webkit-line-clamp", lines)
}

fun StyleScope.marginLeft(value: CSSAutoKeyword) = property("margin-left", value)

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

/** The mask longhands take the same values as their `background-*` counterparts, so they reuse Kobweb's background types. */
fun StyleScope.maskImage(image: CSSImage) = property("mask-image", image)

fun StyleScope.maskImage(url: CSSUrl) = maskImage(CSSImage.of(url))

fun StyleScope.maskPosition(position: BackgroundPosition) = property("mask-position", position)

fun StyleScope.maskRepeat(repeat: BackgroundRepeat) = property("mask-repeat", repeat)

fun StyleScope.maskSize(size: BackgroundSize) = property("mask-size", size)

fun StyleScope.paddingX(value: CSSNumeric) {
	paddingLeft(value)
	paddingRight(value)
}

fun StyleScope.paddingY(value: CSSNumeric) {
	paddingTop(value)
	paddingBottom(value)
}

/** The mobile tap flash, only settable through its `-webkit-` name. */
fun StyleScope.tapHighlightColor(color: CSSColorValue) = property("-webkit-tap-highlight-color", color)

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
	backgroundImage(linearGradient(color1, color2, 90.deg))
	backgroundClip(BackgroundClip.Text)
	property("-webkit-text-fill-color", "transparent")
}

fun StyleScope.scrollbarColor(
	thumbColor: CSSColorValue,
	trackColor: CSSColorValue,
) = property("scrollbar-color", "$thumbColor $trackColor")

fun StyleScope.zoom(factor: Number) = property("zoom", factor)

inline val SelectorsScope.placeholder get() = selector("::placeholder")

fun CSSColorValue.alpha(alpha: String) = Color(toString() + alpha)
fun CSSColorValue.alpha(alpha: Double) = Color(toString() + (alpha * 255).toInt().toString(16).padStart(2, '0'))
