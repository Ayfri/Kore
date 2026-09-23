package io.github.ayfri.kore.website.components.features

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFileBraces
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFileCode
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFolder
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.utils.Span
import io.github.ayfri.kore.website.utils.animationDelay
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import kotlin.math.*

/** Minecraft's `§` color codes used by the in-game mockups. */
private object McColor {
	const val AQUA = "#55ffff"
	const val BLUE = "#5555ff"
	const val DARK_GREEN = "#00aa00"
	const val DARK_PURPLE = "#aa00aa"
	const val GOLD = "#ffaa00"
	const val GRAY = "#aaaaaa"
	const val WHITE = "#ffffff"
	const val YELLOW = "#ffff55"
}

/** Vanilla textures from misode/mcmeta, hotlinked so Mojang assets never land in this repo. */
private const val TEXTURES = "https://raw.githubusercontent.com/misode/mcmeta/assets/assets/minecraft/textures"

private fun texture(path: String) = "url('$TEXTURES/$path.png')"

@Composable
private fun ItemSprite(item: String) = Img("$TEXTURES/item/$item.png", item) { classes(FeatureVisualsStyle.itemIcon) }

@Composable
private fun Window(title: String, content: @Composable () -> Unit) {
	Div({ classes(FeatureVisualsStyle.window) }) {
		Div({ classes(FeatureVisualsStyle.windowBar) }) {
			repeat(3) { Span({ classes(FeatureVisualsStyle.windowDot) }) }
			Span(title, FeatureVisualsStyle.windowTitle)
		}
		content()
	}
}

@Composable
private fun WindowFooter(label: String, content: @Composable () -> Unit) {
	Div({ classes(FeatureVisualsStyle.windowFooter) }) {
		Span(label, FeatureVisualsStyle.footerLabel)
		content()
	}
}

@Composable
private fun McLine(text: String, color: String, italic: Boolean = false, underlined: Boolean = false) {
	Div({
		classes(FeatureVisualsStyle.mcLine)
		style {
			color(Color(color))
			if (italic) fontStyle(FontStyle.Italic)
			if (underlined) property("text-decoration", "underline")
		}
	}) { Text(text.ifEmpty { " " }) }
}

/** Kotlin source on top, the files it generates below, as captured from a real `exportAsStrings()` run. */
@Composable
private fun CodeCompare(title: String, kotlin: String, outputs: List<ShowcaseFile>) {
	Window(title) {
		CodeBlock(kotlin, "kotlin")
		outputs.forEach { file ->
			Div({ classes(FeatureVisualsStyle.outputHeader) }) {
				Span("generates", FeatureVisualsStyle.footerLabel)
				Span(file.path, FeatureVisualsStyle.outputPath)
			}
			CodeBlock(file.code, file.language)
		}
	}
}

// ---------------------------------------------------------------- Commands

@Composable
fun AutocompleteScene() {
	val suggestions = listOf("DIAMOND", "DIAMOND_AXE", "DIAMOND_BLOCK", "DIAMOND_BOOTS", "DIAMOND_CHESTPLATE", "DIAMOND_HELMET")
	var selected by remember { mutableStateOf(0) }
	Window("Arena.kt") {
		Div({ classes(FeatureVisualsStyle.editor) }) {
			Div { Span("function", FeatureVisualsStyle.tokenFn); Text("(\"reward\") {") }
			Div({ classes(FeatureVisualsStyle.indent) }) {
				Span("give", FeatureVisualsStyle.tokenFn)
				Text("(self(), Items.DIA")
				Span({ classes(FeatureVisualsStyle.caret) })
			}
			Div({ classes(FeatureVisualsStyle.popup) }) {
				suggestions.forEachIndexed { index, name ->
					Div({
						classes(FeatureVisualsStyle.suggestion)
						if (index == selected) classes(FeatureVisualsStyle.suggestionActive)
						onMouseEnter { selected = index }
						onClick { selected = index }
					}) {
						Span("E", FeatureVisualsStyle.enumBadge)
						Span({ classes(FeatureVisualsStyle.suggestionName) }) {
							B { Text("DIA") }
							Text(name.removePrefix("DIA"))
						}
						Span("Items", FeatureVisualsStyle.suggestionType)
					}
				}
			}
			Div { Text("}") }
		}
		WindowFooter("generates") {
			Code { Text("give @s minecraft:${suggestions[selected].lowercase()}") }
		}
	}
}

@Composable
fun MacrosScene() = CodeCompare(
	"Spawn.kt",
	"""
		class TeleportMacros : Macros() {
			val player by "player"
		}

		val teleportToSpawn = function("teleport_to_spawn", ::TeleportMacros) {
			teleport(player(macros.player), vec3())
		}

		function("start") {
			function(teleportToSpawn, arguments = nbt { this["player"] = "Steve" })
		}
	""".trimIndent(),
	listOf(
		ShowcaseFile("function/teleport_to_spawn.mcfunction", "mcfunction", "\$teleport @a[limit=1,name=\$(player)] ~ ~ ~"),
		ShowcaseFile("function/start.mcfunction", "mcfunction", "function arena:teleport_to_spawn {player:\"Steve\"}"),
	),
)

@Composable
fun SelectorsScene() = CodeCompare(
	"Rewards.kt",
	"""
		execute {
			asTarget(allPlayers {
				scores { score("kills") greaterThanOrEqualTo 10 }
			})
			at(self())
			run { effect(self()) { give(Effects.GLOWING, 10, 0, true) } }
		}
	""".trimIndent(),
	listOf(
		ShowcaseFile(
			"function/reward_winners.mcfunction",
			"mcfunction",
			"execute as @a[scores={kills=10..}] at @s run effect give @s minecraft:glowing 10 0 true",
		),
	),
)

// ---------------------------------------------------------------- Game data

