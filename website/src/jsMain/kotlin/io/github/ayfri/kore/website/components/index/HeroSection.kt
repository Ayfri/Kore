package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.AppGlobals
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.*
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text

enum class HeroTab(val tabName: String, val language: String) {
	INDEX("index.kt", "kotlin") {
		override val code = """
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
		""".trimIndent()
	},
	CUSTOM_SCOREBOARD("customScoreboard.kt", "kotlin") {
		override val code = """
			fun scoreboardDisplayDatapack() = dataPack("scoreboard_display") {
				val randomEntityUUID = randomUUID()
				val scoreName = "score"

				// use a marker entity to hold the score
				val scoreHolderEntity = allEntities(limitToOne = true) {
					nbt = nbt {
						putNbtCompound("data") {
							this["UUID"] = randomEntityUUID.uuid.toString()
						}
					}
				}

				load {
					ScoreboardDisplay.resetAll()

					// create the entity
					summon(EntityTypes.MARKER) {
						putNbtCompound("data") {
							this["UUID"] = randomEntityUUID.uuid.toString()
						}
					}

					// create the score
					scoreboard.objectives.add(scoreName)
					scoreboard.player(scoreHolderEntity) {
						set(scoreName, 0)
					}
				}

				tick {
					// recreate the scoreboard display each tick so that it's dynamic
					scoreboardDisplay("minigame") {
						displayName = textComponent("✪ Mini-game ✪", Color.GOLD)

						// some fake lines for the example
						appendLine("Game: Mini-game")
						appendLine("IP: 127.0.0.1")
						appendLine("Players: 10/20")

						appendLine("------------------")

						// display the score
						appendLine(textComponent("Score: ")) {
							customScoreEntity = scoreHolderEntity
							customScoreObjective = scoreName
						}

						emptyLine()
						appendLine("HyKore server 3.1.0")

						// hide values except the score
						hideValues(1..<5)
						hideValues(6..lines.size)
					}
				}
			}
		""".trimIndent()
	},
	EXTERNAL_DATAPACK("externalDatapack.kt", "kotlin") {
		override val code = """
			import io.github.ayfri.kore.bindings.api.importDatapacks
			import kore.dependencies.vanillatweaks.VanillaTweaks

			fun setupBindings() = importDatapacks {
				github("VanillaTweaks/Vanilla-Tweaks-Datapacks")
			}

			fun main() {
				setupBindings()

				val datapack = dataPack("my_enhanced_datapack") {
					function("check_and_config") {
						function(VanillaTweaks.Spawn.Functions.CHECK_X)

						execute {
							ifCondition(VanillaTweaks.Spawn.Predicates.LOADED)
							run {
								function(VanillaTweaks.Spawn.Functions.CONFIG)
							}
						}
					}
					
					function("spawn_at_valid_location") {
						execute {
							ifCondition(block(vec3(0, -1, 0)).`is`(VanillaTweaks.Spawn.Tags.Blocks.VALID_SPAWN_LOCATION))
							run {
								say("Spawning at valid location!")
							}
						}
					}
				}

				datapack.generateZip()
			}
		""".trimIndent()
	};

	abstract val code: String
}

@Composable
fun HeroSection() {
	Style(HeroSectionStyle)

	Section({
		classes(HeroSectionStyle.heroSection)
	}) {
		H1({
			classes(HeroSectionStyle.title)
		}) {
			Text("Rethink your datapack development experience with")
			Img {
				attr("src", "/logo.png")
				classes(HeroSectionStyle.logo)
			}
		}

		Div({
			classes(HeroSectionStyle.versionContainer)
		}) {
			Div({ classes(HeroSectionStyle.versionItem) }) {
				Span("Kore", classes = arrayOf(HeroSectionStyle.versionLabel))
				Span(AppGlobals["projectVersion"] ?: "1.37.0", classes = arrayOf(HeroSectionStyle.versionValue))
			}
			Div({ classes(HeroSectionStyle.versionDivider) })
			Div({ classes(HeroSectionStyle.versionItem) }) {
				Span("Minecraft", classes = arrayOf(HeroSectionStyle.versionLabel))
				Span(AppGlobals["minecraftVersion"] ?: "1.21.10", classes = arrayOf(HeroSectionStyle.versionValue))
			}
		}

		P(
			"""
				A modern, type-safe Kotlin library for Minecraft datapack development.
				Create complex datapacks without ever writing JSON or MCFunction manually.
			""".trimIndent(),
			HeroSectionStyle.subTitle
		)

		Div({
			classes(HeroSectionStyle.actions)
		}) {
			LinkButton("Get Started", "/docs/getting-started", color = ButtonColor.PRIMARY)
			LinkButton("GitHub", GITHUB_LINK, variant = ButtonVariant.OUTLINE)
		}

		val tabs = HeroTab.entries.map {
			Tab(it.tabName) { CodeBlock(it.code, it.language) }
		}

		Tabs(tabs, className = HeroSectionStyle.tabs, contentClassName = HeroSectionStyle.tabContent)
	}
}

