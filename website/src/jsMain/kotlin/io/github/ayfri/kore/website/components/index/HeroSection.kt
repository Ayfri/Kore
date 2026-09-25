package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.silk.components.icons.lucide.LucideArrowRight
import io.github.ayfri.kore.website.components.common.*
import io.github.ayfri.kore.website.components.features.*
import io.github.ayfri.kore.website.components.mc.*
import io.github.ayfri.kore.website.utils.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.A as DomA

private class HeroExample(val name: String, val file: String, val showcase: Showcase, val preview: @Composable () -> Unit)

private val welcomeShowcase = Showcase(
	"Chat & items",
	"""
		dataPack("arena") {
			function("welcome") {
				tellraw(allPlayers(), "Welcome!", color = Color.GOLD)
				give(allPlayers(), Items.IRON_SWORD)
			}
		}
	""".trimIndent(),
	listOf(
		ShowcaseFile(
			"data/arena/function/welcome.mcfunction",
			"mcfunction",
			"""
				tellraw @a {type:"text",color:"gold",text:"Welcome!"}
				give @a minecraft:iron_sword
			""".trimIndent(),
		),
	),
)

private val victoryShowcase = Showcase(
	"Titles",
	"""
		dataPack("arena") {
			function("victory") {
				title(
					allPlayers(),
					TitleLocation.TITLE,
					textComponent("Victory!", Color.GOLD),
				)
				effect(allPlayers()) {
					give(Effects.GLOWING, 10, 0, true)
				}
			}
		}
	""".trimIndent(),
	listOf(
		ShowcaseFile(
			"data/arena/function/victory.mcfunction",
			"mcfunction",
			"""
				title @a title {type:"text",color:"gold",text:"Victory!"}
				effect give @a minecraft:glowing 10 0 true
			""".trimIndent(),
		),
	),
)

private val bossBarShowcase = Showcase(
	"Boss bars",
	"""
		dataPack("arena") {
			function("boss_fight") {
				bossBar("boss", "arena") {
					add("Wither Storm")
					setColor(BossBarColor.PURPLE)
					setStyle(BossBarStyle.NOTCHED_10)
					setMax(200)
					setValue(150)
					setPlayers(allPlayers())
				}
			}
		}
	""".trimIndent(),
	listOf(
		ShowcaseFile(
			"data/arena/function/boss_fight.mcfunction",
			"mcfunction",
			"""
				bossbar add arena:boss "Wither Storm"
				bossbar set arena:boss color purple
				bossbar set arena:boss style notched_10
				bossbar set arena:boss max 200
				bossbar set arena:boss value 150
				bossbar set arena:boss players @a
			""".trimIndent(),
		),
	),
)

private val scoreboardShowcase = Showcase(
	"Scoreboards",
	"""
		dataPack("arena") {
			function("setup_scores") {
				scoreboard {
					objectives {
						add("kills", ScoreboardCriteria.PLAYER_KILL_COUNT)
						setDisplay(DisplaySlots.sidebar, "kills")
					}
				}
			}
		}
	""".trimIndent(),
	listOf(
		ShowcaseFile(
			"data/arena/function/setup_scores.mcfunction",
			"mcfunction",
			"""
				scoreboard objectives add kills playerKillCount
				scoreboard objectives setdisplay sidebar kills
			""".trimIndent(),
		),
	),
)

/** Slots of the 27 slot chest filled by the 3 rolls of the boss_chest loot table. */
private val bossChestLoot = mapOf(3 to "iron_ingot", 12 to "golden_apple", 23 to "diamond")