@Composable
fun TooltipScene() {
	Div({ classes(FeatureVisualsStyle.stack) }) {
		Window("Relic.kt") {
			CodeBlock(
				"""
					val relic = Items.NETHERITE_SWORD {
						customName(textComponent("Blade of the Arena", Color.GOLD))
						lore(textComponent("Awarded to the last one standing", Color.GRAY) + text("Season 3", Color.DARK_PURPLE))
						unbreakable()
					}

					give(self(), relic)
				""".trimIndent(),
				"kotlin",
			)
		}
		McTooltip {
			McLine("Blade of the Arena", McColor.GOLD, italic = true)
			McLine("Awarded to the last one standing", McColor.GRAY, italic = true)
			McLine("Season 3", McColor.DARK_PURPLE, italic = true)
			McLine("", McColor.WHITE)
			McLine("When in Main Hand:", McColor.GRAY)
			McLine(" 8 Attack Damage", McColor.DARK_GREEN)
			McLine(" 1.6 Attack Speed", McColor.DARK_GREEN)
			McLine("Unbreakable", McColor.BLUE)
		}
	}
}

@Composable
private fun McTooltip(vararg extraClasses: String, content: @Composable () -> Unit) {
	Div({ classes(FeatureVisualsStyle.tooltip, *extraClasses) }) { content() }
}

@Composable
fun ChatScene() {
	var hovered by remember { mutableStateOf(false) }
	Div({ classes(FeatureVisualsStyle.stack) }) {
		Window("Lobby.kt") {
			CodeBlock(
				"""
					val join = function("join") { teleport(self(), vec3(0, 80, 0)) }

					tellraw(allPlayers(), textComponent("[Join the arena]", Color.AQUA) {
						hoverEvent { showText("Teleports you to the lobby") }
						clickEvent { runCommand { function(join) } }
					})
				""".trimIndent(),
				"kotlin",
			)
		}
		Div({ classes(FeatureVisualsStyle.chatScreen) }) {
			Div({ classes(FeatureVisualsStyle.chatLines) }) {
				McLine("<Alex> ready?", McColor.WHITE)
				Div({
					classes(FeatureVisualsStyle.chatLink)
					onMouseEnter { hovered = true }
					onMouseLeave { hovered = false }
				}) { McLine("[Join the arena]", McColor.AQUA, underlined = hovered) }
			}
			if (hovered) McTooltip(FeatureVisualsStyle.chatHover) { McLine("Teleports you to the lobby", McColor.WHITE) }
			Span(if (hovered) "click runs function arena:join" else "hover the chat message", FeatureVisualsStyle.sceneHint)
		}
	}
}

@Composable
fun StorageScene() = CodeCompare(
	"State.kt",
	"""
		val state = storage("state", "arena")

		function("init_state") {
			data(state) {
				set("round", 1)
				set("map", "Sky Wars")
			}
		}
	""".trimIndent(),
	listOf(
		ShowcaseFile(
			"function/init_state.mcfunction",
			"mcfunction",
			"""
				data modify storage arena:state round set value 1
				data modify storage arena:state map set value "Sky Wars"
			""".trimIndent(),
		),
	),
)

// ---------------------------------------------------------------- Data-driven

/** File tree whose three highlighted files open the matching scene through [open]. */
@Composable
fun FileTreeScene(open: (Int) -> Unit) {
	val entries = listOf(
		Triple(0, "arena/", null),
		Triple(1, "pack.mcmeta", null),
		Triple(1, "data/arena/", null),
		Triple(2, "advancement/first_kill.json", 3),
		Triple(2, "dialog/main_menu.json", null),
		Triple(2, "enchantment/lifesteal.json", null),
		Triple(2, "function/load.mcfunction", null),
		Triple(2, "loot_table/boss_chest.json", 1),
		Triple(2, "recipe/arena_key.json", 2),
		Triple(2, "tags/item/weapons.json", null),
		Triple(2, "trial_spawner/arena_wave.json", null),
		Triple(2, "worldgen/biome/ashlands.json", null),
	)
	Window("build/arena") {
		Div({ classes(FeatureVisualsStyle.tree) }) {
			entries.forEach { (depth, name, scene) ->
				Div({
					classes(FeatureVisualsStyle.treeRow)
					if (scene != null) {
						classes(FeatureVisualsStyle.treeRowOpenable)
						onClick { open(scene) }
					}
					style { paddingLeft((depth * 1.3 + 0.5).cssRem) }
				}) {
					when {
						name.endsWith("/") -> LucideFolder()
						name.endsWith(".mcfunction") -> LucideFileCode()
						else -> LucideFileBraces()
					}
					val dir = name.substringBeforeLast('/', "")
					if (name.endsWith("/") || dir.isEmpty()) Text(name)
					else {
						Span("$dir/", FeatureVisualsStyle.treeDir)
						Text(name.substringAfterLast('/'))
					}
					if (scene != null) Span("open", FeatureVisualsStyle.treeOpen)
				}
			}
		}
		WindowFooter("written by") {
			Code { Text("dataPack(\"arena\") { ... }.generate()") }
		}
	}
}

@Composable
fun LootTableScene() = CodeCompare(
	"Loot.kt",
	showcases.first().kotlin,
	showcases.first().outputs.map { it.copy(path = it.path.removePrefix("data/arena/")) },
)

@Composable
fun RecipeScene() {
	Div({ classes(FeatureVisualsStyle.stack) }) {
		CraftingGrid()
		CodeCompare(
			"Recipes.kt",
			"""
				recipes {
					craftingShaped("arena_key") {
						pattern(" G ", "GDG", " G ")
						keys {
							"G" to Items.GOLD_INGOT
							"D" to Items.DIAMOND
						}
						result(Items.TRIAL_KEY)
					}
				}
			""".trimIndent(),
			listOf(
				ShowcaseFile(
					"recipe/arena_key.json",
					"json",
					"""
						{
							"type": "minecraft:crafting_shaped",
							"pattern": [
								" G ",
								"GDG",
								" G "
							],
							"key": {
								"G": "minecraft:gold_ingot",
								"D": "minecraft:diamond"
							},
							"result": "minecraft:trial_key"
						}
					""".trimIndent(),
				),
			),
		)
	}
}

