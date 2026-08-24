@file:OptIn(ExperimentalComposeWebApi::class)

package io.github.ayfri.kore.website.components.playground

import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.paddingX
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto

object PlaygroundStyle : StyleSheet() {
	// One family per argument: the DSL quotes whatever it is given, so a pre-joined stack becomes one bogus name.
	private val monoFont = arrayOf("JetBrains Mono", "Fira Code", "Cascadia Code", "Consolas", "monospace")

	private val accentColor = GlobalStyle.linkColor.toString()
	private val tertiaryBackground = GlobalStyle.tertiaryBackgroundColor.toString()

	private val errorColor = Color("#ff6b7f")
	private val warningColor = Color("#ffcb6b")

	val spin by keyframes {
		from {
			transform { rotate(0.deg) }
		}

		to {
			transform { rotate(360.deg) }
		}
	}

	/** A bar with no known fraction still has to look alive, so it sweeps instead of sitting still. */
	val sweep by keyframes {
		from {
			property("margin-left", "-40%")
		}

		to {
			property("margin-left", "100%")
		}
	}

	val container by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		marginX(auto)
		maxWidth(1600.px)
		paddingBottom(2.cssRem)
		paddingTop(1.5.cssRem)
		paddingX(4.percent)
		width(100.percent)

		mdMax(self) {
			paddingX(3.percent)
		}
	}

	val hero by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Baseline)
		flexWrap(FlexWrap.Wrap)
		gap(0.75.cssRem)
	}

	val pageTitle by style {
		fontSize(2.cssRem)
		fontWeight(700)
		margin(0.px)
	}

	val description by style {
		color(GlobalStyle.altTextColor)
		fontSize(1.05.cssRem)
		lineHeight(1.5.number)
		margin(0.px)
	}

	val toolbar by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingSection)
		flexWrap(FlexWrap.Wrap)
		gap(0.6.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		padding(0.6.cssRem, 0.8.cssRem)
	}

	val toolbarGroup by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
	}

	val toolbarButton by style {
		fontSize(0.9.cssRem)
		padding(0.45.cssRem, 0.9.cssRem)
	}

	val disabledButton by style {
		cursor(Cursor.NotAllowed)
		opacity(0.45)
	}

	val badge by style {
		backgroundColor(GlobalStyle.backgroundColor)
		border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.altTextColor)
		fontFamily(*monoFont)
		fontSize(0.8.cssRem)
		padding(0.35.cssRem, 0.6.cssRem)
		whiteSpace("nowrap")
	}

	val picker by style {
		position(Position.Relative)
	}

	val pickerBackdrop by style {
		position(Position.Fixed)
		property("inset", "0")
		property("z-index", "40")
	}

	val pickerMenu by style {
		position(Position.Absolute)
		backgroundColor(GlobalStyle.backgroundColor)
		border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingSection)
		property("box-shadow", "0 1rem 2rem ${GlobalStyle.shadowColor}")
		left(0.px)
		maxHeight(24.cssRem)
		marginTop(0.4.cssRem)
		minWidth(20.cssRem)
		overflowY(Overflow.Auto)
		paddingBottom(0.4.cssRem)
		paddingTop(0.4.cssRem)
		top(100.percent)
		property("z-index", "41")
	}

	val pickerCategory by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.7.cssRem)
		fontWeight(700)
		letterSpacing(0.06.cssRem)
		opacity(0.7)
		padding(0.5.cssRem, 0.8.cssRem, 0.25.cssRem)
		textTransform(TextTransform.Uppercase)
	}

	val pickerEntry by style {
		display(DisplayStyle.Flex)
		borderLeft(2.px, LineStyle.Solid, Color.transparent)
		cursor(Cursor.Pointer)
		flexDirection(FlexDirection.Column)
		gap(0.15.cssRem)
		padding(0.4.cssRem, 0.8.cssRem)
		transition(0.2.s, "background-color", "border-color")

		self + hover style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		}
	}

	val pickerEntryActive by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor.alpha(0.6))
		property("border-left-color", GlobalStyle.linkColor)
	}

	val pickerTitle by style {
		color(GlobalStyle.textColor)
		fontSize(0.9.cssRem)
		fontWeight(600)
	}

	val pickerDescription by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.78.cssRem)
		lineHeight(1.35.number)
	}

	val workspace by style {
		display(DisplayStyle.Grid)
		columnGap(0.px)
		rowGap(1.cssRem)
		gridTemplateColumns("minmax(12rem, var(--playground-split, 1.1fr)) auto minmax(12rem, 1fr)")
		property("height", "calc(100dvh - 17rem)")
		minHeight(520.px)

		mdMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
			property("height", "auto")
			minHeight(0.px)
		}
	}

	val splitter by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		property("cursor", "col-resize")
		justifyContent(JustifyContent.Center)
		property("touch-action", "none")
		width(0.6.cssRem)

		self + hover style {
			property("--playground-splitter-color", accentColor)
		}

		self + before style {
			property("background-color", "var(--playground-splitter-color, $tertiaryBackground)")
			borderRadius(999.px)
			property("content", "''")
			height(3.cssRem)
			transition(0.2.s, "background-color")
			width(1.px)
		}

		mdMax(self) {
			display(DisplayStyle.None)
		}
	}

	val splitterActive by style {
		property("--playground-splitter-color", accentColor)
	}

	val pane by style {
		display(DisplayStyle.Flex)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingSection)
		flexDirection(FlexDirection.Column)
		minHeight(0.px)
		overflow(Overflow.Hidden)

		mdMax(self) {
			height(70.vh)
		}
	}

	val paneMaximized by style {
		position(Position.Fixed)
		property("inset", "0")
		borderRadius(0.px)
		height(100.dvh)
		maxHeight(100.dvh)
		property("z-index", "50")

		mdMax(self) {
			height(100.dvh)
		}
	}

	val paneHeader by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		borderBottom(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		flexShrink(0)
		gap(0.75.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		minHeight(2.6.cssRem)
		padding(0.35.cssRem, 0.8.cssRem)
	}

	val paneTitle by style {
		color(GlobalStyle.altTextColor)
		fontFamily(*monoFont)
		fontSize(0.85.cssRem)
	}

	val pathPrefix by style {
		color(GlobalStyle.altTextColor)
		opacity(0.6)
	}

	val paneHint by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.75.cssRem)
		opacity(0.75)
	}

	val paneActions by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(0.4.cssRem)
	}

	val iconButton by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.altTextColor)
		cursor(Cursor.Pointer)
		fontSize(0.78.cssRem)
		gap(0.35.cssRem)
		padding(0.3.cssRem, 0.55.cssRem)
		transition(0.2.s, "background-color", "color", "border-color")

		self + hover style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
			color(GlobalStyle.textColor)
		}
	}

	val iconButtonActive by style {
		property("border-color", GlobalStyle.linkColor)
		color(GlobalStyle.textColor)
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
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		color(GlobalStyle.altTextColor)
		flexDirection(FlexDirection.Column)
		gap(0.6.cssRem)
		property("inset", "0")
		justifyContent(JustifyContent.Center)
		position(Position.Absolute)
	}

	val problems by style {
		backgroundColor(GlobalStyle.backgroundColor)
		borderTop(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		flexShrink(0)
		maxHeight(9.cssRem)
		overflowY(Overflow.Auto)
	}

	val problem by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Baseline)
		backgroundColor(Color.transparent)
		border(0.px)
		color(GlobalStyle.textColor)
		cursor(Cursor.Pointer)
		fontFamily(*monoFont)
		fontSize(0.78.cssRem)
		gap(0.5.cssRem)
		padding(0.35.cssRem, 0.8.cssRem)
		textAlign(TextAlign.Left)
		transition(0.2.s, "background-color")
		width(100.percent)

		self + hover style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		}
	}

	val problemPosition by style {
		color(GlobalStyle.altTextColor)
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
		borderRight(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		minHeight(0.px)
		overflowY(Overflow.Auto)
		paddingBottom(0.4.cssRem)
		paddingTop(0.4.cssRem)
	}

	val treeRow by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(0.px)
		borderLeft(2.px, LineStyle.Solid, Color.transparent)
		color(GlobalStyle.altTextColor)
		cursor(Cursor.Pointer)
		fontFamily(*monoFont)
		fontSize(0.8.cssRem)
		gap(0.35.cssRem)
		paddingBottom(0.22.cssRem)
		paddingRight(0.5.cssRem)
		paddingTop(0.22.cssRem)
		textAlign(TextAlign.Left)
		transition(0.2.s, "background-color", "color", "border-color")
		width(100.percent)

		self + hover style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
			color(GlobalStyle.textColor)
		}

		"svg" style {
			flexShrink(0)
			height(0.95.cssRem)
			width(0.95.cssRem)
		}
	}

	val treeChevron by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		opacity(0.7)
	}

	val treeIcon by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		opacity(0.85)
	}

	val treeFileIcon by style {
		color(GlobalStyle.linkColor)
		opacity(1.0)
	}

	val treeFolder by style {
		fontSize(0.75.cssRem)
		letterSpacing(0.02.cssRem)
	}

	val treeFile by style {
		fontSize(0.8.cssRem)
	}

	val treeFileActive by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor.alpha(0.6))
		property("border-left-color", GlobalStyle.linkColor)
		color(GlobalStyle.textColor)
	}

	val treeLabel by style {
		overflow(Overflow.Hidden)
		property("text-overflow", "ellipsis")
		whiteSpace("nowrap")
	}

	val preview by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		minHeight(0.px)
		overflow(Overflow.Auto)

		// Prism's toolbar plugin wraps the `pre`, so the wrapper is what has to fill the pane - a four-line
		// `pack.mcmeta` otherwise leaves the code box, and the horizontal scrollbar with it, halfway up.
		"div.code-toolbar" style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			flexGrow(1)
			minWidth(100.percent)
			property("width", "max-content")
		}

		// Scrolling belongs to the pane, not to the code box, so the bar sits at the bottom edge either way.
		"pre, pre[class*=\"language-\"]" style {
			backgroundColor(Color.transparent)
			borderRadius(0.px)
			boxSizing(BoxSizing.BorderBox)
			flexGrow(1)
			fontSize(0.82.cssRem)
			margin(0.px)
			minWidth(100.percent)
			overflow(Overflow.Visible)
			padding(0.8.cssRem)
		}
	}

	val stateBox by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		color(GlobalStyle.altTextColor)
		flexDirection(FlexDirection.Column)
		flexGrow(1)
		gap(0.6.cssRem)
		justifyContent(JustifyContent.Center)
		padding(1.5.cssRem)
		textAlign(TextAlign.Center)
	}

	val stateTitle by style {
		color(GlobalStyle.textColor)
		fontSize(1.cssRem)
		fontWeight(600)
	}

	val stateDetail by style {
		fontSize(0.85.cssRem)
		lineHeight(1.5.number)
		maxWidth(38.cssRem)
	}

	val errorText by style {
		color(errorColor)
		fontFamily(*monoFont)
		fontSize(0.8.cssRem)
		maxHeight(14.cssRem)
		overflow(Overflow.Auto)
		textAlign(TextAlign.Left)
		whiteSpace("pre-wrap")
		width(100.percent)
	}

	val statusStrip by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		borderTop(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		color(GlobalStyle.altTextColor)
		flexShrink(0)
		flexWrap(FlexWrap.Wrap)
		fontFamily(*monoFont)
		fontSize(0.75.cssRem)
		gap(0.9.cssRem)
		padding(0.4.cssRem, 0.8.cssRem)
	}

	val progressTrack by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(999.px)
		height(0.25.cssRem)
		maxWidth(24.cssRem)
		overflow(Overflow.Hidden)
		width(100.percent)
	}

	val progressBar by style {
		backgroundColor(GlobalStyle.linkColor)
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

		border(2.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		property("border-top-color", GlobalStyle.linkColor)
		borderRadius(50.percent)
		height(1.4.cssRem)
		width(1.4.cssRem)
	}
}
