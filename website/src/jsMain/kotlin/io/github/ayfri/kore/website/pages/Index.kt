package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle
import com.varabyte.kobweb.silk.components.icons.mdi.MdiArchitecture
import com.varabyte.kobweb.silk.components.icons.mdi.MdiDataObject
import com.varabyte.kobweb.silk.components.icons.mdi.MdiGroupAdd
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.common.LinkButton
import io.github.ayfri.kore.website.components.common.Tab
import io.github.ayfri.kore.website.components.common.Tabs
import io.github.ayfri.kore.website.components.index.Masonry
import io.github.ayfri.kore.website.components.index.MasonryItem
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.*

// language=kotlin
const val PRESENTATION_CODE = """
fun main() {
	val datapack = dataPack("test") {
		function("display_text") {
			tellraw(allPlayers(), textComponent("Hello World!"))
		}

		recipes {
			craftingShaped("enchanted_golden_apple") {
				pattern(
					"GGG",
					"GAG",
					"GGG"
				)

				key("G", Items.GOLD_BLOCK)
				key("A", Items.APPLE)

				result(Items.ENCHANTED_GOLDEN_APPLE)
			}
		}

		function("tp_random_entity_to_entity") {
			val entityName = "test"
			val entity = allEntities(limitToOne = true) {
				name = entityName
			}

			summon(Entities.CREEPER, vec3(), nbt {
				this["CustomName"] = textComponent("Hello World!")
			})

			execute {
				asTarget(allEntities {
					limit = 3
					sort = Sort.RANDOM
				})

				ifCondition {
					score(self(), "test") lessThan 10
				}

				run {
					teleport(entity)
				}
			}
		}

		pack {
			description = textComponent("Datapack test for ", Color.GOLD) + text("Kore", Color.AQUA) {
				bold = true
			}
		}
	}

	datapack.generateZip()
}
"""

data class Feature(
	val title: String,
	val description: String,
	val icon: @Composable () -> Unit,
)

@Page
@Composable
fun HomePage() {
	PageLayout("Home") {
		Style(HomePageStyle)

		Section({
			classes(HomePageStyle.heroSection)
		}) {
			H1({
				classes(HomePageStyle.title)
			}) {
				Text("Rethink your datapack development experience with")
				Span("Kore")
			}

			P(
				"""
					Kore is a modern, open-source, and easy-to-use Kotlin library for Minecraft datapack development.
					You can use it to create your own Minecraft Datapacks, and without the need to write a single line of JSON or MCFunction.
				""".trimIndent(),
				HomePageStyle.subTitle
			)

			Div({
				classes(HomePageStyle.actions)
			}) {
				LinkButton("Get Started", "/docs/getting-started", classes = arrayOf("primary"))
				LinkButton("GitHub", GITHUB_LINK)
			}

			val tabs = listOf(
				Tab(
					name = "index.kt",
					content = { CodeBlock(PRESENTATION_CODE.trimIndent(), "kotlin") }
				),
				Tab(
					name = "test.tkt",
					content = { CodeBlock("function test() { say Hello World! }", "mcfunction") }
				)
			)

			Tabs(tabs, className = HomePageStyle.tabs, contentClassName = HomePageStyle.tabContent)
		}

		val features = listOf(
			Feature(
				"Modern",
				"Write datapacks for recent Minecraft versions with a modern programming language leveraging Kotlin features for robust code.",
			) { MdiArchitecture(style = IconStyle.ROUNDED) },
			Feature(
				"Easy to use",
				"Intuitive API and abstractions over vanilla minimize complexity for simple datapack development.",
			) { MdiDataObject(style = IconStyle.ROUNDED) },
			Feature(
				"Open Source",
				"Active community and contributions provide freedom and support for any open source project.",
			) { MdiGroupAdd(style = IconStyle.ROUNDED) },
		)

		Div({
			classes(HomePageStyle.featuresContainer)
		}) {
			features.forEach { feature ->
				Div({
					classes(HomePageStyle.feature)
				}) {
					feature.icon()
					H2 {
						Text(feature.title)
					}
					P(feature.description)
				}
			}
		}

		val masonryItems = listOf(
			MasonryItem(
				"Simple APIs",
				"Kore provides simple and intuitive APIs to create Minecraft datapacks and call commands. Almost all the lists from the game are available as enums, so you are always sure to use the right value.",
				// language=kotlin
				"""
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
				""".trimIndent()
			),
			MasonryItem(
				"JSON-less",
				"By using Kore, you don't have to write a single line of JSON ever. You only have one language to learn and maintain. Feature-complete code completion and documentation are available in your IDE.",
				// language=kotlin
				"""
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
				""".trimIndent()
			),
			MasonryItem(
				"Perfect for big projects",
				"Kore is perfect for big projects, as it allows you to split your code into multiple files and use the full power of Kotlin to manage your code.",
				// language=kotlin
				"""
					import myproject.constants.myScore
					import myproject.functions.myFunction
					import myproject.predicates.myPredicate

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
				""".trimIndent()
			),
			MasonryItem(
				"Type-safe",
				"We crafted generators for douzens of Minecraft lists, so you can be sure to use the right value at the right place. No more typos in your commands, JSON files, or functions.",
				// language=kotlin
				"""
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
				""".trimIndent()
			),
		)

		Masonry(masonryItems)
	}
}

object HomePageStyle : StyleSheet() {
	val titleFontSize = 4.cssRem

	val heroSection by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		textAlign(TextAlign.Center)

		"p" style {
			color(GlobalStyle.altTextColor)
			fontSize(1.2.cssRem)
		}
	}

	val title by style {
		fontSize(titleFontSize)
		fontWeight(700)
		letterSpacing((-1.5).px)
		maxWidth(80.percent)
		marginY(4.cssRem)

		"span" style {
			fontWeight(900)
			marginLeft(titleFontSize / 4)
			textGradient(GlobalStyle.logoLeftColor, GlobalStyle.logoRightColor)
		}
	}

	val subTitle by style {
		maxWidth(90.percent)
	}

	val actions by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(1.cssRem)

		"a" + className("primary") style {
			backgroundColor(GlobalStyle.buttonBackgroundColor)

			hover(self) style {
				backgroundColor(GlobalStyle.buttonBackgroundColorHover)
			}
		}
	}

	val tabs by style {
		marginY(4.cssRem)
		height(25.cssRem)
		width(60.percent)
	}

	val tabContent by style {
		background(GlobalStyle.secondaryBackgroundColor)
		height(100.percent)
		width(100.percent)
	}

	val featuresContainer by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns {
			repeat(3) { size(1.fr) }
		}
		justifyItems(JustifyItems.Center)
		marginX(7.5.percent)
	}

	val feature by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingSection)
		padding(2.cssRem)

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		width(25.vw)
		textAlign(TextAlign.Center)

		className("material-icons-round") style {
			fontSize(2.cssRem)
			color(GlobalStyle.altTextColor)
		}

		"p" {
			marginBottom(0.px)
		}
	}
}