@Composable
private fun CraftingGrid() {
	val pattern = " G GDG G "
	Div({ classes(FeatureVisualsStyle.craftingTable) }) {
		Span("Crafting", FeatureVisualsStyle.craftingTitle)
		Div({ classes(FeatureVisualsStyle.craftingBody) }) {
			Div({ classes(FeatureVisualsStyle.craftingGrid) }) {
				pattern.forEach { key ->
					Div({ classes(FeatureVisualsStyle.slot) }) {
						when (key) {
							'G' -> ItemSprite("gold_ingot")
							'D' -> ItemSprite("diamond")
						}
					}
				}
			}
			Span("➜", FeatureVisualsStyle.craftingArrow)
			Div({ classes(FeatureVisualsStyle.slot, FeatureVisualsStyle.resultSlot) }) {
				ItemSprite("trial_key")
			}
		}
	}
}

@Composable
fun AdvancementScene() {
	Div({ classes(FeatureVisualsStyle.stack) }) {
		Div({ classes(FeatureVisualsStyle.toast) }) {
			Div({ classes(FeatureVisualsStyle.toastIcon) }) {
				ItemSprite("iron_sword")
			}
			Div {
				McLine("Goal Reached!", McColor.YELLOW)
				McLine("First Blood", McColor.WHITE)
			}
		}
		CodeCompare(
			"Advancements.kt",
			"""
				advancement("first_kill") {
					display(Items.IRON_SWORD, "First Blood", "Win your first duel") {
						frame = AdvancementFrameType.GOAL
					}
					criteria {
						playerKilledEntity("kill_player")
					}
				}
			""".trimIndent(),
			listOf(
				ShowcaseFile(
					"advancement/first_kill.json",
					"json",
					"""
						{
							"display": {
								"icon": {
									"id": "minecraft:iron_sword"
								},
								"title": "First Blood",
								"description": "Win your first duel",
								"frame": "goal"
							},
							"criteria": {
								"kill_player": {
									"trigger": "minecraft:player_killed_entity"
								}
							}
						}
					""".trimIndent(),
				),
			),
		)
	}
}

// ---------------------------------------------------------------- Worldgen

private enum class Biome(val label: String, val color: String) {
	DEEP_OCEAN("Deep ocean", "#1f3f7a"),
	OCEAN("Ocean", "#2f63b0"),
	BEACH("Beach", "#d8c98c"),
	DESERT("Desert", "#e3c27a"),
	PLAINS("Plains", "#79b34f"),
	FOREST("Forest", "#3f7d33"),
	MOUNTAINS("Mountains", "#8b8f93"),
	SNOW("Snowy peaks", "#eef3f7"),
}

private const val MAP_WIDTH = 44
private const val MAP_HEIGHT = 24

private fun hash(x: Int, y: Int, seed: Int): Double {
	var h = x * 374761393 + y * 668265263 + seed * 144269504
	h = (h xor (h ushr 13)) * 1274126177
	return ((h xor (h ushr 16)) and 0xffff) / 65535.0
}

private fun smoothNoise(x: Double, y: Double, seed: Int): Double {
	val x0 = floor(x).toInt()
	val y0 = floor(y).toInt()
	val tx = (x - x0).let { it * it * (3 - 2 * it) }
	val ty = (y - y0).let { it * it * (3 - 2 * it) }
	val top = hash(x0, y0, seed) + (hash(x0 + 1, y0, seed) - hash(x0, y0, seed)) * tx
	val bottom = hash(x0, y0 + 1, seed) + (hash(x0 + 1, y0 + 1, seed) - hash(x0, y0 + 1, seed)) * tx
	return top + (bottom - top) * ty
}

private fun fractalNoise(x: Int, y: Int, seed: Int) =
	0.6 * smoothNoise(x / 14.0, y / 14.0, seed) + 0.3 * smoothNoise(x / 7.0, y / 7.0, seed) + 0.1 * smoothNoise(x / 3.5, y / 3.5, seed)

/** A fixed-seed value-noise map, computed once, so the illustration is identical on every render. */
private val biomeMap = List(MAP_HEIGHT) { y ->
	List(MAP_WIDTH) { x ->
		val height = fractalNoise(x, y, 7) + 0.1
		val moisture = fractalNoise(x, y, 31)
		when {
			height < 0.36 -> Biome.DEEP_OCEAN
			height < 0.44 -> Biome.OCEAN
			height < 0.455 -> Biome.BEACH
			height < 0.63 && moisture < 0.42 -> Biome.DESERT
			height < 0.63 && moisture > 0.52 -> Biome.FOREST
			height < 0.63 -> Biome.PLAINS
			height < 0.71 -> Biome.MOUNTAINS
			else -> Biome.SNOW
		}
	}
}

@Composable
fun BiomeMapScene() {
	var hovered by remember { mutableStateOf<Biome?>(null) }
	Window("custom_overworld") {
		Div({
			classes(FeatureVisualsStyle.map)
			onMouseLeave { hovered = null }
		}) {
			biomeMap.forEach { row ->
				row.forEach { biome ->
					Div({
						if (hovered != null && hovered != biome) classes(FeatureVisualsStyle.mapDimmed)
						style { backgroundColor(Color(biome.color)) }
						onMouseEnter { hovered = biome }
					})
				}
			}
		}
		Div({ classes(FeatureVisualsStyle.legend) }) {
			Biome.entries.forEach { biome ->
				Span({
					classes(FeatureVisualsStyle.legendItem)
					if (biome == hovered) classes(FeatureVisualsStyle.legendItemActive)
					onMouseEnter { hovered = biome }
					onMouseLeave { hovered = null }
				}) {
					Span({
						classes(FeatureVisualsStyle.legendSwatch)
						style { backgroundColor(Color(biome.color)) }
					})
					Text(biome.label)
				}
			}
		}
	}
}

