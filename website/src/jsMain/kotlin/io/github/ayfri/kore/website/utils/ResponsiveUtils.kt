package io.github.ayfri.kore.website.utils

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.CSSSelector

private val xlBreakpoint = 1280.px
private val lgBreakpoint = 1024.px
private val mdBreakpoint = 768.px
private val smBreakpoint = 640.px
private val xsBreakpoint = 480.px

private fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.from(breakpoint: CSSSizeValue<CSSUnit.px>, element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMinWidth(breakpoint)) {
		style(element, block)
	}

private fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.below(breakpoint: CSSSizeValue<CSSUnit.px>, element: CSSSelector, block: TBuilder.() -> Unit) =
	media(mediaMaxWidth(breakpoint - 1.px)) {
		style(element, block)
	}

/** 1024px minimum width. */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.lgMin(element: CSSSelector, block: TBuilder.() -> Unit) = from(lgBreakpoint, element, block)

/** 768px minimum width. */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.mdMin(element: CSSSelector, block: TBuilder.() -> Unit) = from(mdBreakpoint, element, block)

/** 1279px maximum width. */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xlMax(element: CSSSelector, block: TBuilder.() -> Unit) = below(xlBreakpoint, element, block)

/** 1023px maximum width. */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.lgMax(element: CSSSelector, block: TBuilder.() -> Unit) = below(lgBreakpoint, element, block)

/** 767px maximum width. */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.mdMax(element: CSSSelector, block: TBuilder.() -> Unit) = below(mdBreakpoint, element, block)

/** 639px maximum width. */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.smMax(block: GenericStyleSheetBuilder<TBuilder>.() -> Unit) =
	media(mediaMaxWidth(smBreakpoint - 1.px), block)

/** 639px maximum width. */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.smMax(element: CSSSelector, block: TBuilder.() -> Unit) = below(smBreakpoint, element, block)

/** 479px maximum width. */
fun <TBuilder> GenericStyleSheetBuilder<TBuilder>.xsMax(element: CSSSelector, block: TBuilder.() -> Unit) = below(xsBreakpoint, element, block)