private val heroExamples = listOf(
	HeroExample("Chat", "Welcome.kt", welcomeShowcase) {
		WorldPreview(1) {
			McChat(FeatureVisualsStyle.hudChat, FeatureVisualsStyle.aboveHotbar) { McChatLine("Welcome!", McColor.GOLD) }
			Div({ classes(FeatureVisualsStyle.hudHotbar) }) { McHotbar(listOf("iron_sword")) }
		}
	},
	HeroExample("Titles", "Victory.kt", victoryShowcase) {
		WorldPreview(3) {
			McTitle("Victory!", McColor.GOLD)
			Div({ classes(FeatureVisualsStyle.hudHotbar) }) { McHotbar(listOf("iron_sword")) }
		}
	},
	HeroExample("Boss bars", "BossFight.kt", bossBarShowcase) {
		WorldPreview(2) {
			Div({ classes(HeroSectionStyle.bossBar) }) { McBossBar("Wither Storm", McBossBarColor.PURPLE, 150 / 200.0, notches = 10) }
			Div({ classes(FeatureVisualsStyle.hudHotbar) }) { McHotbar(listOf("iron_sword")) }
		}
	},
	HeroExample("Scoreboards", "Scores.kt", scoreboardShowcase) {
		WorldPreview(0) {
			Div({ classes(HeroSectionStyle.sidebar) }) {
				McSidebar(
					"kills", McColor.WHITE, listOf(
						McSidebarLine("Steve", value = "7", valueColor = McColor.RED),
						McSidebarLine("Alex", value = "4", valueColor = McColor.RED),
						McSidebarLine("Notch", value = "1", valueColor = McColor.RED),
					)
				)
			}
			Div({ classes(FeatureVisualsStyle.hudHotbar) }) { McHotbar(listOf("iron_sword")) }
		}
	},
	HeroExample("Recipes", "Recipes.kt", recipeShowcase) {
		WorldPreview(1, McUiStyle.menuBackground, HeroSectionStyle.centered) { ArenaKeyRecipe() }
	},
	HeroExample("Loot tables", "Loot.kt", showcases.first()) {
		WorldPreview(3, McUiStyle.menuBackground, HeroSectionStyle.centered) {
			McContainer("generic_54", height = 222, rows = 0 until 71) {
				McLabel("Chest", 8, 6)
				bossChestLoot.forEach { (slot, item) -> McSlot(8 + slot % 9 * 18, 18 + slot / 9 * 18, item) }
			}
		}
	},
	HeroExample("Advancements", "Advancements.kt", advancementShowcase) {
		WorldPreview(0) { FirstBloodToast(HeroSectionStyle.toast) }
	},
)

/** Time each example stays on screen while the showcase rotates on its own. */
private const val EXAMPLE_MS = 7000L

private const val SHOWCASE_ID = "hero-showcase"

@Composable
fun HeroSection() {
	Style(HeroSectionStyle)

	Section({ classes(HeroSectionStyle.hero) }) {
		DomA("/updates", { classes(HeroSectionStyle.announcement) }) {
			Span("New", HeroSectionStyle.announcementBadge)
			Text("Kore ${AppGlobals.getValue("projectVersion")} supports Minecraft ${AppGlobals.getValue("minecraftVersion")}")
			LucideArrowRight()
		}

		H1({ classes(HeroSectionStyle.title) }) {
			Text("Minecraft datapacks, written in ")
			Span("Kotlin", FeatureSectionsStyle.heroTitleAccent)
		}

		P(
			"Kore is a Kotlin library that generates regular Minecraft datapacks. Your editor autocompletes every item and command, mistakes show up before you /reload, and what ships is plain vanilla files.",
			HeroSectionStyle.lead
		)

		Div({ classes(HeroSectionStyle.actions) }) {
			LinkButton("Get started", "/docs/getting-started", color = ButtonColor.PRIMARY)
			LinkButton("Explore features", "/features", variant = ButtonVariant.OUTLINE)
		}

		HeroShowcase()
	}
}