// ---------------------------------------------------------------- Gameplay

private enum class GamePhase { LOBBY, PLAYING, ENDED }

@Composable
fun GameplayScene() {
	var phase by remember { mutableStateOf(GamePhase.LOBBY) }
	var remaining by remember { mutableStateOf(1.0) }
	var score by remember { mutableStateOf(340) }
	var title by remember { mutableStateOf<String?>(null) }
	val chat = remember { mutableStateListOf("<Steve> gg") }

	LaunchedEffect(phase) {
		if (phase != GamePhase.PLAYING) return@LaunchedEffect
		title = "Round 1"
		remaining = 1.0
		while (remaining > 0) {
			delay(80)
			remaining = (remaining - 0.01).coerceAtLeast(0.0)
			if (remaining < 0.8) title = null
		}
		chat += "Boss round over!"
		title = "Victory"
		phase = GamePhase.ENDED
	}

	Div({ classes(FeatureVisualsStyle.gameWrapper) }) {
		Div({ classes(FeatureVisualsStyle.scene) }) {
			if (phase != GamePhase.LOBBY) {
				Div({ classes(FeatureVisualsStyle.bossBar) }) {
					McLine("Boss round", McColor.WHITE)
					Div({ classes(FeatureVisualsStyle.bossBarTrack) }) {
						Div({
							classes(FeatureVisualsStyle.bossBarFill)
							style { width((remaining * 100).percent) }
						})
					}
				}
			}

			title?.let {
				Div({ classes(FeatureVisualsStyle.mcTitle) }) { McLine(it, if (it == "Victory") McColor.GOLD else McColor.WHITE) }
			}

			Div({ classes(FeatureVisualsStyle.sidebar) }) {
				Div({ classes(FeatureVisualsStyle.sidebarTitle) }) { McLine("✪ Mini-game ✪", McColor.GOLD) }
				SidebarRow("Game: Sky Wars")
				SidebarRow("State", phase.name.lowercase())
				SidebarRow("Score", score.toString())
				SidebarRow("")
				SidebarRow("HyKore server 3.1.0", color = McColor.YELLOW)
			}

			Div({ classes(FeatureVisualsStyle.chat) }) {
				chat.takeLast(3).forEach { McLine(it, if (it.startsWith("<")) McColor.WHITE else McColor.AQUA) }
			}
		}

		Div({ classes(FeatureVisualsStyle.controls) }) {
			McButton(if (phase == GamePhase.PLAYING) "Round running..." else "timer.start()", enabled = phase != GamePhase.PLAYING) {
				title = null
				phase = GamePhase.PLAYING
			}
			McButton("score += 10") { score += 10 }
			McButton("state.reset()") {
				phase = GamePhase.LOBBY
				title = null
				score = 340
			}
		}
	}
}

@Composable
private fun SidebarRow(label: String, value: String? = null, color: String = McColor.WHITE) {
	Div({ classes(FeatureVisualsStyle.sidebarRow) }) {
		McLine(label, color)
		value?.let { McLine(it, McColor.WHITE) }
	}
}

@Composable
private fun McButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
	Button({
		classes(FeatureVisualsStyle.mcButton)
		if (!enabled) disabled()
		onClick { onClick() }
	}) { Text(label) }
}

// ---------------------------------------------------------------- Helpers

private enum class VfxShape(val call: String) {
	CIRCLE("Shape.CIRCLE"),
	SPIRAL("Shape.SPIRAL"),
	HELIX("Shape.HELIX"),
	SPHERE("Shape.SPHERE"),
	LINE("Shape.LINE"),
}

private const val PARTICLES = 48

/** Projected 2D positions (percent) and depth (0..1) for each particle, so switching shapes animates dot by dot. */
private fun VfxShape.points() = List(PARTICLES) { i ->
	val t = i.toDouble() / PARTICLES
	when (this) {
		VfxShape.CIRCLE -> Triple(50 + 30 * cos(2 * PI * t), 50 + 30 * sin(2 * PI * t) * 1.45, 1.0)
		VfxShape.SPIRAL -> {
			val radius = 4 + 30 * t
			Triple(50 + radius * cos(6 * PI * t), 50 + radius * sin(6 * PI * t) * 1.45, 1.0)
		}
		VfxShape.HELIX -> {
			val angle = 6 * PI * t
			Triple(50 + 22 * cos(angle), 88 - 76 * t, (sin(angle) + 1) / 2)
		}
		VfxShape.SPHERE -> {
			val y = 1 - 2 * (i + 0.5) / PARTICLES
			val ring = sqrt(1 - y * y)
			val theta = PI * (3 - sqrt(5.0)) * i
			Triple(50 + 30 * ring * cos(theta), 50 + 42 * y, (ring * sin(theta) + 1) / 2)
		}
		VfxShape.LINE -> Triple(14 + 72 * t, 70 - 40 * t, 1.0)
	}
}

@Composable
fun ParticlesScene() {
	var shape by remember { mutableStateOf(VfxShape.CIRCLE) }
	Div({ classes(FeatureVisualsStyle.gameWrapper) }) {
		Div({ classes(FeatureVisualsStyle.scene, FeatureVisualsStyle.nightScene) }) {
			shape.points().forEachIndexed { index, (x, y, depth) ->
				Span({
					classes(FeatureVisualsStyle.particle, FeatureVisualsStyle.flame)
					style {
						left(x.percent)
						top(y.percent)
						opacity(0.35 + 0.65 * depth)
						animationDelay((index * 40).ms)
						property("transition-delay", "${index * 8}ms")
					}
				})
			}
			Span("drawShape(\"fx\") { shape = ${shape.call} }", FeatureVisualsStyle.sceneLabel, FeatureVisualsStyle.labelLeft)
		}
		Div({ classes(FeatureVisualsStyle.controls) }) {
			VfxShape.entries.forEach { entry ->
				Button({
					classes(FeatureVisualsStyle.pill)
					if (entry == shape) classes(FeatureVisualsStyle.pillActive)
					onClick { shape = entry }
				}) { Text(entry.name.lowercase()) }
			}
		}
	}
}

