package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.common.LinkButton
import io.github.ayfri.kore.website.components.common.Tab
import io.github.ayfri.kore.website.components.common.Tabs
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text

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

// language=kotlin
const val PRESENTATION_CODE_HELPERS = """
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
"""

// language=kotlin
const val PRESENTATION_CODE_EXTERNAL = """
data object VanillaTweaksCreate {
	val VANILLA_TWEAKS_SPAWN_NAMESPACE = "spawn"

	context(Function)
	fun checkX() = function(namespace = VANILLA_TWEAKS_SPAWN_NAMESPACE, "check_x")
	context(Function)
	fun checkZ() = function(namespace = VANILLA_TWEAKS_SPAWN_NAMESPACE, "check_z")
	context(Function)
	fun config() = function(namespace = VANILLA_TWEAKS_SPAWN_NAMESPACE, "config")
	context(Function)
	fun decrementCooldown() = function(namespace = VANILLA_TWEAKS_SPAWN_NAMESPACE, "decrement_cooldown")
	context(Function)
	fun decrementCooldowns() = function(namespace = VANILLA_TWEAKS_SPAWN_NAMESPACE, "decrement_cooldowns")

	// ...
	context(Function)
	fun uninstall() = function(namespace = VANILLA_TWEAKS_SPAWN_NAMESPACE, "restore_command_feedback")

	// Note : You could also use FunctionArgument instead, but you'll have instead to call the function explicitly.

	fun loadedPredicate() = PredicateArgument.invoke(namespace = VANILLA_TWEAKS_SPAWN_NAMESPACE, name = "loaded")

	fun validSpawnLocationTag() = TagArgument.invoke(namespace = VANILLA_TWEAKS_SPAWN_NAMESPACE, name = "blocks/valid_spawn_location")
}

fun main() {
	val datapack = dataPack("vanilla_tweaks_spawn") {
		function("check_coordinates") {
			VanillaTweaksCreate.checkX()
			VanillaTweaksCreate.checkZ()
		}

		function("config_if_loaded") {
			execute {
				ifCondition(VanillaTweaksCreate.loadedPredicate())
				run {
					VanillaTweaksCreate.config()
				}
			}
		}
	}
}
"""

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
			Span("Kore")
		}

		P(
			"""
				Kore is a modern, open-source, and easy-to-use Kotlin library for Minecraft datapack development.
				You can use it to create your own Minecraft Datapacks, and without the need to write a single line of JSON or MCFunction.
			""".trimIndent(),
			HeroSectionStyle.subTitle
		)

		Div({
			classes(HeroSectionStyle.actions)
		}) {
			LinkButton("Get Started", "https://ayfri.com/articles/kore-introduction/", classes = arrayOf("primary"))
			LinkButton("GitHub", GITHUB_LINK)
		}

		val tabs = listOf(
			Tab(
				name = "index.kt",
				content = { CodeBlock(PRESENTATION_CODE.trimIndent(), "kotlin") }
			),
			Tab(
				name = "customScoreboard.kt",
				content = { CodeBlock(PRESENTATION_CODE_HELPERS.trimIndent(), "kotlin") }
			),
			Tab(
				name = "externalDatapack.kt",
				content = { CodeBlock(PRESENTATION_CODE_EXTERNAL.trimIndent(), "kotlin") }
			),
		)

		Tabs(tabs, className = HeroSectionStyle.tabs, contentClassName = HeroSectionStyle.tabContent)
	}
}

object HeroSectionStyle : StyleSheet() {
	init {
		smMax(child(className("code-toolbar"), type("pre"))) {
			fontSize(0.8.cssRem)
		}
	}

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

		mdMax(self) {
			"p" style {
				fontSize(1.05.cssRem)
				marginBottom(2.cssRem)
			}
		}
	}

	val title by style {
		fontSize(titleFontSize)
		fontWeight(FontWeight.Bold)
		letterSpacing((-1.5).px)
		maxWidth(80.percent)
		marginY(4.cssRem)

		"span" style {
			fontWeight(FontWeight.Black)
			marginLeft(titleFontSize / 4)
			textGradient(GlobalStyle.logoLeftColor, GlobalStyle.logoRightColor)
		}

		lgMax(self) {
			fontSize(3.cssRem)
		}

		mdMax(self) {
			fontSize(2.8.cssRem)
			maxWidth(92.percent)
		}
	}

	val subTitle by style {
		maxWidth(85.percent)

		mdMax(self) {
			maxWidth(92.percent)
		}
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

		mdMax(child(self, type("a"))) {
			fontSize(1.2.cssRem)
		}
	}

	val tabs by style {
		marginY(4.cssRem)
		height(37.5.cssRem)
		width(60.percent)

		xlMax(self) {
			height(32.cssRem)
			width(85.percent)
		}

		mdMax(self) {
			height(35.cssRem)
			width(90.percent)
		}
	}

	val tabContent by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		height(100.percent)
		width(100.percent)
	}
}