/** One Kotlin snippet next to what it does in game, rotating through [heroExamples] until the visitor picks one. */
@Composable
private fun HeroShowcase() {
	var example by remember { mutableStateOf(0) }
	var autoplay by remember { mutableStateOf(true) }
	val current = heroExamples[example]
	val output = current.showcase.outputs.first()

	LaunchedEffect(autoplay) {
		while (autoplay) {
			delay(EXAMPLE_MS)
			example = (example + 1) % heroExamples.size
		}
	}
	highlightCodeIn(SHOWCASE_ID, example)

	Div({ classes(HeroSectionStyle.showcase) }) {
		Div({ classes(HeroSectionStyle.tabs) }) {
			heroExamples.forEachIndexed { index, entry ->
				Button({
					classes(HeroSectionStyle.tab)
					if (index == example) classes(HeroSectionStyle.tabActive)
					onClick {
						example = index
						autoplay = false
					}
				}) {
					Text(entry.name)
					if (index == example && autoplay) Span({ classes(HeroSectionStyle.tabProgress) })
				}
			}
		}

		key(example) {
			Div({
				id(SHOWCASE_ID)
				classes(HeroSectionStyle.frame, FeatureSectionsStyle.sceneBody)
			}) {
				Div({ classes(HeroSectionStyle.codePane) }) {
					Span(current.file, HeroSectionStyle.paneTitle)
					CodeBlock(current.showcase.kotlin, "kotlin")
					Div({ classes(HeroSectionStyle.paneFooter) }) {
						Text("Generates ")
						Code { Text(output.path) }
					}
				}
				Div({ classes(HeroSectionStyle.gamePane) }) { current.preview() }
			}
		}
	}
}

/** The world seen from title screen panorama [face], with HUD or menu [content] drawn over it. */
@Composable
private fun WorldPreview(face: Int, vararg extraClasses: String, content: @Composable () -> Unit) {
	Div({
		classes(McUiStyle.world, HeroSectionStyle.world, *extraClasses)
		style { property("background-image", mcPanorama(face)) }
	}) { content() }
}

object HeroSectionStyle : StyleSheet() {
	val hero by style {
		alignItems(AlignItems.Center)
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		marginX(auto)
		maxWidth(76.cssRem)
		padding(4.5.cssRem, 5.vw, 3.cssRem)
		textAlign(TextAlign.Center)
		width(100.percent)

		smMax(self) {
			padding(2.5.cssRem, 1.1.cssRem, 2.cssRem)
		}
	}

	val announcement by style {
		alignItems(AlignItems.Center)
		backgroundColor(rgba(21, 28, 38, 0.7))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(999.px)
		color(Color("var(--landing-muted)"))
		display(DisplayStyle.Flex)
		fontSize(0.88.cssRem)
		gap(0.6.cssRem)
		marginBottom(1.8.cssRem)
		padding(0.3.cssRem, 0.9.cssRem, 0.3.cssRem, 0.3.cssRem)
		transition(0.2.s, "border-color", "color")

		"svg" style { fontSize(0.9.cssRem) }

		hover(self) style {
			borderColor(Color("rgba(8, 182, 214, 0.5)"))
			color(Color("var(--landing-text)"))
		}
	}

	val announcementBadge by style {
		backgroundColor(rgba(8, 182, 214, 0.18))
		borderRadius(999.px)
		color(Color("var(--landing-accent-strong)"))
		fontSize(0.78.cssRem)
		fontWeight(600)
		padding(0.15.cssRem, 0.6.cssRem)
	}

	val title by style {
		fontSize(4.2.cssRem)
		letterSpacing((-2).px)
		lineHeight(1.05.number)
		margin(0.px)
		maxWidth(15.em)
		textWrap(TextWrap.Balance)

		mdMax(self) {
			fontSize(2.8.cssRem)
			letterSpacing((-1).px)
		}

		xsMax(self) {
			fontSize(2.2.cssRem)
		}
	}

	val lead by style {
		color(Color("var(--landing-muted)"))
		fontSize(1.2.cssRem)
		margin(1.4.cssRem, 0.px, 0.px)
		maxWidth(40.cssRem)
		textWrap(TextWrap.Pretty)

		smMax(self) {
			fontSize(1.05.cssRem)
		}
	}