@Composable
fun RaycastScene() {
	val steps = 16
	var reached by remember { mutableStateOf(0) }
	LaunchedEffect(Unit) {
		while (true) {
			for (step in 0..steps) {
				reached = step
				delay(70)
			}
			delay(1400)
		}
	}
	Div({ classes(FeatureVisualsStyle.scene, FeatureVisualsStyle.nightScene) }) {
		Span({ classes(FeatureVisualsStyle.player) })
		repeat(steps) { index ->
			if (index < reached) Span({
				classes(FeatureVisualsStyle.particle, FeatureVisualsStyle.endRod)
				style {
					left((18 + index * 4.4).percent)
					top((62 - index * 2.1).percent)
				}
			})
		}
		Span({
			classes(FeatureVisualsStyle.block)
			if (reached == steps) classes(FeatureVisualsStyle.blockHit)
		})
		if (reached == steps) Div({ classes(FeatureVisualsStyle.chat) }) { McLine("Target acquired!", McColor.WHITE) }
		Span("raycast { step = 0.25; onHitBlock { ... } }", FeatureVisualsStyle.sceneLabel, FeatureVisualsStyle.labelLeft)
	}
}

@Composable
fun MenuScene() {
	var pressed by remember { mutableStateOf<String?>(null) }
	Div({ classes(FeatureVisualsStyle.scene, FeatureVisualsStyle.dialogScene) }) {
		Div({ classes(FeatureVisualsStyle.dialog) }) {
			McLine("Arena menu", McColor.WHITE)
			McLine("Pick a mode to queue for.", McColor.GRAY)
			listOf("Solo duel", "Team fight", "Spectate").forEach { label ->
				McButton(label) { pressed = label }
			}
		}
		Span(pressed?.let { "\"$it\" runs its function for the player who clicked" } ?: "buttons run a function for any player", FeatureVisualsStyle.sceneLabel, FeatureVisualsStyle.labelLeft)
	}
}

// ---------------------------------------------------------------- Build

private val terminalLines = listOf(
	"\$ gradlew koreRun --continuous",
	"> Task :koreBuild",
	"> Task :koreLinkDataPack",
	"> Task :koreReload",
	"BUILD SUCCESSFUL",
	"Waiting for changes to input files...",
)

@Composable
fun TerminalScene() {
	var shown by remember { mutableStateOf(0) }
	LaunchedEffect(Unit) {
		while (true) {
			for (count in 1..terminalLines.size) {
				shown = count
				delay(if (count == 1) 900 else 450)
			}
			delay(3000)
		}
	}
	Window("Terminal") {
		Div({ classes(FeatureVisualsStyle.terminal) }) {
			terminalLines.take(shown).forEach { line ->
				Div({
					classes(
						when {
							line.startsWith("$") -> FeatureVisualsStyle.prompt
							line.startsWith("BUILD") -> FeatureVisualsStyle.success
							else -> FeatureVisualsStyle.muted
						}
					)
				}) { Text(line) }
			}
			Span({ classes(FeatureVisualsStyle.caret) })
		}
	}
	Div({ classes(FeatureVisualsStyle.targets) }) {
		listOf("Folder", "Zip", "Fabric", "Forge", "Quilt", "NeoForge").forEach { Span(it, FeatureVisualsStyle.targetBadge) }
	}
}

object FeatureVisualsStyle : StyleSheet() {
	private const val MONO = "JetBrains Mono"

