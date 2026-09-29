package io.github.ayfri.kore.website

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.CSSImage
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.compose.css.functions.url
import com.varabyte.kobweb.compose.ui.graphics.Colors
import io.github.ayfri.kore.website.utils.Hyphens
import io.github.ayfri.kore.website.utils.counter
import io.github.ayfri.kore.website.utils.counterIncrement
import io.github.ayfri.kore.website.utils.counterReset
import io.github.ayfri.kore.website.utils.hyphens
import io.github.ayfri.kore.website.utils.lh
import io.github.ayfri.kore.website.utils.lineHeight
import io.github.ayfri.kore.website.utils.maskImage
import io.github.ayfri.kore.website.utils.maskPosition
import io.github.ayfri.kore.website.utils.maskRepeat
import io.github.ayfri.kore.website.utils.maskSize
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.selectors.CSSSelector

/** The top of the hovered code line, set by the hover handler in `RuntimeAdditions.kt`. */
val HoverY by StyleVariable<CSSLengthNumericValue>()

/** Code block chrome and syntax colors, replacing Prism's own theme so every token color lives here. */
@OptIn(ExperimentalComposeWebApi::class)
object CodeThemeStyle : StyleSheet() {
	val textColor = Color("#dde3ea")
	val commentColor = Color("#6b737f")
	val numberColor = Color("#f78c6c")
	val stringColor = Color("#c3e88d")
	val keywordColor = Color("#c792ea")
	val functionColor = Color("#82aaff")
	val classColor = Color("#ffcb6b")
	val propertyColor = Color("#f07178")
	val punctuationColor = Color("#89ddff")
	val lineNumberColor = Color("#4b5263")
	val chromeBorderColor = rgba(255, 255, 255, 0.07)

	val fonts = arrayOf("JetBrains Mono", "Consolas", "Monaco", "monospace")

	private const val TOOLBAR_ITEM = "div.code-toolbar > .toolbar > .toolbar-item"

	/** Lucide icon paths, drawn through a CSS mask so they take the current text color. */
	private object Icons {
		const val BRACES = "<path d='M8 3H7a2 2 0 0 0-2 2v5a2 2 0 0 1-2 2 2 2 0 0 1 2 2v5c0 1.1.9 2 2 2h1'/><path d='M16 21h1a2 2 0 0 0 2-2v-5c0-1.1.9-2 2-2a2 2 0 0 1-2-2V5a2 2 0 0 0-2-2h-1'/>"
		const val CHECK = "<path d='M20 6 9 17l-5-5'/>"
		const val CODE_XML = "<path d='m18 16 4-4-4-4'/><path d='m6 8-4 4 4 4'/><path d='m14.5 4-5 16'/>"
		const val COPY ="<rect width='14' height='14' x='8' y='8' rx='2' ry='2'/><path d='M4 16c-1.1 0-2-.9-2-2V4c0-1.1.9-2 2-2h10c1.1 0 2 .9 2 2'/>"
		const val FILE_CODE = "<path d='M10 12.5 8 15l2 2.5'/><path d='m14 12.5 2 2.5-2 2.5'/><path d='M14 2v4a2 2 0 0 0 2 2h4'/><path d='M15 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7z'/>"
		const val SQUARE_TERMINAL = "<path d='m7 11 2-2-2-2'/><path d='M11 13h4'/><rect width='18' height='18' x='3' y='3' rx='2' ry='2'/>"
		const val TERMINAL = "<path d='M12 19h8'/><path d='m4 17 6-6-6-6'/>"

		fun svg(paths: String) =
			url("data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='black' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'>$paths</svg>")
	}

	/** The official Kotlin mark with its brand gradient, a real logo rather than a masked monochrome icon. */
	private val KOTLIN_LOGO =
		url("data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'><defs><linearGradient id='k' x1='24' y1='0' x2='0' y2='24' gradientUnits='userSpaceOnUse'><stop offset='0' stop-color='%23E44857'/><stop offset='.47' stop-color='%23C711E1'/><stop offset='1' stop-color='%237F52FF'/></linearGradient></defs><path fill='url(%23k)' d='M24 24H0V0h24L12 12Z'/></svg>")

	fun scope(vararg names: String): CSSSelector {
		val initial = className("token")
		val subClasses = names.map { initial + className(it) }
		return group(*subClasses.toTypedArray())
	}