object HeroSectionStyle : StyleSheet() {
	init {
		smMax(child(className("code-toolbar"), type("pre"))) {
			fontSize(0.8.cssRem)
		}
	}

	val titleFontSize = 2.8.cssRem

	val heroSection by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		textAlign(TextAlign.Center)
		paddingTop(5.cssRem)
		paddingBottom(3.cssRem)
		property(
			"background-image",
			"radial-gradient(circle at top, #2e3440 0%, ${GlobalStyle.backgroundColor} 70%), linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px)"
		)
		property("background-size", "100% 100%, 60px 60px, 60px 60px")

		"p" style {
			color(GlobalStyle.altTextColor)
			fontSize(1.1.cssRem)
		}

		mdMax(self) {
			"p" style {
				fontSize(1.cssRem)
				marginBottom(1.5.cssRem)
			}
		}
	}

	val title by style {
		fontSize(titleFontSize)
		fontWeight(FontWeight.Bold)
		letterSpacing((-1.5).px)
		maxWidth(80.percent)
		lineHeight(1.1.number)
		marginTop(1.cssRem)
		marginBottom(2.cssRem)

		lgMax(self) {
			fontSize(2.4.cssRem)
		}

		mdMax(self) {
			fontSize(2.1.cssRem)
			maxWidth(95.percent)
		}
	}

	val logo by style {
		height(titleFontSize * 1.5)
		marginLeft(titleFontSize / 2)
		property("filter", "drop-shadow(0 0 10px ${GlobalStyle.logoRightColor.alpha(0.3)})")
		verticalAlign(VerticalAlign.Middle)

		mdMax(self) {
			height(3.cssRem)
		}
	}

	val subTitle by style {
		maxWidth(45.cssRem)
		marginX(auto)
		property("line-height", "1.6")

		mdMax(self) {
			maxWidth(92.percent)
		}
	}

	val actions by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(1.cssRem)

		mdMax(child(self, type("a"))) {
			fontSize(1.1.cssRem)
		}
	}

	val tabs by style {
		marginY(3.cssRem)
		height(35.cssRem)
		width(60.percent)

		xlMax(self) {
			height(30.cssRem)
			width(85.percent)
		}

		mdMax(self) {
			height(32.cssRem)
			width(90.percent)
		}
	}

	val tabContent by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		height(100.percent)
		width(100.percent)
	}

	val versionContainer by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.secondaryBackgroundColor.alpha(0.5))
		border(1.px, LineStyle.Solid, GlobalStyle.borderColor.alpha(0.2))
		borderRadius(999.px)
		padding(0.5.cssRem, 1.5.cssRem)
		marginBottom(2.cssRem)
		gap(1.5.cssRem)
	}

	val versionItem by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(0.5.cssRem)
		alignItems(AlignItems.Center)
	}

	val versionLabel by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.9.cssRem)
		fontWeight(FontWeight.Medium)
		textTransform(TextTransform.Uppercase)
		letterSpacing(1.px)
	}

	val versionValue by style {
		color(GlobalStyle.logoRightColor)
		fontSize(1.cssRem)
		fontWeight(FontWeight.Bold)
	}

	val versionDivider by style {
		width(1.px)
		height(1.cssRem)
		backgroundColor(GlobalStyle.borderColor.alpha(0.2))
	}
}
