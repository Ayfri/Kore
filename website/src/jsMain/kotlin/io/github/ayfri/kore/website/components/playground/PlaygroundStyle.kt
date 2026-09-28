@file:OptIn(ExperimentalComposeWebApi::class)

package io.github.ayfri.kore.website.components.playground

import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.CodeThemeStyle
import io.github.ayfri.kore.website.utils.lgMax
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto

/** Page styles, on top of the landing variables `HomePageStyle.page` defines (`--landing-*`). */
object PlaygroundStyle : StyleSheet() {
	private const val MONO = "JetBrains Mono"

	private val accent = Color("var(--landing-accent)")
	private val accentStrong = Color("var(--landing-accent-strong)")
	private val border = Color("var(--landing-border)")
	private val card = Color("var(--landing-card)")
	private val muted = Color("var(--landing-muted)")
	private val surface = Color("var(--landing-surface-2)")
	private val text = Color("var(--landing-text)")

	private val accentWash = rgba(8, 182, 214, 0.08)
	private val accentHover = rgba(8, 182, 214, 0.14)
	private val errorColor = Color("#ff6b7f")
	private val warningColor = CodeThemeStyle.classColor

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

	val container by style {
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		marginX(auto)
		maxWidth(100.cssRem)
		padding(2.cssRem, 3.vw, 1.cssRem)
		width(100.percent)

		smMax(self) {
			padding(1.5.cssRem, 1.1.cssRem, 1.cssRem)
		}
	}

