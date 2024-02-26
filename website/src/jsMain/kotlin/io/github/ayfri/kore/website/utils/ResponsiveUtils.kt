package io.github.ayfri.kore.website.utils

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.CSSSelector

val xlBreakpoint = 1280.px
val lgBreakpoint = 1024.px
val mdBreakpoint = 768.px
val smBreakpoint = 640.px
val xsBreakpoint = 480.px
val xxsBreakpoint = 320.px

/**
 * 1279px minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xlMin(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMinWidth(xlBreakpoint), block)

/**
 * 1279px minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xlMin(element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMinWidth(xlBreakpoint)) {
		style(element, block)
	}

/**
 * 1024px minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.lgMin(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMinWidth(lgBreakpoint), block)

/**
 * 1024px minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.lgMin(element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMinWidth(lgBreakpoint)) {
		style(element, block)
	}

/**
 * 768px minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.mdMin(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMinWidth(mdBreakpoint), block)

/**
 * 768px minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.mdMin(element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMinWidth(mdBreakpoint)) {
		style(element, block)
	}

/**
 * 640px minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.smMin(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMinWidth(smBreakpoint), block)

/**
 * 640px minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.smMin(element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMinWidth(smBreakpoint)) {
		style(element, block)
	}

/**
 * 480px minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xsMin(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMinWidth(xsBreakpoint), block)

/**
 * 480px minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xsMin(element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMinWidth(xsBreakpoint)) {
		style(element, block)
	}

/**
 * 320px minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xxsMin(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMinWidth(xxsBreakpoint), block)

/**
 * 320px minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xxsMin(element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMinWidth(xxsBreakpoint)) {
		style(element, block)
	}

/****************************************************
 * Maximum width
 ****************************************************/

/**
 * 1279px maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xlMax(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMaxWidth(xlBreakpoint - 1.px), block)

/**
 * 1279px maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xlMax(element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMaxWidth(xlBreakpoint - 1.px)) {
		style(element, block)
	}

/**
 * 1023px maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.lgMax(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMaxWidth(lgBreakpoint - 1.px), block)

/**
 * 1023px maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.lgMax(element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMaxWidth(lgBreakpoint - 1.px)) {
		style(element, block)
	}

/**
 * 767px maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.mdMax(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMaxWidth(mdBreakpoint - 1.px), block)

/**
 * 767px maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.mdMax(element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMaxWidth(mdBreakpoint - 1.px)) {
		style(element, block)
	}

/**
 * 639px maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.smMax(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMaxWidth(smBreakpoint - 1.px), block)

/**
 * 639px maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.smMax(element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMaxWidth(smBreakpoint - 1.px)) {
		style(element, block)
	}

/**
 * 479px maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xsMax(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMaxWidth(xsBreakpoint - 1.px), block)

/**
 * 479px maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xsMax(element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMaxWidth(xsBreakpoint - 1.px)) {
		style(element, block)
	}

/**
 * 319px maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xxsMax(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMaxWidth(xxsBreakpoint - 1.px), block)

/**
 * 319px maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xxsMax(element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMaxWidth(xxsBreakpoint - 1.px)) {
		style(element, block)
	}

/****************************************************
 * Custom width
 ****************************************************/

/**
 * Custom minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.minWidthBreak(
	minWidth: CSSUnitValue,
	block: GenericStyleSheetBuilder<TBuilder>.() -> Unit,
) =
	media(mediaMinWidth(minWidth), block)

/**
 * Custom minimum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.minWidthBreak(minWidth: CSSUnitValue, element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMinWidth(minWidth)) {
		style(element, block)
	}

/**
 * Custom maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.maxWidthBreak(
	maxWidth: CSSUnitValue,
	block: GenericStyleSheetBuilder<TBuilder>.() -> Unit,
) =
	media(mediaMaxWidth(maxWidth), block)

/**
 * Custom maximum width.
 */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.maxWidthBreak(maxWidth: CSSUnitValue, element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMaxWidth(maxWidth)) {
		style(element, block)
	}
