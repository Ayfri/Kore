@file:OptIn(ExperimentalComposeWebApi::class)

package io.github.ayfri.kore.website.components.playground

import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.CodeThemeStyle
import io.github.ayfri.kore.website.utils.lgMax
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto

/**
 * The playground's IDE frame, on top of the landing variables `HomePageStyle.page` defines (`--landing-*`).
 *
 * Chrome (bars, sidebar, tab strips) sits on [chrome], code on [surface], the color Monaco's theme paints, so the
 * editors read as documents inside a darker shell.
 */
object PlaygroundStyle : StyleSheet() {
	private const val MONO = "JetBrains Mono"

	private val accent = Color("var(--landing-accent)")
	private val accentStrong = Color("var(--landing-accent-strong)")
	private val border = rgba(151, 176, 202, 0.14)
	private val chrome = Color("#0e131a")
	private val chromeDeep = Color("#0b0f15")
	private val muted = Color("var(--landing-muted)")
	private val raised = Color("#18212c")
	private val surface = Color("var(--landing-surface-2)")
	private val text = Color("var(--landing-text)")

	private val accentBorder = rgba(8, 182, 214, 0.45)
	private val accentWash = rgba(8, 182, 214, 0.1)
	private val accentHover = rgba(8, 182, 214, 0.16)
	private val errorColor = Color("#ff6b7f")
	private val hoverWash = rgba(255, 255, 255, 0.05)
	private val infoColor = CodeThemeStyle.functionColor
	private val successColor = CodeThemeStyle.stringColor
	private val warningColor = CodeThemeStyle.classColor

	/** Height of every tab strip, which the rebuild hairline sits right under. */
	private val TAB_STRIP_HEIGHT = 2.1.cssRem

	val spin by keyframes {
		from { transform { rotate(0.deg) } }
		to { transform { rotate(360.deg) } }
	}

	/** A bar with no known fraction still has to look alive, so it sweeps instead of sitting still. */
	val sweep by keyframes {
		from { property("margin-left", "-40%") }
		to { property("margin-left", "100%") }
	}

	val pulse by keyframes {
		from { opacity(1) }
		to { opacity(0.35) }
	}

	val rise by keyframes {
		from {
			opacity(0)
			transform { translateY(0.5.cssRem) }
		}

		to {
			opacity(1)
			transform { translateY(0.px) }
		}
	}

	// Frame

	/** Drops the bottom padding `HomePageStyle.page` gives the landing pages, so the frame runs edge to edge down to the footer. */
	val page by style {
		padding(0.px)
	}

	/** The site header is 4.5rem plus its 1px border; the frame fills what is left of the viewport. */
	val ide by style {
		backgroundColor(chrome)
		borderBottom(1.px, LineStyle.Solid, border)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		fontSize(0.8.cssRem)
		property("height", "calc(100dvh - 4.5rem - 1px)")
		minHeight(34.cssRem)
		overflow(Overflow.Hidden)
		position(Position.Relative)

		lgMax(self) {
			property("height", "auto")
			minHeight(0.px)
		}

		"button" style {
			fontFamily("inherit")
		}

		"svg" style {
			flexShrink(0)
		}
	}

	val ideFocus by style {
		property("border", "0")
		height(100.dvh)
		property("inset", "0")
		position(Position.Fixed)
		zIndex(70)

		lgMax(self) {
			height(100.dvh)
			overflowY(Overflow.Auto)
		}
	}

	val resizingColumns by style {
		property("cursor", "col-resize")
		userSelect(UserSelect.None)
	}

	val resizingRows by style {
		property("cursor", "row-resize")
		userSelect(UserSelect.None)
	}

	val ideBody by style {
		display(DisplayStyle.Flex)
		flexGrow(1)
		minHeight(0.px)
		position(Position.Relative)
	}