	val window by style {
		backgroundColor(Color("var(--landing-surface-2)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.cssRem)
		boxShadow(0.px, 24.px, 60.px, 0.px, rgba(5, 12, 20, 0.5))
		overflow(Overflow.Hidden)
		width(100.percent)

		"pre" style {
			borderRadius(0.px)
			fontSize(0.8.cssRem)
			margin(0.px)
			overflowX(Overflow.Auto)
		}
	}

	val windowBar by style {
		alignItems(AlignItems.Center)
		backgroundColor(rgba(255, 255, 255, 0.03))
		borderBottom(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		display(DisplayStyle.Flex)
		gap(0.4.cssRem)
		padding(0.6.cssRem, 0.9.cssRem)
	}

	val windowDot by style {
		backgroundColor(rgba(255, 255, 255, 0.14))
		borderRadius(50.percent)
		height(0.6.cssRem)
		width(0.6.cssRem)
	}

	val windowTitle by style {
		color(Color("var(--landing-muted)"))
		fontFamily(MONO, "monospace")
		fontSize(0.78.cssRem)
		marginLeft(0.5.cssRem)
	}

	val windowFooter by style {
		alignItems(AlignItems.Center)
		borderTop(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		fontSize(0.8.cssRem)
		gap(0.6.cssRem)
		padding(0.7.cssRem, 1.cssRem)

		"code" style {
			color(Color("var(--landing-accent-strong)"))
			fontFamily(MONO, "monospace")
		}
	}

	val footerLabel by style {
		color(Color("var(--landing-muted)"))
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)
		letterSpacing(1.px)
		textTransform(TextTransform.Uppercase)
	}

	val outputHeader by style {
		alignItems(AlignItems.Center)
		backgroundColor(rgba(255, 255, 255, 0.03))
		borderBottom(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderTop(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		display(DisplayStyle.Flex)
		gap(0.6.cssRem)
		padding(0.45.cssRem, 1.cssRem)
	}

	val outputPath by style {
		color(Color("var(--landing-text)"))
		fontFamily(MONO, "monospace")
		fontSize(0.78.cssRem)
		overflowWrap(OverflowWrap.Anywhere)
	}

	val editor by style {
		fontFamily(MONO, "monospace")
		fontSize(0.88.cssRem)
		lineHeight(1.9.number)
		padding(1.cssRem, 1.2.cssRem)
		position(Position.Relative)
	}

	val indent by style {
		paddingLeft(1.6.cssRem)
	}

	val tokenFn by style {
		color(Color("#6fb3ff"))
	}

	val caretBlink by keyframes {
		from { opacity(1) }
		to { opacity(0) }
	}

	val caret by style {
		animation(caretBlink) {
			direction(AnimationDirection.Alternate)
			duration(0.6.s)
			iterationCount(null)
		}
		backgroundColor(Color("var(--landing-accent-strong)"))
		display(DisplayStyle.InlineBlock)
		height(1.1.cssRem)
		marginLeft(1.px)
		verticalAlign(VerticalAlign.Middle)
		width(2.px)
	}

	val popup by style {
		backgroundColor(Color("#1b222c"))
		border(1.px, LineStyle.Solid, Color("rgba(151, 176, 202, 0.28)"))
		borderRadius(0.5.cssRem)
		boxShadow(0.px, 14.px, 30.px, 0.px, rgba(0, 0, 0, 0.45))
		margin(0.2.cssRem, 0.px, 0.4.cssRem, 6.5.cssRem)
		maxWidth(22.cssRem)
		padding(0.3.cssRem)

		smMax(self) {
			marginLeft(1.6.cssRem)
		}
	}

	val suggestion by style {
		alignItems(AlignItems.Center)
		borderRadius(0.3.cssRem)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		gap(0.6.cssRem)
		lineHeight(1.6.number)
		padding(0.1.cssRem, 0.5.cssRem)
	}

	val suggestionActive by style {
		backgroundColor(rgba(8, 182, 214, 0.22))
	}

	val enumBadge by style {
		backgroundColor(rgba(254, 201, 7, 0.18))
		borderRadius(0.25.cssRem)
		color(Color("var(--landing-gold)"))
		fontSize(0.7.cssRem)
		fontWeight(700)
		padding(0.px, 0.3.cssRem)
	}

	val suggestionName by style {
		color(Color("var(--landing-muted)"))
		flexGrow(1)

		"b" style { color(Color("var(--landing-accent-strong)")) }
	}

	val suggestionType by style {
		color(Color("var(--landing-muted)"))
		fontSize(0.75.cssRem)
	}

	val stack by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		position(Position.Relative)
		width(100.percent)
	}

	val tooltip by style {
		backgroundColor(rgba(16, 0, 16, 0.94))
		border(2.px, LineStyle.Solid, Color("#2d0a6b"))
		boxShadow(0.px, 0.px, 0.px, 2.px, rgba(16, 0, 16, 0.94))
		marginRight(1.cssRem)
		marginTop((-3).cssRem)
		padding(0.5.cssRem, 0.7.cssRem)
		position(Position.Relative)
		property("align-self", "flex-end")
		property("border-image", "linear-gradient(#5000ff, #28007f) 1")

		smMax(self) {
			marginRight(0.px)
			marginTop((-1).cssRem)
			property("align-self", "center")
		}
	}

	val mcLine by style {
		fontFamily(MONO, "monospace")
		fontSize(0.85.cssRem)
		lineHeight(1.45.number)
		property("text-shadow", "2px 2px 0 rgba(0, 0, 0, 0.55)")
		whiteSpace(WhiteSpace.Pre)
	}

	val chatScreen by style {
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.cssRem)
		minHeight(9.cssRem)
		overflow(Overflow.Hidden)
		padding(1.cssRem)
		position(Position.Relative)
		property("background", "linear-gradient(180deg, #6ea8ff, #b3d3ff)")
	}

	val chatLines by style {
		bottom(1.cssRem)
		left(1.cssRem)
		position(Position.Absolute)

		"div" style {
			backgroundColor(rgba(0, 0, 0, 0.45))
			padding(0.px, 0.4.cssRem)
		}
	}

	val chatLink by style {
		cursor(Cursor.Pointer)
	}

	val chatHover by style {
		left(9.cssRem)
		marginTop(0.px)
		position(Position.Absolute)
		property("align-self", "auto")
		top(1.cssRem)
	}

	val sceneHint by style {
		color(Color("#10213a"))
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)
		position(Position.Absolute)
		right(0.8.cssRem)
		top(0.6.cssRem)
	}

	val tree by style {
		fontFamily(MONO, "monospace")
		fontSize(0.85.cssRem)
		padding(0.7.cssRem, 0.6.cssRem)
	}

	val treeRow by style {
		alignItems(AlignItems.Center)
		borderRadius(0.4.cssRem)
		color(Color("var(--landing-text)"))
		display(DisplayStyle.Flex)
		lineHeight(1.1.cssRem)
		whiteSpace(WhiteSpace.NoWrap)

		"svg" style {
			color(Color("var(--landing-accent)"))
			flexShrink(0)
			marginRight(0.5.cssRem)
		}
	}

	val treeRowOpenable by style {
		cursor(Cursor.Pointer)
		transition(0.15.s, "background-color")

		hover(self) style {
			backgroundColor(rgba(8, 182, 214, 0.12))
		}
	}

	val treeOpen by style {
		border(1.px, LineStyle.Solid, Color("rgba(8, 182, 214, 0.45)"))
		borderRadius(999.px)
		color(Color("var(--landing-accent-strong)"))
		fontSize(0.65.cssRem)
		property("margin-left", "auto")
		marginRight(0.4.cssRem)
		padding(0.px, 0.45.cssRem)
		textTransform(TextTransform.Uppercase)
	}

	val treeDir by style {
		color(Color("var(--landing-muted)"))
	}

	val craftingTable by style {
		backgroundColor(Color("#c6c6c6"))
		border(2.px, LineStyle.Solid, Color("#000"))
		borderRadius(0.25.cssRem)
		boxShadow(0.px, 0.px, 0.px, 0.px, Color.transparent)
		padding(0.6.cssRem, 1.cssRem, 1.cssRem)
		property("align-self", "flex-start")
		property("box-shadow", "inset 3px 3px 0 #fff, inset -3px -3px 0 #555")
	}

	val craftingTitle by style {
		color(Color("#404040"))
		display(DisplayStyle.Block)
		fontFamily(MONO, "monospace")
		fontSize(0.8.cssRem)
		marginBottom(0.4.cssRem)
	}

	val craftingBody by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(1.2.cssRem)
	}

	val craftingGrid by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("repeat(3, 2.4rem)")
	}

