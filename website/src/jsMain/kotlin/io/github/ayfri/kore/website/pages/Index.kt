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
import io.github.ayfri.kore.website.components.index.*
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.selectors.Nth
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

data class Feature(
	val title: String,
	val description: String,
	val icon: @Composable () -> Unit,
)

@Page
@Composable
fun HomePage() = PageLayout("Home") {
	Style(HomePageStyle)

	// Hero section with code examples
	HeroSection()

	// Core features section
	FeaturesSection()

	// Masonry grid with code examples
	Masonry(masonryItems)

	// Community platforms section
	CommunitySection()

	// FAQ section
	FaqSection()

	// Call to action section
	CtaSection()
}

object HomePageStyle : StyleSheet() {
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
		background(GlobalStyle.secondaryBackgroundColor)
		height(100.percent)
		width(100.percent)
	}

	val featuresContainer by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		paddingX(7.5.percent)
		paddingY(5.cssRem)

		marginY(7.cssRem)

		display(DisplayStyle.Grid)
		gridTemplateColumns {
			repeat(3) { size(1.fr) }
		}
		justifyItems(JustifyItems.Center)

		lgMax(self) {
			paddingX(4.percent)
			marginTop(4.cssRem)
			marginBottom(6.cssRem)
		}

		mdMax(self) {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			gap(3.cssRem)
		}
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

		lgMax(self) {
			padding(1.25.cssRem)
			width(28.vw)
		}

		mdMax(self) {
			width(90.percent)
		}
	}

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

	val cta by style {
		marginY(8.cssRem)

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		textAlign(TextAlign.Center)

		"h2" style {
			fontSize(3.2.cssRem)
			marginY(2.5.cssRem)
			width(40.cssRem)
		}

		mdMax(type("h2")) {
			fontSize(2.5.cssRem)
			width(90.percent)
		}

		"p" style {
			color(GlobalStyle.altTextColor)
			fontSize(1.2.cssRem)
			marginY(3.cssRem)
			width(30.cssRem)
		}

		mdMax(type("p")) {
			width(90.percent)
		}
	}
}

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
			template = Items.DIAMOND_BLOCK
			base = Items.DIAMOND_SWORD
			addition = Items.NETHERITE_INGOT
			result = Items.NETHERITE_SWORD
		}

		function("give_recipe") {
			recipe(allPlayers()).give(myRecipe)
		}
	}
"""

// language=kotlin
private const val BIG_PROJECTS_CODE = """
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

private val masonryItems = listOf(
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
