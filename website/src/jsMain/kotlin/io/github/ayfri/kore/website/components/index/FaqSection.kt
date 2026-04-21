package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle
import com.varabyte.kobweb.silk.components.icons.mdi.MdiChevronRight
import io.github.ayfri.kore.website.components.common.Markdown
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.transition
import io.github.ayfri.kore.website.utils.xsMax
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*

private data class FaqItem(
	val question: String,
	val answer: String,
)

@Composable
fun FaqSection() {
	Style(FaqSectionStyle)

	val questions = listOf(
		FaqItem(
			"How does Kore handle variables, if/else conditions, and loops?",
			"This is the most important concept in Kore. Standard Kotlin variables (`val`/`var`) and loops (`for`/`while`) are evaluated at compile-time to generate your datapack. To handle actual in-game logic at runtime, Kore provides built-in abstractions over Minecraft's scoreboards, command macros, and data storages. You write Kotlin, but Kore correctly maps your logic to Minecraft's execution engine."
		),
		FaqItem(
			"Can I use other datapacks with Kore?",
			"Yes, you can use other datapacks with Kore. By providing [bindings](/docs/advanced/bindings) for your datapacks, you can use them in your Kore code.",
		),
		FaqItem(
			"Does Kore add overhead to the generated datapack?",
			"No. Kore is a **build-time** tool. It acts as a smart generator that compiles your Kotlin code down to raw, native JSON and MCFunction files. There is no 'Kore runtime' or plugin required on your server. The output is exactly what you would have written by hand, just generated faster and without typos."
		),
		FaqItem(
			"Does Kore support command macros and recent Minecraft features?",
			"Yes. Kore is designed to stay up-to-date with recent Minecraft updates. It fully supports command macros introduced in 1.20.2, dialogs and timelines, allowing you to leverage dynamic command execution safely with Kotlin's string interpolation and type safety."
		),
		FaqItem(
			"Is Kore compatible with mods?",
			"By providing bindings for the commands/data-driven features included in your mods, you can use Kore with mods. However, Kore is not compatible with any mod currently.",
		),
		FaqItem(
			"Can I also generate Resource Packs with Kore?",
			"Currently, Kore is focused purely on Datapack generation (logic, worldgen, recipes, advancements, tags, etc.). For assets like textures or models, you will still need to handle a separate Resource Pack, though you can structure your Kotlin project to manage both in the same workspace."
		)
	)
	var openedQuestionIndex by remember { mutableStateOf(0) }

	Section({
		classes(FaqSectionStyle.faqContainer)
	}) {
		Div({
			classes(FaqSectionStyle.faqHeader)
		}) {
			H2 {
				Text("Frequently Asked Questions")
			}
		}

		Div({
			classes(FaqSectionStyle.faq)
		}) {
			questions.forEachIndexed { index, item ->
				val isOpen = openedQuestionIndex == index

				Div({
					classes(FaqSectionStyle.faqItem)
					if (isOpen) classes(FaqSectionStyle.faqItemOpened)
				}) {
					Button(attrs = {
						classes(FaqSectionStyle.questionButton)
						attr("aria-expanded", isOpen.toString())
						onClick {
							openedQuestionIndex = if (isOpen) -1 else index
						}
					}) {
						Span({
							classes(FaqSectionStyle.questionText)
						}) {
							Text(item.question)
						}

						Span({
							classes(FaqSectionStyle.questionIndicator)
							if (isOpen) classes(FaqSectionStyle.questionIndicatorOpened)
						}) {
							MdiChevronRight(style = IconStyle.ROUNDED)
						}
					}

					Div({
						classes(FaqSectionStyle.answerWrapper)
						if (isOpen) classes(FaqSectionStyle.answerWrapperOpened)
						attr("aria-hidden", (!isOpen).toString())
					}) {
						Div({
							classes(FaqSectionStyle.answerInner)
							if (isOpen) classes(FaqSectionStyle.answerInnerOpened)
						}) {
							Markdown(item.answer, FaqSectionStyle.answer)
						}
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalComposeWebApi::class)
object FaqSectionStyle : StyleSheet() {
	val faqContainer by style {
		marginX(auto)
		marginTop(1.5.cssRem)
		marginBottom(2.5.cssRem)
		maxWidth(85.cssRem)
		display(DisplayStyle.Grid)
		gridTemplateColumns("minmax(0, 0.9fr) minmax(0, 1.4fr)")
		gap(2.cssRem)
		padding(2.1.cssRem, 4.vw)
		borderRadius(1.8.cssRem)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		property(
			"background",
			"linear-gradient(135deg, rgba(15, 20, 27, 0.92) 0%, rgba(21, 28, 38, 0.9) 100%)"
		)

		mdMax(self) {
			gridTemplateColumns("1fr")
			padding(2.cssRem, 4.vw)
		}
	}

	val faqHeader by style {
		"h2" {
			marginTop(0.px)
			marginBottom(0.px)
			fontSize(2.6.cssRem)
			textAlign(TextAlign.Left)
		}

		mdMax(self) {
			"h2" style {
				textAlign(TextAlign.Center)
				fontSize(2.2.cssRem)
			}
		}
	}

	val faq by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.2.cssRem)
	}

	val faqItem by style {
		padding(1.05.cssRem, 1.35.cssRem)
		borderRadius(1.1.cssRem)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		backgroundColor(Color("rgba(12, 18, 26, 0.85)"))
		boxShadow(0.px, 12.px, 30.px, 0.px, rgba(5, 12, 20, 0.35))
		overflow(Overflow.Hidden)
		transition(0.25.s, "border-color", "background-color")

		xsMax(self) {
			padding(1.05.cssRem, 1.1.cssRem)
		}
	}

	val faqItemOpened by style {
		borderColor(Color("rgba(8, 182, 214, 0.45)"))
		backgroundColor(Color("rgba(16, 24, 34, 0.92)"))
	}

	val questionButton by style {
		width(100.percent)
		display(DisplayStyle.Flex)
		justifyContent(org.jetbrains.compose.web.css.JustifyContent.SpaceBetween)
		alignItems(org.jetbrains.compose.web.css.AlignItems.Center)
		gap(1.cssRem)
		backgroundColor(Color.transparent)
		border(0.px, LineStyle.Solid, Color.transparent)
		padding(0.px)
		color(Color("var(--landing-text)"))
		cursor(Cursor.Pointer)
		textAlign(TextAlign.Left)
		fontSize(1.2.cssRem)
		fontWeight(FontWeight.Bold)
		property("-webkit-tap-highlight-color", "transparent")

		hover(self) style {
			color(Color("var(--landing-accent)"))
		}

		mdMax(self) {
			fontSize(1.12.cssRem)
		}
	}

	val questionText by style {
		flex(1)
	}

	val questionIndicator by style {
		color(Color("var(--landing-accent)"))
		display(DisplayStyle.Flex)
		alignItems(org.jetbrains.compose.web.css.AlignItems.Center)
		fontSize(1.6.cssRem)
		fontWeight(FontWeight.Normal)
		lineHeight(1.number)
		justifyContent(org.jetbrains.compose.web.css.JustifyContent.Center)
		transition(0.25.s, "color", "transform")
		width(1.4.cssRem)

		child(self, type("svg")) style {
			width(1.35.cssRem)
			height(1.35.cssRem)
		}
	}

	val questionIndicatorOpened by style {
		transform {
			rotate(90.deg)
		}
	}

	val answerWrapper by style {
		display(DisplayStyle.Grid)
		gridTemplateRows(GridEntry.TrackSize(0.fr))
		opacity(0)
		overflow(Overflow.Hidden)
		transition(0.2.s, "grid-template-rows", "opacity", "visibility")
		visibility(Visibility.Hidden)
	}

	val answerWrapperOpened by style {
		gridTemplateRows(GridEntry.TrackSize(1.fr))
		opacity(1)
		visibility(Visibility.Visible)
	}

	val answerInner by style {
		minHeight(0.px)
		overflow(Overflow.Hidden)
		transition(0.2.s, "transform")
		transform {
			translateY((-8).px)
		}
	}

	val answerInnerOpened by style {
		transform {
			translateY(0.px)
		}
	}

	val answer by style {
		fontSize(1.02.cssRem)
		fontWeight(FontWeight.Normal)
		color(Color("var(--landing-muted)"))
		lineHeight(1.6.number)
		marginTop(0.8.cssRem)
		marginBottom(0.px)
		paddingLeft(0.px)

		mdMax(self) {
			fontSize(0.98.cssRem)
		}
	}
}