	val header by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.45.cssRem)

		"h1" style {
			fontSize(2.3.cssRem)
			letterSpacing((-1).px)
			lineHeight(1.1.number)
			margin(0.px)
		}

		"p" style {
			color(muted)
			fontSize(1.02.cssRem)
			margin(0.px)
			maxWidth(46.cssRem)
		}
	}

	val eyebrow by style {
		color(accent)
		fontFamily(MONO, "monospace")
		fontSize(0.78.cssRem)
	}

	val toolbar by style {
		alignItems(AlignItems.Center)
		backgroundColor(card)
		border(1.px, LineStyle.Solid, border)
		borderRadius(1.cssRem)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.6.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		padding(0.55.cssRem, 0.7.cssRem)

		// Descendant specificity: the shared button style is injected after this sheet and would win a tie.
		"button" style {
			fontSize(0.9.cssRem)
			fontWeight(600)
			padding(0.45.cssRem, 0.9.cssRem)
		}
	}

	val toolbarGroup by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
	}

	val disabledButton by style {
		cursor(Cursor.NotAllowed)
		opacity(0.45)
	}

	val shortcut by style {
		color(muted)
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)

		mdMax(self) {
			display(DisplayStyle.None)
		}
	}

	val picker by style {
		position(Position.Relative)
	}

	val pickerBackdrop by style {
		position(Position.Fixed)
		property("inset", "0")
		zIndex(40)
	}

	val pickerMenu by style {
		backgroundColor(card)
		border(1.px, LineStyle.Solid, border)
		borderRadius(1.cssRem)
		property("box-shadow", "0 1.2rem 2.5rem rgba(0, 0, 0, 0.55)")
		left(0.px)
		marginTop(0.4.cssRem)
		maxHeight(28.cssRem)
		minWidth(21.cssRem)
		overflowY(Overflow.Auto)
		padding(0.4.cssRem)
		position(Position.Absolute)
		top(100.percent)
		zIndex(41)
	}

	val pickerCategory by style {
		color(muted)
		fontFamily(MONO, "monospace")
		fontSize(0.68.cssRem)
		letterSpacing(1.5.px)
		padding(0.6.cssRem, 0.7.cssRem, 0.3.cssRem)
		textTransform(TextTransform.Uppercase)
	}

	val pickerEntry by style {
		borderRadius(0.6.cssRem)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.15.cssRem)
		padding(0.45.cssRem, 0.7.cssRem)
		transition(0.2.s, "background-color")

		hover(self) style {
			backgroundColor(accentWash)
		}
	}

	val pickerEntryActive by style {
		backgroundColor(accentHover)

		hover(self) style {
			backgroundColor(accentHover)
		}
	}

	val pickerTitle by style {
		color(text)
		fontSize(0.9.cssRem)
		fontWeight(600)
	}

	val pickerDescription by style {
		color(muted)
		fontSize(0.78.cssRem)
		lineHeight(1.4.number)
	}

	val workspace by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("minmax(12rem, var(--playground-split, 1.1fr)) auto minmax(12rem, 1fr)")
		property("height", "clamp(32rem, calc(100dvh - 15.5rem), 64rem)")
		rowGap(1.cssRem)

		lgMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
			property("height", "auto")
		}
	}

	val splitter by style {
		alignItems(AlignItems.Center)
		property("cursor", "col-resize")
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)
		property("touch-action", "none")
		width(0.8.cssRem)

		hover(self) style {
			property("--playground-splitter-color", "var(--landing-accent)")
		}

		self + before style {
			property("background-color", "var(--playground-splitter-color, var(--landing-border))")
			borderRadius(999.px)
			property("content", "''")
			height(3.cssRem)
			transition(0.2.s, "background-color")
			width(2.px)
		}

		lgMax(self) {
			display(DisplayStyle.None)
		}
	}

	val splitterActive by style {
		property("--playground-splitter-color", "var(--landing-accent-strong)")
	}

	val pane by style {
		backgroundColor(surface)
		border(1.px, LineStyle.Solid, border)
		borderRadius(1.cssRem)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		minHeight(0.px)
		minWidth(0.px)
		overflow(Overflow.Hidden)
		position(Position.Relative)

		lgMax(self) {
			height(70.vh)
		}
	}

	val paneMaximized by style {
		borderRadius(0.px)
		height(100.dvh)
		property("inset", "0")
		maxHeight(100.dvh)
		position(Position.Fixed)
		zIndex(60)

		lgMax(self) {
			height(100.dvh)
		}
	}

	val paneHeader by style {
		alignItems(AlignItems.Center)
		backgroundColor(rgba(255, 255, 255, 0.02))
		borderBottom(1.px, LineStyle.Solid, border)
		display(DisplayStyle.Flex)
		flexShrink(0)
		gap(0.75.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		minHeight(2.7.cssRem)
		padding(0.35.cssRem, 0.6.cssRem, 0.35.cssRem, 0.9.cssRem)
	}

	val paneHeading by style {
		alignItems(AlignItems.Baseline)
		display(DisplayStyle.Flex)
		gap(0.7.cssRem)
		minWidth(0.px)
		overflow(Overflow.Hidden)
	}

	/** The same label the Features page puts over its code panels, so the playground reads as the live version of it. */
	val paneLabel by style {
		color(accent)
		flexShrink(0)
		fontFamily(MONO, "monospace")
		fontSize(0.7.cssRem)
		letterSpacing(1.5.px)
		textTransform(TextTransform.Uppercase)
	}

	/** `min-width: 0` lets the path shrink to its ellipsis, a flex item otherwise keeps its full width and clips the label. */
	val paneTitle by style {
		color(muted)
		fontFamily(MONO, "monospace")
		fontSize(0.8.cssRem)
		minWidth(0.px)
		overflow(Overflow.Hidden)
		property("text-overflow", "ellipsis")
		whiteSpace(WhiteSpace.NoWrap)
	}

	val pathPrefix by style {
		opacity(0.6)
	}

	val paneActions by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexShrink(0)
		gap(0.35.cssRem)
	}

	val iconButton by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(1.px, LineStyle.Solid, border)
		borderRadius(0.5.cssRem)
		color(muted)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontFamily(MONO, "monospace")
		fontSize(0.75.cssRem)
		gap(0.35.cssRem)
		height(1.9.cssRem)
		padding(0.px, 0.55.cssRem)
		transition(0.2.s, "background-color", "color", "border-color")

		hover(self) style {
			backgroundColor(accentHover)
			borderColor(Color("rgba(8, 182, 214, 0.45)"))
			color(text)
		}
	}

	/** Button text that a phone drops, keeping the icon, so the pane header keeps room for its label. */
	val wideOnly by style {
		smMax(self) {
			display(DisplayStyle.None)
		}
	}

	val iconButtonActive by style {
		borderColor(Color("rgba(8, 182, 214, 0.45)"))
		color(accentStrong)
	}

	/** Shown over an output built from an older buffer, so a stale pack never passes for the current one. */
	val staleBadge by style {
		alignItems(AlignItems.Center)
		backgroundColor(accentWash)
		border(1.px, LineStyle.Solid, Color("rgba(8, 182, 214, 0.35)"))
		borderRadius(999.px)
		color(accentStrong)
		display(DisplayStyle.Flex)
		flexShrink(0)
		fontFamily(MONO, "monospace")
		fontSize(0.7.cssRem)
		gap(0.4.cssRem)
		padding(0.15.cssRem, 0.6.cssRem)
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

	val problems by style {
		borderTop(1.px, LineStyle.Solid, border)
		flexShrink(0)
		maxHeight(9.cssRem)
		overflowY(Overflow.Auto)
		padding(0.25.cssRem, 0.px)
	}

	val problem by style {
		alignItems(AlignItems.Baseline)
		backgroundColor(Color.transparent)
		border(0.px)
		color(text)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontFamily(MONO, "monospace")
		fontSize(0.76.cssRem)
		gap(0.6.cssRem)
		padding(0.3.cssRem, 0.9.cssRem)
		textAlign(TextAlign.Left)
		transition(0.2.s, "background-color")
		width(100.percent)

		hover(self) style {
			backgroundColor(accentWash)
		}
	}

	val problemPosition by style {
		color(muted)
		flexShrink(0)
	}

	val problemError by style {
		color(errorColor)
		flexShrink(0)
		fontWeight(700)
	}

	val problemWarning by style {
		color(warningColor)
		flexShrink(0)
		fontWeight(700)
	}

	val outputBody by style {
		display(DisplayStyle.Grid)
		flexGrow(1)
		gridTemplateColumns("minmax(9rem, 15rem) minmax(0, 1fr)")
		gridTemplateRows("minmax(0, 1fr)")
		minHeight(0.px)

		smMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
			gridTemplateRows("8rem minmax(0, 1fr)")
		}
	}

	val fileTree by style {
		borderRight(1.px, LineStyle.Solid, border)
		minHeight(0.px)
		overflowY(Overflow.Auto)
		padding(0.4.cssRem)

		smMax(self) {
			borderBottom(1.px, LineStyle.Solid, border)
			borderRight(0.px)
		}
	}

	val treeRow by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(0.px)
		borderRadius(0.45.cssRem)
		color(muted)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontFamily(MONO, "monospace")
		fontSize(0.78.cssRem)
		gap(0.35.cssRem)
		paddingBottom(0.24.cssRem)
		paddingRight(0.5.cssRem)
		paddingTop(0.24.cssRem)
		textAlign(TextAlign.Left)
		transition(0.2.s, "background-color", "color")
		width(100.percent)

		hover(self) style {
			backgroundColor(accentWash)
			color(text)
		}

		"svg" style {
			flexShrink(0)
			height(0.95.cssRem)
			width(0.95.cssRem)
		}
	}

	val treeChevron by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		opacity(0.7)
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
		fontSize(0.74.cssRem)
	}

	val treeFile by style {
		fontSize(0.78.cssRem)
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

	val preview by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		minHeight(0.px)
		overflow(Overflow.Auto)

		// The pane header already shows the path and holds copy, so Prism's own chrome would only repeat it.
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
		"pre, pre[class*=\"language-\"]" style {
			backgroundColor(Color.transparent)
			boxSizing(BoxSizing.BorderBox)
			flexGrow(1)
			fontSize(0.82.cssRem)
			margin(0.px)
			minWidth(100.percent)
			overflow(Overflow.Visible)
			padding(0.8.cssRem, 1.cssRem)
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
		padding(1.5.cssRem)
		textAlign(TextAlign.Center)
	}

	val stateTitle by style {
		color(text)
		fontSize(1.02.cssRem)
		fontWeight(600)
	}

	val stateDetail by style {
		fontSize(0.86.cssRem)
		lineHeight(1.55.number)
		maxWidth(38.cssRem)
	}

	val errorText by style {
		backgroundColor(rgba(255, 107, 127, 0.06))
		border(1.px, LineStyle.Solid, rgba(255, 107, 127, 0.25))
		borderRadius(0.6.cssRem)
		boxSizing(BoxSizing.BorderBox)
		color(errorColor)
		fontFamily(MONO, "monospace")
		fontSize(0.78.cssRem)
		maxHeight(16.cssRem)
		overflow(Overflow.Auto)
		padding(0.7.cssRem, 0.9.cssRem)
		textAlign(TextAlign.Left)
		whiteSpace(WhiteSpace.PreWrap)
		width(100.percent)
	}

	val statusStrip by style {
		alignItems(AlignItems.Center)
		borderTop(1.px, LineStyle.Solid, border)
		color(muted)
		display(DisplayStyle.Flex)
		flexShrink(0)
		flexWrap(FlexWrap.Wrap)
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)
		gap(0.3.cssRem, 1.cssRem)
		padding(0.45.cssRem, 0.9.cssRem)
	}

	val statusAccent by style {
		color(accentStrong)
	}

	val progressTrack by style {
		backgroundColor(rgba(255, 255, 255, 0.06))
		borderRadius(999.px)
		height(0.25.cssRem)
		maxWidth(24.cssRem)
		overflow(Overflow.Hidden)
		width(100.percent)
	}

	/** A hairline under the pane header while an edit rebuilds in the background, the output below staying usable. */
	val rebuildTrack by style {
		height(2.px)
		left(0.px)
		overflow(Overflow.Hidden)
		position(Position.Absolute)
		right(0.px)
		top(2.7.cssRem)
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
}
