package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text

/** The title block every homepage section opens with, so they all read as one page. */
@Composable
fun SectionHeader(title: String, subtitle: String? = null, centered: Boolean = true, content: @Composable () -> Unit = {}) {
	Style(SectionHeaderStyle)

	Div({
		classes(SectionHeaderStyle.header)
		if (centered) classes(SectionHeaderStyle.centered)
	}) {
		H2({ classes(SectionHeaderStyle.title) }) { Text(title) }
		subtitle?.let { P(it, SectionHeaderStyle.subtitle) }
		content()
	}
}

object SectionHeaderStyle : StyleSheet() {
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
		lineHeight(1.15.number)
		margin(0.px)
		textWrap(TextWrap.Balance)

		smMax(self) {
			fontSize(1.9.cssRem)
		}
	}

	val subtitle by style {
		color(Color("var(--landing-muted)"))
		fontSize(1.1.cssRem)
		margin(0.px)
		textWrap(TextWrap.Pretty)
	}
}
