package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.selectors.Nth
import org.jetbrains.compose.web.dom.*

@Composable
fun FaqSection() {
	Style(FaqSectionStyle)

	val questions = mapOf(
		"Is Kore compatible with recent Minecraft versions?" to "Yes, Kore is compatible with all Minecraft versions from 1.20 to the latest version.",
		"Can I use other datapacks with Kore?" to "Yes, you can use other datapacks with Kore. By providing bindings for your datapacks, you can use them in your Kore code.",
		"Why would I use Kore?" to "Kore is a modern, open-source, and easy-to-use Kotlin library for Minecraft datapack development. You can use it to create your own Minecraft Datapacks, and without writing a single line of JSON or MCFunction. You'll have great, precise, and fast code completion, and you'll be able to use the full power of Kotlin to manage your code. Furthermore, you can even create libraries to help you or others develop datapacks.",
		"Are its APIs complete?" to "Yes, Kore's API is complete. It provides simple and intuitive APIs to create Minecraft datapacks and call commands. All the common lists of the game are available as enums (like all the blocks, items, enchantments, etc.), you are always sure to use the right value.",
		"Is Kore compatible with mods?" to "By providing APIs for creating your own commands/features, you can use Kore with mods. However, Kore is not compatible with mods directly.",
	)

	Div({
		classes(FaqSectionStyle.faqContainer)
	}) {
		H2 {
			Text("Frequently Asked Questions")
		}

		Div({
			classes(FaqSectionStyle.faq)
		}) {
			questions.entries.forEachIndexed { index, (question, answer) ->
				val inputName = "faq-entry-$index"

				Input(type = InputType.Checkbox, attrs = {
					hidden()
					name(inputName)
					id(inputName)
				})

				Details({
					attr("open", "true")
					style {
						variable("--lines", (answer.length / 100f).toString())
					}
				}) {
					Summary {
						Label(forId = inputName) {
							Text(question)
						}
					}

					P(answer)
				}
			}
		}
	}
}

object FaqSectionStyle : StyleSheet() {
	val faqContainer by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		paddingY(5.cssRem)

		marginY(7.cssRem)

		"h2" {
			marginTop(0.px)
			marginBottom(5.cssRem)

			fontSize(3.cssRem)
			textAlign(TextAlign.Center)
		}

		mdMax(type("h2")) {
			fontSize(2.7.cssRem)
			paddingX(1.cssRem)
		}

		xsMax(type("h2")) {
			fontSize(2.2.cssRem)
		}
	}

	val faq by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingSection)

		marginX(auto)
		maxWidth(80.percent)
		padding(1.cssRem, 2.cssRem)

		"details" {
			val elementNameHeight by variable<CSSNumeric>()
			elementNameHeight(4.cssRem)

			mdMax(self + firstOfType) {
				elementNameHeight(5.5.cssRem)
			}

			maxWidthBreak(430.px, self + nthOfType(Nth.Functional(b = 2))) {
				elementNameHeight(5.5.cssRem)
			}

			xxsMax(self + lastOfType) {
				elementNameHeight(5.5.cssRem)
			}

			maxWidthBreak(305.px, self + firstOfType) {
				elementNameHeight(7.cssRem)
			}

			maxHeight(elementNameHeight.value())
			transition(0.3.s, AnimationTimingFunction.EaseOut, "max-height")
			overflow(Overflow.Hidden)
			paddingY(1.2.cssRem)

			fontSize(1.3.cssRem)
			fontWeight(FontWeight.Bold)

			self + not(lastOfType) style {
				borderBottom(1.px, LineStyle.Solid, GlobalStyle.borderColor)
			}

			"summary" {
				display(DisplayStyle.Block)
				userSelect(UserSelect.None)

				self + before style {
					fontFamily("Material Icons Round")
					content("\\e145")
					marginRight(0.5.cssRem)
					position(Position.Relative)
					top(3.px)
				}
			}

			"label" {
				cursor(Cursor.Pointer)
			}

			"p" {
				fontSize(1.cssRem)
				fontWeight(FontWeight.Normal)

				paddingLeft(2.cssRem)
				marginBottom(0.px)
			}
		}

		xsMax(desc(type("div") + self, type("summary"))) {
			fontSize(1.175.cssRem)
		}

		xsMax(desc(type("div") + self, type("p"))) {
			paddingLeft(0.px)
		}

		smMax(type("div") + self) {
			paddingX(1.25.cssRem)
			maxWidth(90.percent)
		}

		adjacent(type("input") + checked, type("details")) style {
			lgMin(self) {
				maxHeight("calc(var(--lines) * 1.2rem + 6.5rem)")
			}

			lgMax(self) {
				maxHeight("calc(var(--lines) * 1.8rem + 6.5rem)")
			}

			mdMax(self) {
				this.maxHeight("calc(var(--lines) * 2.5rem + 6.5rem)")
			}

			smMax(self) {
				maxHeight("calc(var(--lines) * 3.2rem + 6.5rem)")
			}

			maxWidthBreak(400.px, self) {
				maxHeight("calc(var(--lines) * 4rem + 6.5rem)")
			}

			xxsMax(self) {
				maxHeight("calc(var(--lines) * 5.5rem + 6.5rem)")
			}
		}

		desc(adjacent(type("input") + checked, type("details")), type("summary") + before) style {
			content("\\e15b")
		}
	}
}