	val actions by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(1.cssRem)
		justifyContent(JustifyContent.Center)
		marginTop(2.cssRem)
	}

	val showcase by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(1.2.cssRem)
		marginTop(4.cssRem)
		textAlign(TextAlign.Left)
		width(100.percent)

		smMax(self) {
			marginTop(2.5.cssRem)
		}
	}

	val tabs by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.3.cssRem)
		justifyContent(JustifyContent.Center)
	}

	val tab by style {
		backgroundColor(Color.transparent)
		border(0.px)
		borderRadius(0.5.cssRem)
		color(Color("var(--landing-muted)"))
		cursor(Cursor.Pointer)
		fontFamily("inherit")
		fontSize(0.92.cssRem)
		fontWeight(500)
		overflow(Overflow.Hidden)
		padding(0.5.cssRem, 1.cssRem)
		position(Position.Relative)
		transition(0.2.s, "background-color", "color")

		hover(self) style {
			color(Color("var(--landing-text)"))
		}
	}

	val tabActive by style {
		backgroundColor(rgba(255, 255, 255, 0.06))
		color(Color("var(--landing-text)"))
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val progressFill by keyframes {
		from { transform { scaleX(0) } }
		to { transform { scaleX(1) } }
	}

	/** Fills under the current tab while the showcase rotates, so it's clear it moves on by itself. */
	val tabProgress by style {
		backgroundColor(Color("var(--landing-accent)"))
		bottom(0.px)
		height(2.px)
		left(0.px)
		position(Position.Absolute)
		property("transform-origin", "left")
		width(100.percent)
		animation(progressFill) {
			duration(EXAMPLE_MS.toDouble().ms)
			timingFunction(AnimationTimingFunction.Linear)
		}
	}

	val frame by style {
		backgroundColor(Color("var(--landing-surface-2)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.1.cssRem)
		boxShadow(0.px, 40.px, 100.px, (-20).px, rgba(0, 0, 0, 0.6))
		display(DisplayStyle.Grid)
		gridTemplateColumns("minmax(0, 1.15fr) minmax(0, 1fr)")
		// Tall enough for the longest example, so rotating examples never moves the page.
		minHeight(25.cssRem)
		overflow(Overflow.Hidden)
		width(100.percent)

		lgMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val codePane by style {
		borderRight(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		minWidth(0.px)

		// Prism's language label and copy button are noise in a showcase, the pane already frames the code.
		"div.code-toolbar > .toolbar" style { display(DisplayStyle.None) }

		"div.code-toolbar" style {
			backgroundColor(Color.transparent)
			border(0.px)
			borderRadius(0.px)
			flexGrow(1)
		}

		"pre" style {
			backgroundColor(Color.transparent)
			fontSize(0.82.cssRem)
			height(100.percent)
			margin(0.px)
			overflowX(Overflow.Auto)
			property("box-sizing", "border-box")
		}

		lgMax(self) {
			borderRight(0.px, LineStyle.None, Color.transparent)
			borderBottom(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		}

		smMax(self) {
			"pre" style { fontSize(0.72.cssRem) }
		}
	}

	val paneTitle by style {
		borderBottom(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		color(Color("var(--landing-muted)"))
		fontSize(0.82.cssRem)
		padding(0.7.cssRem, 1.1.cssRem)
	}

	val paneFooter by style {
		borderTop(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		color(Color("var(--landing-muted)"))
		fontSize(0.8.cssRem)
		overflowWrap(OverflowWrap.Anywhere)
		padding(0.7.cssRem, 1.1.cssRem)

		"code" style {
			color(Color("var(--landing-text)"))
			fontFamily("JetBrains Mono", "monospace")
		}
	}

	val gamePane by style {
		minHeight(20.cssRem)
		minWidth(0.px)
		position(Position.Relative)
	}

	val world by style {
		position(Position.Absolute)
		property("inset", "0")

		// GUI pixels are fixed size, so the whole HUD shrinks on phones instead of overflowing.
		smMax(self) {
			"> *" style { property("zoom", "0.75") }
		}
	}

	val centered by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)
	}

	/** The game centers the sidebar vertically on the right edge of the screen. */
	val sidebar by style {
		position(Position.Absolute)
		right(0.px)
		property("top", PIXEL_HALF)
		property("transform", pixelTranslate("0px", "-50%"))
	}

	val bossBar by style {
		position(Position.Absolute)
		top(gui(2))
		property("left", PIXEL_HALF)
		property("transform", pixelTranslate("-50%"))
	}

	val toast by style {
		position(Position.Absolute)
		right(0.px)
		top(gui(4))
	}
}