	val slot by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color("#8b8b8b"))
		display(DisplayStyle.Flex)
		height(2.4.cssRem)
		justifyContent(JustifyContent.Center)
		width(2.4.cssRem)
		property("box-shadow", "inset 2px 2px 0 #373737, inset -2px -2px 0 #fff")
	}

	val resultSlot by style {
		height(3.2.cssRem)
		width(3.2.cssRem)
	}

	val craftingArrow by style {
		color(Color("#8b8b8b"))
		fontSize(1.8.cssRem)
	}

	val itemIcon by style {
		display(DisplayStyle.Block)
		height(80.percent)
		width(80.percent)
		property("image-rendering", "pixelated")
	}

	val toast by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color("#212121"))
		border(2.px, LineStyle.Solid, Color("#000"))
		borderRadius(0.35.cssRem)
		display(DisplayStyle.Flex)
		gap(0.8.cssRem)
		padding(0.5.cssRem, 1.cssRem, 0.5.cssRem, 0.6.cssRem)
		property("align-self", "flex-end")
		property("box-shadow", "inset 2px 2px 0 #5a5a5a, inset -2px -2px 0 #0e0e0e, 0 12px 30px rgba(0, 0, 0, 0.45)")
		width(18.cssRem)
	}

	val toastIcon by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		height(2.2.cssRem)
		justifyContent(JustifyContent.Center)
		width(2.2.cssRem)
	}

	val map by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("repeat($MAP_WIDTH, 1fr)")
		property("aspect-ratio", "$MAP_WIDTH / $MAP_HEIGHT")
		width(100.percent)

		"> div" style {
			transition(0.15.s, "opacity", "filter")
		}
	}

	val mapDimmed by style {
		opacity(0.25)
		property("filter", "grayscale(0.7)")
	}

	val legend by style {
		display(DisplayStyle.Grid)
		gap(0.3.cssRem, 1.cssRem)
		gridTemplateColumns("repeat(auto-fill, minmax(7.5rem, 1fr))")
		padding(0.8.cssRem, 1.cssRem)
	}

	val legendItem by style {
		alignItems(AlignItems.Center)
		color(Color("var(--landing-muted)"))
		cursor(Cursor.Default)
		display(DisplayStyle.Flex)
		fontSize(0.78.cssRem)
		gap(0.4.cssRem)
		transition(0.15.s, "color")
		whiteSpace(WhiteSpace.NoWrap)
	}

	/** Color and swatch ring only: a bolder font would widen the label and reflow the grid. */
	val legendItemActive by style {
		color(Color("var(--landing-text)"))

		"span" style { property("box-shadow", "0 0 0 2px var(--landing-text)") }
	}

	val legendSwatch by style {
		borderRadius(0.15.cssRem)
		flexShrink(0)
		height(0.7.cssRem)
		transition(0.15.s, "box-shadow")
		width(0.7.cssRem)
	}

	val gameWrapper by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.8.cssRem)
		width(100.percent)
	}

	val scene by style {
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.cssRem)
		boxShadow(0.px, 24.px, 60.px, 0.px, rgba(5, 12, 20, 0.5))
		overflow(Overflow.Hidden)
		position(Position.Relative)
		property("aspect-ratio", "16 / 10")
		// Block grid lines only cover the ground (bottom 45%), layered over the sky/grass/dirt gradient.
		property(
			"background",
			"repeating-linear-gradient(90deg, rgba(0, 0, 0, 0.12) 0 1px, transparent 1px 2.2rem) bottom / 100% 45% no-repeat, " +
				"repeating-linear-gradient(180deg, rgba(0, 0, 0, 0.12) 0 1px, transparent 1px 2.2rem) bottom / 100% 45% no-repeat, " +
				"linear-gradient(180deg, #6ea8ff 0%, #b3d3ff 55%, #6fb144 55%, #4f8a2f 62%, #7a5433 62%, #5a3d26 100%)"
		)
		width(100.percent)
	}

	val nightScene by style {
		property("background", "radial-gradient(circle at 50% 120%, #1c2a3a 0%, #0b1017 70%)")
	}

	val dialogScene by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)
		property("background", "linear-gradient(rgba(0, 0, 0, 0.55), rgba(0, 0, 0, 0.55)), linear-gradient(180deg, #6ea8ff 0%, #b3d3ff 55%, #6fb144 55%, #5a3d26 100%)")
	}

	val dialog by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.5.cssRem)
		width(60.percent)

		"button" style { width(100.percent) }
	}

	val bossBar by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.25.cssRem)
		left(50.percent)
		position(Position.Absolute)
		top(0.8.cssRem)
		property("transform", "translateX(-50%)")
		width(55.percent)
	}

	val bossBarTrack by style {
		backgroundColor(Color("#0b3d0b"))
		height(0.45.cssRem)
		position(Position.Relative)
		width(100.percent)
		// NOTCHED_20 style: 20 segments.
		property(
			"background-image",
			"repeating-linear-gradient(90deg, transparent 0 calc(5% - 1px), rgba(0, 0, 0, 0.55) calc(5% - 1px) 5%)"
		)
	}

	val bossBarFill by style {
		backgroundColor(Color("#3bd23b"))
		height(100.percent)
		property("mix-blend-mode", "screen")
		transition(0.08.s, "width")
	}

	val mcTitle by style {
		left(50.percent)
		position(Position.Absolute)
		top(32.percent)
		property("transform", "translate(-50%, -50%) scale(2.6)")
		property("transform-origin", "center")
	}

	val sidebar by style {
		backgroundColor(rgba(0, 0, 0, 0.42))
		minWidth(12.cssRem)
		padding(0.1.cssRem, 0.4.cssRem, 0.3.cssRem)
		position(Position.Absolute)
		right(0.7.cssRem)
		top(55.percent)
		property("transform", "translateY(-50%)")

		smMax(self) {
			minWidth(9.cssRem)
			"div" style { fontSize(0.62.cssRem) }
		}
	}

	val sidebarTitle by style {
		backgroundColor(rgba(0, 0, 0, 0.2))
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)
		margin(0.px, (-0.4).cssRem, 0.2.cssRem)
	}

	val sidebarRow by style {
		display(DisplayStyle.Flex)
		gap(1.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
	}

	val chat by style {
		bottom(0.8.cssRem)
		left(0.7.cssRem)
		position(Position.Absolute)

		"div" style {
			backgroundColor(rgba(0, 0, 0, 0.45))
			padding(0.px, 0.4.cssRem)
		}

		smMax(self) {
			"div" style { fontSize(0.62.cssRem) }
		}
	}

	val controls by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
	}

	val mcButton by style {
		backgroundColor(Color("#6f6f6f"))
		border(2.px, LineStyle.Solid, Color("#000"))
		color(Color("#fff"))
		cursor(Cursor.Pointer)
		fontFamily(MONO, "monospace")
		fontSize(0.8.cssRem)
		padding(0.35.cssRem, 0.9.cssRem)
		property("box-shadow", "inset 2px 2px 0 #a8a8a8, inset -2px -2px 0 #4a4a4a")
		property("text-shadow", "2px 2px 0 #3f3f3f")

		hover(self) style {
			backgroundColor(Color("#7b86c2"))
			property("box-shadow", "inset 2px 2px 0 #bcc4f0, inset -2px -2px 0 #4a5288")
		}

		(self + disabled) style {
			backgroundColor(Color("#3c3c3c"))
			color(Color("#a0a0a0"))
			cursor(Cursor.Default)
			property("box-shadow", "none")
		}
	}

	val pill by style {
		backgroundColor(Color("var(--landing-card)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(999.px)
		color(Color("var(--landing-muted)"))
		cursor(Cursor.Pointer)
		fontFamily(MONO, "monospace")
		fontSize(0.8.cssRem)
		padding(0.3.cssRem, 0.8.cssRem)
	}

	val pillActive by style {
		backgroundColor(rgba(254, 178, 62, 0.15))
		borderColor(Color("rgba(254, 178, 62, 0.6)"))
		color(Color("var(--landing-text)"))
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val twinkle by keyframes {
		from { transform { scale(0.7) } }
		to { transform { scale(1.2) } }
	}

	val particle by style {
		animation(twinkle) {
			direction(AnimationDirection.Alternate)
			duration(0.9.s)
			iterationCount(null)
			timingFunction(AnimationTimingFunction.EaseInOut)
		}
		height(16.px)
		marginLeft((-8).px)
		marginTop((-8).px)
		position(Position.Absolute)
		transition(0.7.s, "left", "top", "opacity")
		property("background-size", "contain")
		property("image-rendering", "pixelated")
		property("transition-timing-function", "cubic-bezier(0.22, 1, 0.36, 1)")
		width(16.px)
	}

	val flame by style {
		property("background-image", texture("particle/flame"))
		property("filter", "drop-shadow(0 0 4px rgba(255, 140, 30, 0.8))")
	}

	/** First frame of the `end_rod` particle animation. */
	val endRod by style {
		property("background-image", texture("particle/glitter_7"))
		property("filter", "drop-shadow(0 0 4px rgba(220, 210, 255, 0.8))")
	}

	val player by style {
		backgroundColor(Color("#4aa3df"))
		borderRadius(0.2.cssRem)
		bottom(22.percent)
		height(18.percent)
		left(12.percent)
		position(Position.Absolute)
		width(3.percent)
	}

	val block by style {
		height(12.percent)
		left(88.percent)
		position(Position.Absolute)
		top(22.percent)
		transition(0.2.s, "box-shadow")
		width(7.5.percent)
		property("background", "${texture("block/target_side")} center / cover")
		property("image-rendering", "pixelated")
	}

	val blockHit by style {
		property("box-shadow", "0 0 0 3px #fff, 0 0 24px 6px rgba(255, 255, 255, 0.5)")
	}

	val sceneLabel by style {
		backgroundColor(rgba(15, 20, 27, 0.8))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(0.4.cssRem)
		color(Color("var(--landing-accent-strong)"))
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)
		padding(0.2.cssRem, 0.5.cssRem)
		position(Position.Absolute)
		top(0.7.cssRem)
	}

	val labelLeft by style {
		left(0.7.cssRem)
	}

	val terminal by style {
		color(Color("var(--landing-text)"))
		fontFamily(MONO, "monospace")
		fontSize(0.85.cssRem)
		lineHeight(1.8.number)
		minHeight(12.5.cssRem)
		padding(1.cssRem, 1.2.cssRem)
		whiteSpace(WhiteSpace.PreWrap)
	}

	val prompt by style {
		color(Color("var(--landing-accent-strong)"))
	}

	val muted by style {
		color(Color("var(--landing-muted)"))
	}

	val success by style {
		color(Color("#4ade80"))
		fontWeight(700)
	}

	val targets by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
		marginTop(1.cssRem)
	}

	val targetBadge by style {
		backgroundColor(Color("var(--landing-card)"))
		border(1.px, LineStyle.Solid, Color("rgba(254, 201, 7, 0.35)"))
		borderRadius(999.px)
		color(Color("var(--landing-text)"))
		fontFamily(MONO, "monospace")
		fontSize(0.8.cssRem)
		padding(0.3.cssRem, 0.8.cssRem)
	}
}