	init {
		// `:where` keeps these base rules at zero specificity, so each page's own `pre` sizing and spacing still wins.
		":where(code[class*='language-'], pre[class*='language-'])" style {
			color(textColor)
			fontFamily(*fonts)
			fontVariantLigatures(FontVariantLigatures.None)
			hyphens(Hyphens.NONE)
			lineHeight(1.6)
			tabSize(TabSize.of(4))
			textAlign(TextAlign.Left)
			whiteSpace(WhiteSpace.Pre)
			wordBreak(WordBreak.Normal)
			wordSpacing(WordSpacing.Normal)
		}

		":where(pre[class*='language-'])" style {
			fontSize(0.875.cssRem)
			margin(0.px)
			overflow(Overflow.Auto)
			padding(0.7.cssRem, 0.9.cssRem)
			scrollbarWidth(ScrollbarWidth.Thin)
		}

		":where(pre[class*='language-']) ::selection" style {
			backgroundColor(rgba(130, 170, 255, 0.25))
		}

		"pre[class*='language-']:hover" style {
			backgroundAttachment(BackgroundAttachment.Local)
			backgroundImage(linearGradient(rgba(255, 255, 255, 0.045), rgba(255, 255, 255, 0.045)))
			backgroundPosition(BackgroundPosition.of(CSSPosition(0.px, HoverY.value((-100).lh))))
			backgroundRepeat(BackgroundRepeat.NoRepeat)
			backgroundSize(BackgroundSize.of(100.percent, 1.lh))
		}

		/** `inline-block` makes each guide span the full line box, so consecutive lines join into one unbroken rule. */
		scope("indent-guide") style {
			boxShadow(1.px, 0.px, 0.px, color = rgba(255, 255, 255, 0.09), inset = true)
			display(DisplayStyle.InlineBlock)
		}

		"div.code-toolbar" style {
			backgroundColor(GlobalStyle.secondaryBackgroundColor)
			border(1.px, LineStyle.Solid, chromeBorderColor)
			borderRadius(0.6.cssRem)
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			overflow(Overflow.Hidden)
			position(Position.Relative)
		}

		"div.code-toolbar > pre" style {
			backgroundColor(Colors.Transparent)
			border(0.px)
			borderRadius(0.px)
			flexGrow(1)
			margin(0.px)
		}

		"div.code-toolbar > .toolbar" style {
			alignItems(AlignItems.Center)
			backgroundColor(rgba(255, 255, 255, 0.025))
			borderBottom(1.px, LineStyle.Solid, chromeBorderColor)
			display(DisplayStyle.Flex)
			fontFamily("Roboto", "sans-serif")
			fontSize(0.72.cssRem)
			justifyContent(JustifyContent.SpaceBetween)
			order(-1)
			padding(0.3.cssRem, 0.5.cssRem, 0.3.cssRem, 0.9.cssRem)
		}

		TOOLBAR_ITEM style {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
		}

		"$TOOLBAR_ITEM > span, $TOOLBAR_ITEM > button" style {
			alignItems(AlignItems.Center)
			display(DisplayStyle.LegacyInlineFlex)
			gap(0.45.cssRem)
		}

		"$TOOLBAR_ITEM > span::before, $TOOLBAR_ITEM > button::before" style {
			backgroundColor(Color.currentColor)
			content("")
			height(0.9.cssRem)
			maskImage(Icons.svg(Icons.FILE_CODE))
			maskPosition(BackgroundPosition.of(CSSPosition.Center))
			maskRepeat(BackgroundRepeat.NoRepeat)
			maskSize(BackgroundSize.Contain)
			width(0.9.cssRem)
		}

		"$TOOLBAR_ITEM > span" style {
			color(GlobalStyle.altTextColor)
			fontWeight(500)
			letterSpacing(0.08.cssRem)
			lineHeight(1)
			textTransform(TextTransform.Uppercase)
			// Uppercase text has no descenders, so its box centers the caps ~1px high: shift it down and the logo back up.
			translate(0.px, 1.px)
		}

		"$TOOLBAR_ITEM > span::before" style {
			translate(0.px, (-1).px)
		}

		"div.code-toolbar:has(> pre.language-kotlin) > .toolbar > .toolbar-item > span::before" style {
			background(
				Background.of(
					BackgroundImage.of(KOTLIN_LOGO),
					BackgroundRepeat.NoRepeat,
					BackgroundSize.Contain,
					BackgroundPosition.of(CSSPosition.Center),
				)
			)
			maskImage(CSSImage.None)
		}

		"div.code-toolbar:has(> pre.language-json) > .toolbar > .toolbar-item > span::before" style {
			backgroundColor(classColor)
			maskImage(Icons.svg(Icons.BRACES))
		}

		"div.code-toolbar:has(> pre.language-mcfunction) > .toolbar > .toolbar-item > span::before" style {
			backgroundColor(stringColor)
			maskImage(Icons.svg(Icons.SQUARE_TERMINAL))
		}

		"div.code-toolbar:has(> pre.language-xml) > .toolbar > .toolbar-item > span::before" style {
			backgroundColor(propertyColor)
			maskImage(Icons.svg(Icons.CODE_XML))
		}

		"div.code-toolbar:has(> pre.language-bash) > .toolbar > .toolbar-item > span::before" style {
			maskImage(Icons.svg(Icons.TERMINAL))
		}

		"$TOOLBAR_ITEM > button::before" style {
			maskImage(Icons.svg(Icons.COPY))
		}

		"$TOOLBAR_ITEM > button[data-copy-state='copy-success']::before" style {
			maskImage(Icons.svg(Icons.CHECK))
		}

		"$TOOLBAR_ITEM > button" style {
			backgroundColor(Colors.Transparent)
			border(0.px)
			borderRadius(0.4.cssRem)
			color(GlobalStyle.altTextColor)
			cursor(Cursor.Pointer)
			font("inherit")
			padding(0.2.cssRem, 0.55.cssRem)
			transitions {
				defaultDelay(0.15.s)
				properties("background-color", "color")
			}
		}

		"$TOOLBAR_ITEM > button:hover, $TOOLBAR_ITEM > button:focus-visible" style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
			color(GlobalStyle.textColor)
		}