	val mainColumn by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		flexGrow(1)
		minHeight(0.px)
		minWidth(0.px)
	}

	val workspace by style {
		display(DisplayStyle.Grid)
		flexGrow(1)
		gridTemplateColumns("minmax(12rem, var(--playground-split, 1.2fr)) auto minmax(12rem, 1fr)")
		minHeight(0.px)

		lgMax(self) {
			flexGrow(0)
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	/** A hairline to the eye, a wider strip to the pointer. */
	val splitter by style {
		property("cursor", "col-resize")
		position(Position.Relative)
		property("touch-action", "none")
		width(5.px)
		zIndex(2)

		self + before style {
			property("background-color", "var(--playground-splitter-color, rgba(151, 176, 202, 0.14))")
			bottom(0.px)
			property("content", "''")
			left(2.px)
			position(Position.Absolute)
			top(0.px)
			transition(0.15.s, "background-color", "width", "left")
			width(1.px)
		}

		hover(self) style {
			property("--playground-splitter-color", "var(--landing-accent)")
		}

		lgMax(self) {
			display(DisplayStyle.None)
		}
	}

	val splitterActive by style {
		property("--playground-splitter-color", "var(--landing-accent-strong)")
	}

	val panelResizer by style {
		flexShrink(0)
		height(5.px)
		property("cursor", "row-resize")
		position(Position.Relative)
		property("touch-action", "none")

		self + before style {
			property("background-color", "var(--playground-splitter-color, rgba(151, 176, 202, 0.14))")
			property("content", "''")
			height(1.px)
			left(0.px)
			position(Position.Absolute)
			right(0.px)
			top(2.px)
			transition(0.15.s, "background-color")
		}

		hover(self) style {
			property("--playground-splitter-color", "var(--landing-accent)")
		}

		lgMax(self) {
			display(DisplayStyle.None)
		}
	}

	val panelSlot by style {
		flexShrink(0)
		minHeight(0.px)

		lgMax(self) {
			borderTop(1.px, LineStyle.Solid, border)
			property("height", "16rem", important = true)
		}
	}

	val hiddenInput by style {
		display(DisplayStyle.None)
	}

	val srOnly by style {
		property("clip", "rect(0 0 0 0)")
		height(1.px)
		overflow(Overflow.Hidden)
		position(Position.Absolute)
		whiteSpace(WhiteSpace.NoWrap)
		width(1.px)
	}

	// Controls shared by every bar

	val kbd by style {
		alignItems(AlignItems.Center)
		backgroundColor(rgba(255, 255, 255, 0.07))
		border(1.px, LineStyle.Solid, rgba(255, 255, 255, 0.12))
		property("border-bottom-width", "2px")
		borderRadius(0.3.cssRem)
		boxSizing(BoxSizing.BorderBox)
		color(text)
		property("display", "inline-flex")
		fontFamily(MONO, "monospace")
		fontSize(0.66.cssRem)
		height(1.25.cssRem)
		justifyContent(JustifyContent.Center)
		lineHeight(1.number)
		minWidth(1.25.cssRem)
		padding(0.px, 0.3.cssRem)
	}

	val keys by style {
		alignItems(AlignItems.Center)
		property("display", "inline-flex")
		gap(0.2.cssRem)
	}

	val kotlinIcon by style {
		color(CodeThemeStyle.keywordColor)
		property("display", "inline-flex")
		fontSize(0.9.cssRem)
	}

	val toolButton by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(0.px)
		borderRadius(0.4.cssRem)
		color(muted)
		cursor(Cursor.Pointer)
		property("display", "inline-flex")
		flexShrink(0)
		height(1.75.cssRem)
		justifyContent(JustifyContent.Center)
		padding(0.px)
		position(Position.Relative)
		textDecoration("none")
		transition(0.15.s, "background-color", "color")
		width(1.75.cssRem)

		"svg" style {
			height(1.cssRem)
			width(1.cssRem)
		}

		hover(self) style {
			backgroundColor(hoverWash)
			color(text)
		}

		self + disabled style {
			cursor(Cursor.Default)
			opacity(0.35)
		}

		(self + disabled + hover) style {
			backgroundColor(Color.transparent)
			color(muted)
		}
	}

	val toolButtonActive by style {
		backgroundColor(accentWash)
		color(accentStrong)

		hover(self) style {
			backgroundColor(accentHover)
			color(accentStrong)
		}
	}

	val toolButtonLabelled by style {
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)
		gap(0.35.cssRem)
		padding(0.px, 0.5.cssRem)
		width(auto)
	}

	val toolSeparator by style {
		backgroundColor(border)
		flexShrink(0)
		height(1.1.cssRem)
		margin(0.px, 0.25.cssRem)
		width(1.px)

		mdMax(self) {
			display(DisplayStyle.None)
		}
	}

	val textButton by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(1.px, LineStyle.Solid, border)
		borderRadius(0.45.cssRem)
		color(muted)
		cursor(Cursor.Pointer)
		property("display", "inline-flex")
		fontSize(0.76.cssRem)
		gap(0.4.cssRem)
		padding(0.35.cssRem, 0.7.cssRem)
		transition(0.15.s, "border-color", "color")

		"svg" style {
			height(0.9.cssRem)
			width(0.9.cssRem)
		}

		hover(self) style {
			borderColor(accentBorder)
			color(text)
		}
	}

	val dirtyDot by style {
		backgroundColor(text)
		borderRadius(50.percent)
		display(DisplayStyle.InlineBlock)
		flexShrink(0)
		height(0.45.cssRem)
		opacity(0.75)
		position(Position.Relative)
		width(0.45.cssRem)
	}

	val spinnerSmall by style {
		animation(spin) {
			duration(0.8.s)
			timingFunction(AnimationTimingFunction.Linear)
			iterationCount(null)
		}

		border(2.px, LineStyle.Solid, rgba(255, 255, 255, 0.15))
		borderRadius(50.percent)
		boxSizing(BoxSizing.BorderBox)
		property("border-top-color", "currentColor")
		display(DisplayStyle.InlineBlock)
		flexShrink(0)
		height(0.8.cssRem)
		width(0.8.cssRem)
	}

	val searchBox by style {
		alignItems(AlignItems.Center)
		backgroundColor(rgba(255, 255, 255, 0.04))
		border(1.px, LineStyle.Solid, border)
		borderRadius(0.45.cssRem)
		color(muted)
		display(DisplayStyle.Flex)
		flexShrink(0)
		gap(0.45.cssRem)
		height(1.9.cssRem)
		margin(0.1.cssRem, 0.6.cssRem, 0.5.cssRem)
		padding(0.px, 0.55.cssRem)
		transition(0.15.s, "border-color")

		"svg" style {
			height(0.85.cssRem)
			width(0.85.cssRem)
		}

		(self + ":focus-within") style {
			borderColor(accentBorder)
		}
	}

	val searchBoxCompact by style {
		height(1.75.cssRem)
		margin(0.px, 0.45.cssRem, 0.4.cssRem)
	}

	val searchInput by style {
		backgroundColor(Color.transparent)
		border(0.px)
		color(text)
		flexGrow(1)
		fontFamily("inherit")
		fontSize(0.78.cssRem)
		minWidth(0.px)
		property("outline", "none")
	}

	// Title bar

	val titleBar by style {
		alignItems(AlignItems.Center)
		backgroundColor(chromeDeep)
		borderBottom(1.px, LineStyle.Solid, border)
		display(DisplayStyle.Flex)
		flexShrink(0)
		gap(0.75.cssRem)
		height(2.9.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		padding(0.px, 0.5.cssRem, 0.px, 0.8.cssRem)
	}

	val titleGroup by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.3.cssRem)
		minWidth(0.px)
	}

	val titleLogo by style {
		height(1.3.cssRem)
		marginRight(0.35.cssRem)
	}

	val titleName by style {
		fontSize(0.92.cssRem)
		fontWeight(600)
		letterSpacing((-0.2).px)
		margin(0.px)
		whiteSpace(WhiteSpace.NoWrap)

		smMax(self) {
			display(DisplayStyle.None)
		}
	}

	val titleSlash by style {
		color(muted)
		margin(0.px, 0.2.cssRem)
		opacity(0.45)

		smMax(self) {
			display(DisplayStyle.None)
		}
	}

	val titleExample by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(1.px, LineStyle.Solid, Color.transparent)
		borderRadius(0.45.cssRem)
		color(text)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontFamily(MONO, "monospace")
		fontSize(0.76.cssRem)
		gap(0.45.cssRem)
		minWidth(0.px)
		padding(0.25.cssRem, 0.5.cssRem)
		transition(0.15.s, "background-color", "border-color")

		"svg" style {
			color(muted)
			height(0.85.cssRem)
			width(0.85.cssRem)
		}

		hover(self) style {
			backgroundColor(hoverWash)
			borderColor(border)
		}
	}

	val titleExampleName by style {
		overflow(Overflow.Hidden)
		property("text-overflow", "ellipsis")
		whiteSpace(WhiteSpace.NoWrap)
	}

	val runButton by style {
		alignItems(AlignItems.Center)
		backgroundColor(accent)
		border(0.px)
		borderRadius(0.5.cssRem)
		color(Color("#03161c"))
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontSize(0.8.cssRem)
		fontWeight(600)
		gap(0.45.cssRem)
		height(1.9.cssRem)
		padding(0.px, 0.55.cssRem, 0.px, 0.7.cssRem)
		transition(0.15.s, "background-color", "opacity")

		"svg" style {
			property("fill", "currentColor")
			height(0.85.cssRem)
			width(0.85.cssRem)
		}

		".$kbd" style {
			backgroundColor(rgba(0, 0, 0, 0.14))
			borderColor(rgba(0, 0, 0, 0.2))
			color(Color("#03161c"))
		}

		hover(self) style {
			backgroundColor(accentStrong)
		}

		self + disabled style {
			cursor(Cursor.Default)
			opacity(0.5)
		}

		(self + disabled + hover) style {
			backgroundColor(accent)
		}
	}

	val liveChip by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(1.px, LineStyle.Solid, border)
		borderRadius(999.px)
		color(muted)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)
		gap(0.3.cssRem)
		height(1.9.cssRem)
		padding(0.px, 0.65.cssRem)
		transition(0.15.s, "background-color", "border-color", "color")

		"svg" style {
			height(0.85.cssRem)
			width(0.85.cssRem)
		}

		hover(self) style {
			color(text)
		}
	}

	val liveChipOn by style {
		backgroundColor(accentWash)
		borderColor(accentBorder)
		color(accentStrong)

		hover(self) style {
			color(accentStrong)
		}
	}

	// Activity bar and sidebar

	val activityBar by style {
		alignItems(AlignItems.Center)
		backgroundColor(chromeDeep)
		borderRight(1.px, LineStyle.Solid, border)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		flexShrink(0)
		justifyContent(JustifyContent.SpaceBetween)
		padding(0.4.cssRem, 0.px)
		width(2.9.cssRem)

		mdMax(self) {
			display(DisplayStyle.None)
		}
	}

	val activityGroup by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.25.cssRem)
	}

	/** The active view gets the accent bar on the frame's edge, like an IDE activity bar. */
	val activityButton by style {
		height(2.3.cssRem)
		width(2.3.cssRem)

		"svg" style {
			height(1.2.cssRem)
			width(1.2.cssRem)
		}

		(self + ".${toolButtonActive}") style {
			backgroundColor(Color.transparent)
			color(text)
		}

		(self + ".${toolButtonActive}" + before) style {
			backgroundColor(accentStrong)
			borderRadius(0.px, 2.px, 2.px, 0.px)
			property("content", "''")
			height(1.3.cssRem)
			left((-0.3).cssRem)
			position(Position.Absolute)
			width(2.px)
		}
	}

	val activityBadge by style {
		alignItems(AlignItems.Center)
		backgroundColor(errorColor)
		borderRadius(999.px)
		boxSizing(BoxSizing.BorderBox)
		color(Color("#1d0a0e"))
		display(DisplayStyle.Flex)
		fontSize(0.58.cssRem)
		fontWeight(700)
		height(0.95.cssRem)
		justifyContent(JustifyContent.Center)
		minWidth(0.95.cssRem)
		padding(0.px, 0.2.cssRem)
		position(Position.Absolute)
		right(0.1.cssRem)
		top(0.1.cssRem)
	}

	/** Holds two icons and shows the second while hovered or focused, like the docs link turning into "opens in a new tab". */
	val iconSwap by style {
		"span" style {
			display(DisplayStyle.Flex)
		}

		"span:last-child" style {
			display(DisplayStyle.None)
		}

		(self + ":is(:hover, :focus-visible) span:first-child") style {
			display(DisplayStyle.None)
		}

		(self + ":is(:hover, :focus-visible) span:last-child") style {
			display(DisplayStyle.Flex)
		}
	}

	val sidePanel by style {
		backgroundColor(chrome)
		borderRight(1.px, LineStyle.Solid, border)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		flexShrink(0)
		minHeight(0.px)
		width(17.5.cssRem)

		lgMax(self) {
			bottom(0.px)
			property("box-shadow", "1rem 0 2.5rem rgba(0, 0, 0, 0.5)")
			left(2.9.cssRem)
			position(Position.Absolute)
			top(0.px)
			zIndex(21)
		}

		mdMax(self) {
			left(0.px)
			maxWidth(85.vw)
		}
	}

	val sideScrim by style {
		display(DisplayStyle.None)

		lgMax(self) {
			backgroundColor(rgba(4, 8, 12, 0.55))
			display(DisplayStyle.Block)
			property("inset", "0")
			position(Position.Absolute)
			zIndex(20)
		}
	}

	val sectionHeader by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexShrink(0)
		gap(0.5.cssRem)
		height(TAB_STRIP_HEIGHT)
		justifyContent(JustifyContent.SpaceBetween)
		padding(0.px, 0.35.cssRem, 0.px, 0.85.cssRem)
	}

	val sectionTitle by style {
		alignItems(AlignItems.Center)
		color(muted)
		display(DisplayStyle.Flex)
		fontFamily(MONO, "monospace")
		fontSize(0.66.cssRem)
		fontWeight(600)
		gap(0.45.cssRem)
		letterSpacing(1.3.px)
		textTransform(TextTransform.Uppercase)
	}

	val sectionActions by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexShrink(0)
		gap(0.1.cssRem)
	}

	val sideScroll by style {
		flexGrow(1)
		minHeight(0.px)
		overflowY(Overflow.Auto)
		padding(0.px, 0.45.cssRem, 0.6.cssRem)
	}

	val sideCategory by style {
		alignItems(AlignItems.Center)
		color(muted)
		display(DisplayStyle.Flex)
		fontFamily(MONO, "monospace")
		fontSize(0.63.cssRem)
		gap(0.45.cssRem)
		letterSpacing(1.4.px)
		padding(0.75.cssRem, 0.5.cssRem, 0.3.cssRem)
		textTransform(TextTransform.Uppercase)

		"svg" style {
			height(0.8.cssRem)
			width(0.8.cssRem)
		}
	}

	val sideEmpty by style {
		color(muted)
		fontSize(0.76.cssRem)
		padding(0.9.cssRem)
		textAlign(TextAlign.Center)
	}

	val sideFooter by style {
		borderTop(1.px, LineStyle.Solid, border)
		color(muted)
		flexShrink(0)
		fontSize(0.72.cssRem)
		lineHeight(1.5.number)
		padding(0.65.cssRem, 0.85.cssRem)
	}

	val treeCount by style {
		backgroundColor(rgba(255, 255, 255, 0.06))
		borderRadius(999.px)
		color(muted)
		flexShrink(0)
		fontFamily(MONO, "monospace")
		fontSize(0.62.cssRem)
		letterSpacing(0.px)
		property("margin-left", "auto")
		padding(0.05.cssRem, 0.4.cssRem)
	}

	val exampleEntry by style {
		alignItems(AlignItems.Baseline)
		backgroundColor(Color.transparent)
		border(0.px)
		borderRadius(0.45.cssRem)
		color(text)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		gap(0.7.cssRem)
		padding(0.5.cssRem, 0.6.cssRem)
		textAlign(TextAlign.Left)
		transition(0.15.s, "background-color")
		width(100.percent)

		hover(self) style {
			backgroundColor(hoverWash)
		}
	}

	/** Its place in the category, which reads as a progression from the basics to the gameplay examples. */
	val exampleIndex by style {
		color(muted)
		flexShrink(0)
		fontFamily(MONO, "monospace")
		fontSize(0.66.cssRem)
		property("font-variant-numeric", "tabular-nums")
		opacity(0.6)
		transition(0.15.s, "color", "opacity")
	}

	val exampleText by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.15.cssRem)
		minWidth(0.px)
	}

	val exampleTitle by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		fontSize(0.82.cssRem)
		fontWeight(600)
		gap(0.4.cssRem)
		transition(0.15.s, "color")
	}

	val exampleDescription by style {
		color(muted)
		property("display", "-webkit-box")
		fontSize(0.73.cssRem)
		property("-webkit-box-orient", "vertical")
		property("-webkit-line-clamp", "2")
		lineHeight(1.45.number)
		overflow(Overflow.Hidden)
	}

	/** The open example is told apart by its accent title and number on a neutral wash, the row itself stays flat. */
	val exampleEntryActive by style {
		backgroundColor(rgba(255, 255, 255, 0.055))

		".$exampleIndex" style {
			color(accentStrong)
			opacity(1)
		}

		".$exampleTitle" style {
			color(accentStrong)
		}

		hover(self) style {
			backgroundColor(rgba(255, 255, 255, 0.075))
		}
	}

	val settingRow by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(0.px)
		borderRadius(0.5.cssRem)
		color(text)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		gap(0.8.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		padding(0.5.cssRem, 0.6.cssRem)
		textAlign(TextAlign.Left)
		transition(0.15.s, "background-color")
		width(100.percent)

		hover(self) style {
			backgroundColor(hoverWash)
		}
	}

	val settingRowStatic by style {
		cursor(Cursor.Default)

		hover(self) style {
			backgroundColor(Color.transparent)
		}
	}

	val settingText by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.12.cssRem)
		minWidth(0.px)
	}

	val settingLabel by style {
		fontSize(0.8.cssRem)
		fontWeight(500)
	}

	val settingDescription by style {
		color(muted)
		fontSize(0.7.cssRem)
		lineHeight(1.4.number)
	}

	val switchTrack by style {
		backgroundColor(rgba(255, 255, 255, 0.12))
		borderRadius(999.px)
		flexShrink(0)
		height(1.05.cssRem)
		position(Position.Relative)
		transition(0.2.s, "background-color")
		width(1.9.cssRem)
	}

	val switchTrackOn by style {
		backgroundColor(accent)

		"span" style {
			transform { translateX(0.85.cssRem) }
		}
	}

	val switchThumb by style {
		backgroundColor(Color("#f7f9fc"))
		borderRadius(50.percent)
		property("box-shadow", "0 1px 3px rgba(0, 0, 0, 0.4)")
		property("height", "calc(1.05rem - 4px)")
		left(2.px)
		position(Position.Absolute)
		top(2.px)
		transition(0.2.s, "transform")
		property("width", "calc(1.05rem - 4px)")
	}

	val stepper by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.2.cssRem)
	}

	val stepperValue by style {
		fontFamily(MONO, "monospace")
		fontSize(0.76.cssRem)
		minWidth(2.6.cssRem)
		textAlign(TextAlign.Center)
	}

	// Panes and tabs

	val pane by style {
		backgroundColor(surface)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		minHeight(0.px)
		minWidth(0.px)
		overflow(Overflow.Hidden)
		position(Position.Relative)

		lgMax(self) {
			borderBottom(1.px, LineStyle.Solid, border)
			height(62.vh)
		}
	}

	val paneMaximized by style {
		height(100.dvh)
		property("inset", "0")
		maxHeight(100.dvh)
		position(Position.Fixed)
		zIndex(80)

		lgMax(self) {
			height(100.dvh)
		}
	}

	/** The bottom rule is an inset shadow, painted under the tabs, so the active tab covers it without overflowing the strip. */
	val tabStrip by style {
		alignItems(AlignItems.Stretch)
		backgroundColor(chrome)
		property("box-shadow", "inset 0 -1px 0 $border")
		display(DisplayStyle.Flex)
		flexShrink(0)
		gap(0.5.cssRem)
		height(TAB_STRIP_HEIGHT)
		justifyContent(JustifyContent.SpaceBetween)
		minWidth(0.px)
		paddingRight(0.3.cssRem)
	}

	val tabGroup by style {
		alignItems(AlignItems.Stretch)
		display(DisplayStyle.Flex)
		minWidth(0.px)
		overflowX(Overflow.Auto)
		overflowY(Overflow.Hidden)
	}

	val tab by style {
		alignItems(AlignItems.Center)
		borderRight(1.px, LineStyle.Solid, border)
		color(muted)
		display(DisplayStyle.Flex)
		flexShrink(0)
		fontFamily(MONO, "monospace")
		fontSize(0.75.cssRem)
		gap(0.45.cssRem)
		padding(0.px, 0.9.cssRem)
		position(Position.Relative)
		whiteSpace(WhiteSpace.NoWrap)

		"svg" style {
			height(0.9.cssRem)
			width(0.9.cssRem)
		}
	}

	/** Painted in the pane's color over the strip's bottom rule, so the tab reads as the pane's own top. */
	val tabActive by style {
		backgroundColor(surface)
		property("box-shadow", "inset 0 2px 0 var(--landing-accent)")
		color(text)
	}

	val tabButton by style {
		backgroundColor(Color.transparent)
		borderBottom(0.px)
		borderLeft(0.px)
		borderTop(0.px)
		cursor(Cursor.Pointer)
		transition(0.15.s, "color", "background-color")

		hover(self) style {
			color(text)
		}
	}

	val tabActions by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexShrink(0)
		gap(0.1.cssRem)
	}

	val tabMeta by style {
		color(muted)
		fontFamily(MONO, "monospace")
		fontSize(0.66.cssRem)
		opacity(0.75)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val tabErrors by style {
		alignItems(AlignItems.Center)
		backgroundColor(errorColor)
		borderRadius(999.px)
		color(Color("#1d0a0e"))
		display(DisplayStyle.Flex)
		fontSize(0.6.cssRem)
		fontWeight(700)
		height(1.cssRem)
		justifyContent(JustifyContent.Center)
		minWidth(1.cssRem)
		padding(0.px, 0.25.cssRem)
	}

	val tabCount by style {
		backgroundColor(rgba(255, 255, 255, 0.09))
		borderRadius(999.px)
		color(text)
		fontSize(0.62.cssRem)
		padding(0.05.cssRem, 0.4.cssRem)
	}

	/** Shown over an output built from an older buffer, so a stale pack never passes for the current one. */
	val staleBadge by style {
		alignItems(AlignItems.Center)
		backgroundColor(accentWash)
		border(1.px, LineStyle.Solid, rgba(8, 182, 214, 0.35))
		borderRadius(999.px)
		color(accentStrong)
		display(DisplayStyle.Flex)
		flexShrink(0)
		fontFamily(MONO, "monospace")
		fontSize(0.66.cssRem)
		gap(0.4.cssRem)
		marginRight(0.3.cssRem)
		padding(0.1.cssRem, 0.55.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val staleDot by style {
		backgroundColor(accentStrong)
		borderRadius(50.percent)
		height(0.4.cssRem)
		width(0.4.cssRem)
	}

	val staleDotPulsing by style {
		animation(pulse) {
			duration(0.8.s)
			direction(AnimationDirection.Alternate)
			iterationCount(null)
		}
	}

	val staleBody by style {
		opacity(0.55)
		transition(0.2.s, "opacity")
	}

	val editor by style {
		flexGrow(1)
		minHeight(0.px)
		position(Position.Relative)
		width(100.percent)
	}

	val editorHost by style {
		display(DisplayStyle.Block)
	}

	val editorSurface by style {
		height(100.percent)
		width(100.percent)
	}

	val editorLoading by style {
		alignItems(AlignItems.Center)
		backgroundColor(surface)
		color(muted)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.6.cssRem)
		property("inset", "0")
		justifyContent(JustifyContent.Center)
		position(Position.Absolute)
	}

	// Output

	val outputBody by style {
		display(DisplayStyle.Grid)
		flexGrow(1)
		gridTemplateColumns("min(16rem, 40%) minmax(0, 1fr)")
		gridTemplateRows("minmax(0, 1fr)")
		minHeight(0.px)

		smMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
			gridTemplateRows("11rem minmax(0, 1fr)")
		}
	}

	val outputBodyPreviewOnly by style {
		gridTemplateColumns("minmax(0, 1fr)")

		smMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
			gridTemplateRows("minmax(0, 1fr)")
		}
	}

	val explorer by style {
		backgroundColor(chrome)
		borderRight(1.px, LineStyle.Solid, border)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		minHeight(0.px)
		minWidth(0.px)

		smMax(self) {
			borderBottom(1.px, LineStyle.Solid, border)
			borderRight(0.px)
		}
	}

	val fileTree by style {
		flexGrow(1)
		minHeight(0.px)
		overflowY(Overflow.Auto)
		padding(0.px, 0.3.cssRem, 0.5.cssRem)
	}

	/** The children of an open folder, with the guide `--tree-guide` places under the folder's chevron. */
	val treeGroup by style {
		position(Position.Relative)

		self + before style {
			backgroundColor(border)
			bottom(0.px)
			property("content", "''")
			property("left", "var(--tree-guide)")
			property("pointer-events", "none")
			position(Position.Absolute)
			top(0.px)
			width(1.px)
		}
	}

	val treeRow by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(0.px)
		borderRadius(0.4.cssRem)
		color(muted)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontFamily(MONO, "monospace")
		fontSize(0.76.cssRem)
		gap(0.3.cssRem)
		paddingBottom(0.22.cssRem)
		paddingRight(0.45.cssRem)
		paddingTop(0.22.cssRem)
		textAlign(TextAlign.Left)
		transition(0.15.s, "background-color", "color")
		width(100.percent)

		hover(self) style {
			backgroundColor(hoverWash)
			color(text)
		}

		"svg" style {
			flexShrink(0)
			height(0.9.cssRem)
			width(0.9.cssRem)
		}
	}

	/** Files keep an empty one, so a folder's children all sit one step inside it. */
	val treeChevron by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexShrink(0)
		opacity(0.7)
		width(0.8.cssRem)

		"svg" style {
			height(0.8.cssRem)
			width(0.8.cssRem)
		}
	}

	val treeIcon by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		opacity(0.85)
	}

	val treeFileIcon by style {
		color(accent)
		opacity(1)
	}

	val treeFolder by style {
		fontSize(0.72.cssRem)
	}

	val treeFile by style {
		fontSize(0.76.cssRem)
	}

	val treeFileActive by style {
		backgroundColor(accentHover)
		color(text)

		hover(self) style {
			backgroundColor(accentHover)
		}
	}

	val treeLabel by style {
		overflow(Overflow.Hidden)
		property("text-overflow", "ellipsis")
		whiteSpace(WhiteSpace.NoWrap)
	}

	val previewColumn by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		minHeight(0.px)
		minWidth(0.px)
	}

	val previewHeader by style {
		alignItems(AlignItems.Center)
		borderBottom(1.px, LineStyle.Solid, border)
		display(DisplayStyle.Flex)
		flexShrink(0)
		gap(0.5.cssRem)
		height(2.15.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		padding(0.px, 0.3.cssRem, 0.px, 0.8.cssRem)
	}

	val breadcrumb by style {
		alignItems(AlignItems.Center)
		color(muted)
		display(DisplayStyle.Flex)
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)
		gap(0.2.cssRem)
		minWidth(0.px)
		overflow(Overflow.Hidden)
		whiteSpace(WhiteSpace.NoWrap)

		"svg" style {
			height(0.75.cssRem)
			opacity(0.55)
			width(0.75.cssRem)
		}
	}

	/** The folders give way first, down to nothing, and only then the file name, each ending in an ellipsis. */
	val breadcrumbDirectory by style {
		flexShrink(1000)
		minWidth(0.px)
		opacity(0.85)
		overflow(Overflow.Hidden)
		property("text-overflow", "ellipsis")
	}

	val breadcrumbFile by style {
		alignItems(AlignItems.Center)
		color(text)
		display(DisplayStyle.Flex)
		gap(0.3.cssRem)
		marginRight(0.4.cssRem)
		minWidth(0.px)

		"svg" style {
			color(accent)
			height(0.85.cssRem)
			opacity(1)
			width(0.85.cssRem)
		}
	}

	val breadcrumbFileName by style {
		minWidth(0.px)
		overflow(Overflow.Hidden)
		property("text-overflow", "ellipsis")
	}

	val preview by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		flexGrow(1)
		minHeight(0.px)
		overflow(Overflow.Auto)

		// The header above already shows the path and holds copy, so Prism's own chrome would only repeat it.
		"div.code-toolbar" style {
			backgroundColor(Color.transparent)
			border(0.px)
			borderRadius(0.px)
			flexGrow(1)
			minWidth(100.percent)
			overflow(Overflow.Visible)
			property("width", "max-content")
		}

		"div.code-toolbar > .toolbar" style {
			display(DisplayStyle.None)
		}

		// Scrolling belongs to the pane, not to the code box, so the bar sits at the bottom edge either way.
		// Grouped in `:is`: a nested selector list only gets the class on its first entry, the others would apply page-wide.
		":is(pre, pre[class*=\"language-\"])" style {
			backgroundColor(Color.transparent)
			boxSizing(BoxSizing.BorderBox)
			flexGrow(1)
			fontSize(0.8.cssRem)
			margin(0.px)
			minWidth(100.percent)
			overflow(Overflow.Visible)
			padding(0.7.cssRem, 1.cssRem, 0.7.cssRem, 3.4.cssRem)
		}
	}

	val previewWrapped by style {
		"div.code-toolbar" style {
			property("width", "100%")
		}

		":is(pre, pre[class*=\"language-\"], pre > code)" style {
			property("overflow-wrap", "anywhere")
			whiteSpace(WhiteSpace.PreWrap)
		}
	}

	val stateBox by style {
		alignItems(AlignItems.Center)
		color(muted)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		flexGrow(1)
		gap(0.65.cssRem)
		justifyContent(JustifyContent.Center)
		overflowY(Overflow.Auto)
		padding(1.5.cssRem)
		textAlign(TextAlign.Center)
	}

	val stateIcon by style {
		color(muted)
		display(DisplayStyle.Flex)
		opacity(0.7)

		"svg" style {
			height(1.9.cssRem)
			width(1.9.cssRem)
		}
	}

	val stateIconError by style {
		color(errorColor)
		opacity(1)
	}

	val stateTitle by style {
		color(text)
		fontSize(0.98.cssRem)
		fontWeight(600)
	}

	val stateDetail by style {
		fontSize(0.84.cssRem)
		lineHeight(1.55.number)
		maxWidth(36.cssRem)
	}

	val errorText by style {
		backgroundColor(rgba(255, 107, 127, 0.06))
		border(1.px, LineStyle.Solid, rgba(255, 107, 127, 0.25))
		borderRadius(0.6.cssRem)
		boxSizing(BoxSizing.BorderBox)
		color(errorColor)
		fontFamily(MONO, "monospace")
		fontSize(0.76.cssRem)
		maxHeight(16.cssRem)
		overflow(Overflow.Auto)
		padding(0.7.cssRem, 0.9.cssRem)
		textAlign(TextAlign.Left)
		whiteSpace(WhiteSpace.PreWrap)
		width(100.percent)
	}

	val progressTrack by style {
		backgroundColor(rgba(255, 255, 255, 0.06))
		borderRadius(999.px)
		height(0.25.cssRem)
		maxWidth(24.cssRem)
		overflow(Overflow.Hidden)
		width(100.percent)
	}

	/** A hairline under the tab strip while an edit rebuilds in the background, the output below staying usable. */
	val rebuildTrack by style {
		height(2.px)
		left(0.px)
		overflow(Overflow.Hidden)
		position(Position.Absolute)
		right(0.px)
		top(TAB_STRIP_HEIGHT)
		zIndex(1)
	}

	val progressBar by style {
		backgroundColor(accentStrong)
		borderRadius(999.px)
		height(100.percent)
		property("transition", "width 0.25s ease")
	}

	val progressBarPending by style {
		animation(sweep) {
			duration(1.4.s)
			timingFunction(AnimationTimingFunction.EaseInOut)
			iterationCount(null)
		}

		width(40.percent)
	}

	val spinner by style {
		animation(spin) {
			duration(0.9.s)
			timingFunction(AnimationTimingFunction.Linear)
			iterationCount(null)
		}

		border(2.px, LineStyle.Solid, rgba(255, 255, 255, 0.08))
		borderRadius(50.percent)
		property("border-top-color", "var(--landing-accent-strong)")
		height(1.4.cssRem)
		width(1.4.cssRem)
	}

	// Bottom panel

	val bottomPanel by style {
		backgroundColor(surface)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		height(100.percent)
		minHeight(0.px)
	}

	val panelTabs by style {
		backgroundColor(chrome)
	}

	val panelBody by style {
		flexGrow(1)
		fontFamily(MONO, "monospace")
		fontSize(0.74.cssRem)
		minHeight(0.px)
		overflowY(Overflow.Auto)
		padding(0.25.cssRem, 0.px)
	}

	val panelEmpty by style {
		alignItems(AlignItems.Center)
		color(muted)
		display(DisplayStyle.Flex)
		fontFamily("inherit")
		gap(0.45.cssRem)
		padding(0.6.cssRem, 1.cssRem)

		"svg" style {
			height(0.95.cssRem)
			width(0.95.cssRem)
		}
	}

	val panelRow by style {
		alignItems(AlignItems.FlexStart)
		boxSizing(BoxSizing.BorderBox)
		color(text)
		display(DisplayStyle.Flex)
		gap(0.6.cssRem)
		lineHeight(1.5.number)
		padding(0.2.cssRem, 1.cssRem)
		width(100.percent)
	}

	val panelRowButton by style {
		backgroundColor(Color.transparent)
		border(0.px)
		cursor(Cursor.Pointer)
		fontFamily(MONO, "monospace")
		fontSize(0.74.cssRem)
		textAlign(TextAlign.Left)
		transition(0.15.s, "background-color")

		hover(self) style {
			backgroundColor(hoverWash)
		}
	}

	val levelIcon by style {
		display(DisplayStyle.Flex)
		flexShrink(0)
		marginTop(0.15.cssRem)

		"svg" style {
			height(0.85.cssRem)
			width(0.85.cssRem)
		}
	}

	val levelError by style {
		color(errorColor)
	}

	val levelInfo by style {
		color(infoColor)
	}

	val levelSuccess by style {
		color(successColor)
	}

	val levelWarning by style {
		color(warningColor)
	}

	val panelMessage by style {
		columnGap(0.6.cssRem)
		display(DisplayStyle.Flex)
		flexGrow(1)
		flexWrap(FlexWrap.Wrap)
		minWidth(0.px)
		property("overflow-wrap", "anywhere")
		whiteSpace(WhiteSpace.PreWrap)
	}

	val panelMeta by style {
		color(muted)
		flexShrink(0)
		opacity(0.85)
	}

	val logTime by style {
		color(muted)
		flexShrink(0)
		opacity(0.6)
	}

	val panelNote by style {
		color(muted)
		fontFamily("IBM Plex Sans", "sans-serif")
		fontSize(0.76.cssRem)
		padding(0.35.cssRem, 1.cssRem)
	}

	val harness by style {
		"div.code-toolbar" style {
			backgroundColor(Color.transparent)
			border(0.px)
		}

		"div.code-toolbar > .toolbar" style {
			display(DisplayStyle.None)
		}

		":is(pre, pre[class*=\"language-\"])" style {
			backgroundColor(Color.transparent)
			fontSize(0.76.cssRem)
			margin(0.px)
			padding(0.3.cssRem, 1.cssRem, 0.8.cssRem, 3.4.cssRem)
		}
	}

	// Status bar

	val statusBar by style {
		alignItems(AlignItems.Stretch)
		backgroundColor(chromeDeep)
		borderTop(1.px, LineStyle.Solid, border)
		color(muted)
		display(DisplayStyle.Flex)
		flexShrink(0)
		fontFamily(MONO, "monospace")
		fontSize(0.67.cssRem)
		gap(0.5.cssRem)
		height(1.6.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		overflow(Overflow.Hidden)
		padding(0.px, 0.3.cssRem)
	}

	val statusGroup by style {
		alignItems(AlignItems.Stretch)
		display(DisplayStyle.Flex)
		minWidth(0.px)
	}

	val statusItem by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.3.cssRem)
		padding(0.px, 0.5.cssRem)
		whiteSpace(WhiteSpace.NoWrap)

		"svg" style {
			height(0.78.cssRem)
			width(0.78.cssRem)
		}
	}

	val statusItemBusy by style {
		color(accentStrong)
	}

	val statusButton by style {
		backgroundColor(Color.transparent)
		border(0.px)
		color(Color("inherit"))
		cursor(Cursor.Pointer)
		fontFamily(MONO, "monospace")
		fontSize(0.67.cssRem)
		gap(0.55.cssRem)
		transition(0.15.s, "background-color", "color")

		hover(self) style {
			backgroundColor(hoverWash)
			color(text)
		}
	}

	val statusCount by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.25.cssRem)
	}

	val statusLink by style {
		color(muted)
		textDecoration("none")
		transition(0.15.s, "background-color", "color")

		hover(self) style {
			backgroundColor(hoverWash)
			color(text)
		}
	}

	// Overlays

	val toast by style {
		alignItems(AlignItems.Center)
		animation(rise) {
			duration(0.2.s)
			timingFunction(AnimationTimingFunction.EaseOut)
		}

		backgroundColor(raised)
		border(1.px, LineStyle.Solid, rgba(8, 182, 214, 0.35))
		borderRadius(0.6.cssRem)
		bottom(2.4.cssRem)
		property("box-shadow", "0 0.8rem 2rem rgba(0, 0, 0, 0.5)")
		color(text)
		display(DisplayStyle.Flex)
		fontSize(0.8.cssRem)
		gap(0.5.cssRem)
		padding(0.55.cssRem, 0.85.cssRem)
		position(Position.Absolute)
		right(1.cssRem)
		zIndex(60)

		"svg" style {
			color(successColor)
			height(0.95.cssRem)
			width(0.95.cssRem)
		}
	}

	val dialogBackdrop by style {
		property("backdrop-filter", "blur(2px)")
		backgroundColor(rgba(4, 8, 12, 0.6))
		property("inset", "0")
		position(Position.Fixed)
		zIndex(90)
	}

	val dialog by style {
		animation(rise) {
			duration(0.18.s)
			timingFunction(AnimationTimingFunction.EaseOut)
		}

		backgroundColor(Color("#121922"))
		border(1.px, LineStyle.Solid, border)
		borderRadius(0.9.cssRem)
		property("box-shadow", "0 2rem 4rem rgba(0, 0, 0, 0.6)")
		// Centered by auto margins between `left: 0` and `right: 0`: a transform would fight the entry animation.
		property("inset", "9vh 0 auto 0")
		property("margin", "0 auto")
		maxHeight(82.vh)
		overflowY(Overflow.Auto)
		position(Position.Fixed)
		property("width", "min(46rem, calc(100vw - 2rem))")
		zIndex(91)

		"h2" style {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
			fontSize(1.cssRem)
			gap(0.5.cssRem)
			margin(0.px)
		}

		"h2 svg" style {
			color(accent)
			height(1.1.cssRem)
			width(1.1.cssRem)
		}

		"h3" style {
			color(muted)
			fontFamily(MONO, "monospace")
			fontSize(0.64.cssRem)
			letterSpacing(1.4.px)
			margin(0.3.cssRem, 0.px, 0.45.cssRem)
			textTransform(TextTransform.Uppercase)
		}
	}

	val dialogHeader by style {
		alignItems(AlignItems.Center)
		borderBottom(1.px, LineStyle.Solid, border)
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.SpaceBetween)
		padding(0.75.cssRem, 0.6.cssRem, 0.75.cssRem, 1.1.cssRem)
	}

	val dialogColumns by style {
		columnGap(2.cssRem)
		display(DisplayStyle.Grid)
		gridTemplateColumns("repeat(2, minmax(0, 1fr))")
		padding(0.6.cssRem, 1.1.cssRem)

		smMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val dialogFooter by style {
		borderTop(1.px, LineStyle.Solid, border)
		color(muted)
		fontSize(0.74.cssRem)
		padding(0.6.cssRem, 1.1.cssRem)
	}

	val shortcutRow by style {
		alignItems(AlignItems.Center)
		borderBottom(1.px, LineStyle.Solid, rgba(255, 255, 255, 0.04))
		display(DisplayStyle.Flex)
		fontSize(0.8.cssRem)
		gap(1.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		padding(0.32.cssRem, 0.px)
	}

	val inlineCode by style {
		backgroundColor(rgba(255, 255, 255, 0.06))
		borderRadius(0.3.cssRem)
		fontFamily(MONO, "monospace")
		padding(0.05.cssRem, 0.35.cssRem)
	}

	// Command palette, Monaco's quick input dressed like the IDE's own dialogs

	init {
		/** Monaco sizes the palette to 62% of the editor and pins it with an inline `left`, so its anchor spans the editor to recenter it. */
		".overlayWidgets > div:has(> .quick-input-widget)" style {
			width(100.percent)
		}

		".monaco-editor .quick-input-widget" style {
			borderRadius(0.75.cssRem)
			property("box-shadow", "0 1.25rem 3rem rgba(0, 0, 0, 0.55)")
			fontFamily("IBM Plex Sans", "sans-serif")
			property("left", "50%", important = true)
			overflow(Overflow.Hidden)
			property("top", "0.6rem", important = true)
			transform { translateX((-50).percent) }
			property("width", "min(38rem, calc(100% - 2rem))", important = true)
		}

		".monaco-editor .quick-input-widget .quick-input-header" style {
			padding(0.5.cssRem, 0.5.cssRem, 0.4.cssRem)
		}

		".monaco-editor .quick-input-widget .monaco-inputbox" style {
			borderRadius(0.5.cssRem)
		}

		".monaco-editor .quick-input-widget .monaco-inputbox > .ibwrapper > .input" style {
			fontSize(0.85.cssRem)
			padding(0.4.cssRem, 0.6.cssRem)
		}

		".monaco-editor .quick-input-list .monaco-list-row" style {
			borderRadius(0.4.cssRem)
		}

		".monaco-editor .quick-input-list .monaco-keybinding" style {
			gap(0.2.cssRem)
		}

		".monaco-editor .quick-input-list .monaco-keybinding > .monaco-keybinding-key" style {
			backgroundColor(rgba(255, 255, 255, 0.07))
			border(1.px, LineStyle.Solid, rgba(255, 255, 255, 0.12))
			property("border-bottom-width", "2px")
			borderRadius(0.3.cssRem)
			boxSizing(BoxSizing.BorderBox)
			property("box-shadow", "none")
			color(text)
			fontFamily(MONO, "monospace")
			fontSize(0.66.cssRem)
			height(1.15.cssRem)
			margin(0.px)
			minWidth(1.15.cssRem)
			padding(0.px, 0.3.cssRem)
		}

		".monaco-editor .quick-input-list .monaco-keybinding-key-separator" style {
			display(DisplayStyle.None)
		}
	}

	/**
	 * Dropped on phones: a button's text keeping its icon, or a whole button that has another way in. Declared last, so
	 * it wins over the `display` of the class it is paired with.
	 */
	val wideOnly by style {
		mdMax(self) {
			display(DisplayStyle.None)
		}
	}
}
