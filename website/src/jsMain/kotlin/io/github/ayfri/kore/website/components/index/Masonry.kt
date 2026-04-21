package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.selectors.Nth
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text

data class MasonryItem(
	val title: String,
	val description: String,
	val code: String,
)


val masonryItems = listOf(
	MasonryItem(
		"Simple APIs",
		"Kore provides simple and intuitive APIs to create Minecraft datapacks and call commands. Almost all the lists from the game are available as enums, so you are always sure to use the right value.",
		SIMPLE_API_CODE.trimIndent()
	),
	MasonryItem(
		"JSON-less",
		"By using Kore, you don't have to write a single line of JSON ever. You only have one language to learn and maintain. Feature-complete code completion and documentation are available in your IDE.",
		JSON_LESS_CODE.trimIndent()
	),
	MasonryItem(
		"Perfect for big projects",
		"Kore is perfect for big projects, as it allows you to split your code into multiple files and use the full power of Kotlin to manage your code.",
		BIG_PROJECTS_CODE.trimIndent()
	),
	MasonryItem(
		"Type-safe",
		"We crafted generators for dozens of Minecraft lists, so you can be sure to use the right value at the right place. No more typos in your commands, JSON files, or functions.",
		TYPE_SAFE_CODE.trimIndent()
	),
)

// language=kotlin
private const val SIMPLE_API_CODE = """
	fun myDatapack() = dataPack("my_datapack") {
		function("test") {
			say("Hello World!")
		}

		function("gamemode_creative") {
			gamemode(Gamemode.CREATIVE)
			tellraw(
				targets = allPlayers(),
				message = "You are now in creative mode!",
				color = Color.GREEN
			)
		}
	}
"""

// language=kotlin
private const val JSON_LESS_CODE = """
	fun myDatapack() = dataPack("my_datapack") {
		val myRecipe = recipesBuilder.smithingTransform(
			name = "diamond_to_netherite"
		) {
			template(Items.DIAMOND_BLOCK)
			base(Items.DIAMOND_SWORD)
			addition(Items.NETHERITE_INGOT)
			result(Items.NETHERITE_SWORD)
		}

		function("give_recipe") {
			recipe(allPlayers()).give(myRecipe)
		}
	}
"""

// language=kotlin
private const val BIG_PROJECTS_CODE = """
	fun myDatapack() = dataPack("my_datapack") {
		function("execute_my_function") {
			execute {
				ifCondition(myPredicate)

				run {
					function(myFunction)
					scoreboard.player(self()) {
						add(myScore, 1)
					}
				}
			}
		}
	}
"""

// language=kotlin
private const val TYPE_SAFE_CODE = """
	fun myDatapack() = dataPack("my_datapack") {
		function("kore_is_great") {
			advancement(allPlayers {
				gamemode = !Gamemode.CREATIVE
				nbt = nbt {
					this["using_kore"] = true
				}
			}) {
				grant(Advancements.Story.SHINY_GEAR)
			}

			playSound(
				sound = Sounds.Block.EnchantmentTable,
				source = PlaySoundSource.MASTER,
				target = allEntities()
			)
		}
	}
"""


@Composable
fun Masonry(items: List<MasonryItem>) = Div({
	classes(MasonryStyle.container)
}) {
	Style(MasonryStyle)

	val leftItems = items.filterIndexed { index, _ -> index % 2 == 0 }
	val rightItems = items.filterIndexed { index, _ -> index % 2 != 0 }

	Div({
		classes(MasonryStyle.column)
	}) {
		leftItems.forEach { MasonryItemCard(it) }
	}

	Div({
		classes(MasonryStyle.column)
	}) {
		rightItems.forEach { MasonryItemCard(it) }
	}
}

@Composable
private fun MasonryItemCard(item: MasonryItem) = Div(attrs = {
	classes(MasonryStyle.card)
}) {
	CodeBlock(item.code, "kotlin", MasonryStyle.code)

	Div({
		classes(MasonryStyle.cardContent)
	}) {
		H2 {
			Text(item.title)
		}
		P(item.description)
	}
}

object MasonryStyle : StyleSheet() {
	val container by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		alignItems(AlignItems.FlexStart)
		justifyContent(JustifyContent.Center)
		gap(2.cssRem)
		marginX(auto)
		marginY(2.4.cssRem)
		padding(2.2.cssRem, 5.vw)
		width(100.percent)
		maxWidth(85.cssRem)
		boxSizing(BoxSizing.BorderBox)
		borderRadius(1.8.cssRem)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		property(
			"background",
			"linear-gradient(135deg, rgba(15, 20, 27, 0.9) 0%, rgba(21, 28, 38, 0.9) 100%)"
		)

		lgMax(self) {
			alignItems(AlignItems.Center)
			flexDirection(FlexDirection.Column)
			gap(3.5.cssRem)
			marginY(2.cssRem)
		}

		smMax(self) {
			gap(1.5.cssRem)
			padding(1.35.cssRem, 1.cssRem)
		}
	}

	val column by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		flex(1)
		gap(2.cssRem)
		minWidth(0.px)
		width(0.px)

		self + nthOfType(Nth.Even) style {
			marginTop(2.25.cssRem)

			lgMax(child(className(container), self)) {
				marginTop(0.cssRem)
			}
		}

		lgMax(self) {
			gap(3.5.cssRem)
			maxWidth(38.cssRem)
			width(100.percent)
		}

		xsMax(self) {
			width(100.percent)
		}
	}

	val card by style {
		backgroundColor(Color("var(--landing-card)"))
		borderRadius(1.2.cssRem)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		boxSizing(BoxSizing.BorderBox)
		minWidth(0.px)
		overflow(Overflow.Hidden)
		padding(1.4.cssRem)
		property("box-shadow", "0 18px 40px rgba(5, 12, 20, 0.35)")
		width(100.percent)

		"pre" style {
			boxSizing(BoxSizing.BorderBox)
			margin(0.px)
			maxWidth(100.percent)
			overflowX(Overflow.Auto)
			width(100.percent)
		}

		smMax(self) {
			padding(1.cssRem)
		}
	}

	val cardContent by style {
		marginTop(1.cssRem)

		"h2" style {
			fontSize(1.5.cssRem)
			marginBottom(0.6.cssRem)
		}

		"p" style {
			color(Color("var(--landing-muted)"))
			fontSize(1.02.cssRem)
			marginTop(0.px)
		}

		smMax(self) {
			"h2" style {
				fontSize(1.25.cssRem)
			}

			"p" style {
				fontSize(0.96.cssRem)
			}
		}
	}

	val code by style {
		borderRadius(0.9.cssRem)
		border(1.px, LineStyle.Solid, Color("rgba(8, 182, 214, 0.2)"))
		backgroundColor(Color("rgba(9, 15, 22, 0.95)"))
		boxSizing(BoxSizing.BorderBox)
		maxWidth(100.percent)
		overflowX(Overflow.Auto)
		width(100.percent)

		smMax(self) {
			fontSize(0.8.cssRem)
		}
	}
}