		"$TOOLBAR_ITEM > button[data-copy-state='copy-success']" style {
			color(stringColor)
		}

		"pre[class*='language-'].line-numbers" style {
			counterReset("linenumber")
			paddingLeft(3.cssRem)
			position(Position.Relative)
		}

		"pre[class*='language-'].line-numbers > code" style {
			position(Position.Relative)
			whiteSpace("inherit")
		}

		".line-numbers .line-numbers-rows" style {
			borderRight(1.px, LineStyle.Solid, chromeBorderColor)
			left((-3).cssRem)
			pointerEvents(PointerEvents.None)
			position(Position.Absolute)
			top(0.px)
			userSelect(UserSelect.None)
			width(2.45.cssRem)
		}

		".line-numbers-rows > span" style {
			counterIncrement("linenumber")
			display(DisplayStyle.Block)
		}

		".line-numbers-rows > span::before" style {
			color(lineNumberColor)
			content(counter("linenumber"))
			display(DisplayStyle.Block)
			paddingRight(0.65.cssRem)
			textAlign(TextAlign.Right)
		}

		scope("comment") style {
			color(commentColor)
			fontStyle(FontStyle.Italic)
		}

		scope("boolean", "named-argument", "number", "uuid") style {
			color(numberColor)
		}

		scope("string", "char", "regex") style {
			color(stringColor)
		}

		scope("keyword") style {
			color(keywordColor)
			fontStyle(FontStyle.Italic)
		}

		scope("annotation") style {
			color(keywordColor)
		}

		scope("escape", "operator", "punctuation") style {
			color(punctuationColor)
		}

		scope("function") style {
			color(functionColor)
		}

		/** Kotlin calls, italic like IntelliJ draws top-level and extension calls, which make up most Kore code. */
		scope("call") style {
			color(functionColor)
			fontStyle(FontStyle.Italic)
		}

		scope("implicit-parameter") style {
			color(numberColor)
			fontWeight(700)
		}

		scope("class-name") style {
			color(classColor)
		}

		scope("property", "variable") style {
			color(propertyColor)
		}

		scope("constant") style {
			color(textColor)
			fontStyle(FontStyle.Italic)
		}
	}
}
