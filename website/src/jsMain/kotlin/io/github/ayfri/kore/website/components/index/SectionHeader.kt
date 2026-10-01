package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text

/** The title block every homepage section opens with, so they all read as one page. The home page injects [HomeSectionStyle]. */
@Composable
fun SectionHeader(title: String, subtitle: String? = null, centered: Boolean = true, content: @Composable () -> Unit = {}) {
	Div({
		classes(HomeSectionStyle.header)
		if (centered) classes(HomeSectionStyle.centered)
	}) {
		H2({ classes(HomeSectionStyle.title) }) { Text(title) }
		subtitle?.let { P(it, HomeSectionStyle.subtitle) }
		content()
	}
}

object HomeSectionStyle : StyleSheet() {
	/** The frame of every homepage section after the hero. */
	val section by style {
		boxSizing(BoxSizing.BorderBox)
		marginX(auto)
		maxWidth(80.cssRem)
		padding(4.5.cssRem, 5.vw)
		width(100.percent)

		smMax(self) {
			padding(3.cssRem, 1.1.cssRem)
		}
	}

	val header by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.9.cssRem)
		marginBottom(2.8.cssRem)
	}

	val centered by style {
		alignItems(org.jetbrains.compose.web.css.AlignItems.Center)
		marginX(auto)
		maxWidth(46.cssRem)
		textAlign(TextAlign.Center)
	}

	val title by style {
		fontSize(2.5.cssRem)
		letterSpacing((-0.8).px)
		lineHeight(1.15)
		margin(0.px)
		textWrap(TextWrap.Balance)

		smMax(self) {
			fontSize(1.9.cssRem)
		}
	}

	val subtitle by style {
		color(LandingVars.Muted.value())
		fontSize(1.1.cssRem)
		margin(0.px)
		textWrap(TextWrap.Pretty)
	}
}
