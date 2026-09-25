package io.github.ayfri.kore.website.components.features

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFileBraces
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFileCode
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFolder
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.common.mcTexture
import io.github.ayfri.kore.website.components.mc.*
import io.github.ayfri.kore.website.utils.Span
import io.github.ayfri.kore.website.utils.animationDelay
import io.github.ayfri.kore.website.utils.highlightCodeIn
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLElement
import kotlin.math.*

/** An editor-like frame titled [title], usually a file name, around code or a file tree. */
@Composable
fun Window(title: String, vararg extraClasses: String, content: @Composable () -> Unit) {
	Div({ classes(FeatureVisualsStyle.window, *extraClasses) }) {
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

// --- Commands ---

@Composable
fun AutocompleteScene() {
	val suggestions = listOf("DIAMOND", "DIAMOND_AXE", "DIAMOND_BLOCK", "DIAMOND_BOOTS", "DIAMOND_CHESTPLATE", "DIAMOND_HELMET")
	var selected by remember { mutableStateOf(0) }
	val command = "give @s minecraft:${suggestions[selected].lowercase()}"
	Window("Arena.kt") {
		Div({ classes(FeatureVisualsStyle.editor) }) {
			Div { PrismCode("function(\"reward\") {", "kotlin") }
			Div({ classes(FeatureVisualsStyle.indent) }) {
				PrismCode("give(self(), Items.DIA", "kotlin")
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
			Div { PrismCode("}", "kotlin") }
		}
		WindowFooter("generates") {
			Div({ id(AUTOCOMPLETE_OUTPUT_ID) }) {
				key(command) { PrismCode(command, "mcfunction") }
			}
		}
	}
	highlightCodeIn(AUTOCOMPLETE_OUTPUT_ID, command)
}

private const val AUTOCOMPLETE_OUTPUT_ID = "autocomplete-output"

/** Inline code Prism highlights, recreate it with `key` when [code] changes since Prism rewrites its children. */
@Composable
private fun PrismCode(code: String, language: String) {
	Code({ classes("language-$language") }) { Text(code) }
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

// --- Game data ---

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
		McTooltip(FeatureVisualsStyle.tooltipPlacement) {
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
		Div({
			classes(McUiStyle.world, FeatureVisualsStyle.chatScreen)
			style { property("background-image", mcPanorama(1)) }
		}) {
			McChat(FeatureVisualsStyle.hudChat) {
				McChatLine("<Alex> ready?")
				McChatLine("[Join the arena]", McColor.AQUA, underlined = hovered) {
					classes(FeatureVisualsStyle.chatLink)
					onMouseEnter { hovered = true }
					onMouseLeave { hovered = false }
				}
			}
			if (hovered) McTooltip(FeatureVisualsStyle.chatHover) { McLine("Teleports you to the lobby") }
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

// --- Data-driven ---

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
			PrismCode("dataPack(\"arena\") { ... }.generate()", "kotlin")
		}
	}
}

@Composable
fun LootTableScene() = ShowcaseCompare("Loot.kt", showcases.first())

@Composable
fun RecipeScene() {
	Div({ classes(FeatureVisualsStyle.stack) }) {
		ArenaKeyRecipe()
		ShowcaseCompare("Recipes.kt", recipeShowcase)
	}
}

/** The crafting grid of [recipeShowcase]. */
@Composable
fun ArenaKeyRecipe() = McCraftingTable(listOf(" G ", "GDG", " G "), mapOf('G' to "gold_ingot", 'D' to "diamond"), "trial_key")

@Composable
private fun ShowcaseCompare(title: String, showcase: Showcase) =
	CodeCompare(title, showcase.kotlin, showcase.outputs.map { it.copy(path = it.path.removePrefix("data/arena/")) })

@Composable
fun AdvancementScene() {
	Div({ classes(FeatureVisualsStyle.stack) }) {
		FirstBloodToast(FeatureVisualsStyle.toastPlacement)
		ShowcaseCompare("Advancements.kt", advancementShowcase)
	}
}

/** The toast [advancementShowcase] pops when granted. */
@Composable
fun FirstBloodToast(vararg extraClasses: String) = McToast("iron_sword", "Goal Reached!", "First Blood", *extraClasses)

// --- Worldgen ---

/** Biomes with the vanilla map color a map item paints them with. */
private enum class Biome(val label: String, val mapColor: Int) {
	BEACH("Beach", 0xf7e9a3),
	DEEP_OCEAN("Deep Ocean", 0x4040ff),
	DESERT("Desert", 0xf7e9a3),
	FOREST("Forest", 0x007c00),
	OCEAN("Ocean", 0x4040ff),
	PLAINS("Plains", 0x7fb238),
	SNOWY_PLAINS("Snowy Plains", 0xffffff),
	SNOWY_SLOPES("Snowy Slopes", 0xffffff),
	WINDSWEPT_HILLS("Windswept Hills", 0x707070);

	val water get() = this == OCEAN || this == DEEP_OCEAN
}

private const val MAP_SIZE = 128

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

private fun fractalNoise(x: Int, y: Int, seed: Int, scale: Double) =
	0.6 * smoothNoise(x / scale, y / scale, seed) + 0.3 * smoothNoise(2 * x / scale, 2 * y / scale, seed) +
		0.1 * smoothNoise(4 * x / scale, 4 * y / scale, seed)

private class WorldColumn(val biome: Biome, val height: Int)

/** A fixed-seed world, the biome picked from height, temperature and humidity like a trimmed-down `multiNoise` source. */
private fun generateWorld(seaLevel: Int, desertMinTemperature: Double) = List(MAP_SIZE * MAP_SIZE) { index ->
	val x = index % MAP_SIZE
	val z = index / MAP_SIZE
	val height = (fractalNoise(x, z, 7, 40.0) * 140 + 4).toInt()
	/** Value noise stays close to 0.5, stretching it spreads the climate over the whole -1 to 1 range. */
	fun climate(seed: Int, scale: Double) = ((fractalNoise(x, z, seed, scale) - 0.5) * 4).coerceIn(-1.0, 1.0)
	val temperature = climate(31, 56.0)
	val humidity = climate(53, 44.0)
	val biome = when {
		height < seaLevel - 12 -> Biome.DEEP_OCEAN
		height < seaLevel -> Biome.OCEAN
		height < seaLevel + 2 -> Biome.BEACH
		height > 100 -> Biome.SNOWY_SLOPES
		height > 90 -> Biome.WINDSWEPT_HILLS
		temperature >= desertMinTemperature && humidity < 0.05 -> Biome.DESERT
		temperature < -0.3 -> Biome.SNOWY_PLAINS
		humidity > 0.1 -> Biome.FOREST
		else -> Biome.PLAINS
	}
	WorldColumn(biome, height)
}

/**
 * The map item shading: land gets brighter on north-facing rises and darker on drops, with a checkerboard dither,
 * water gets darker with depth. Returns the RGB color of the column at [index].
 */
private fun mapPixel(world: List<WorldColumn>, index: Int, seaLevel: Int): Int {
	val column = world[index]
	val x = index % MAP_SIZE
	val z = index / MAP_SIZE
	val dither = (x + z) and 1
	val brightness = if (column.biome.water) {
		val shade = (seaLevel - column.height) * 0.1 + dither * 0.2
		when {
			shade < 0.5 -> 255
			shade > 0.9 -> 180
			else -> 220
		}
	} else {
		val north = world[if (z == 0) index else index - MAP_SIZE].height.coerceAtLeast(seaLevel)
		val shade = (column.height - north) * 0.8 + (dither - 0.5) * 0.4
		when {
			shade > 0.6 -> 255
			shade < -0.6 -> 180
			else -> 220
		}
	}
	return listOf(16, 8, 0).fold(0) { rgb, shift -> rgb or ((column.biome.mapColor shr shift and 0xff) * brightness / 255 shl shift) }
}

private const val WORLDGEN_CODE_ID = "worldgen-code"
private val SEA_LEVELS = listOf(50, 63, 76)
private val DESERT_TEMPERATURES = listOf(-0.1, 0.1, 0.3)

private data class MapHover(val x: Double, val y: Double, val column: WorldColumn)

@Composable
fun BiomeMapScene() {
	var seaLevel by remember { mutableStateOf(63) }
	var desertMinTemperature by remember { mutableStateOf(0.1) }
	var hover by remember { mutableStateOf<MapHover?>(null) }
	val world = remember(seaLevel, desertMinTemperature) { generateWorld(seaLevel, desertMinTemperature) }

	Div({ classes(FeatureVisualsStyle.stack) }) {
		Div({ classes(FeatureVisualsStyle.mapRow) }) {
			Div({ classes(FeatureVisualsStyle.mapFrame) }) {
				Canvas({
					classes(FeatureVisualsStyle.mapCanvas)
					attr("height", MAP_SIZE.toString())
					attr("width", MAP_SIZE.toString())
					onMouseMove { event ->
						val target = event.target as HTMLElement
						val x = (event.offsetX / target.clientWidth * MAP_SIZE).toInt().coerceIn(0, MAP_SIZE - 1)
						val z = (event.offsetY / target.clientHeight * MAP_SIZE).toInt().coerceIn(0, MAP_SIZE - 1)
						hover = MapHover(event.offsetX, event.offsetY, world[z * MAP_SIZE + x])
					}
					onMouseLeave { hover = null }
				}) {
					DisposableEffect(world) {
						val context = scopeElement.getContext("2d") as CanvasRenderingContext2D
						val image = context.createImageData(MAP_SIZE.toDouble(), MAP_SIZE.toDouble())
						val pixels = image.data.asDynamic()
						world.indices.forEach { index ->
							val rgb = mapPixel(world, index, seaLevel)
							pixels[index * 4] = rgb shr 16
							pixels[index * 4 + 1] = rgb shr 8 and 0xff
							pixels[index * 4 + 2] = rgb and 0xff
							pixels[index * 4 + 3] = 255
						}
						context.putImageData(image, 0.0, 0.0)
						onDispose {}
					}
				}
				hover?.let {
					Div({
						classes(FeatureVisualsStyle.mapTooltip)
						style {
							left((it.x + 38).px)
							top((it.y - 10).px)
						}
					}) {
						McTooltip {
							McLine(it.column.biome.label)
							McLine("minecraft:${it.column.biome.name.lowercase()}", McColor.DARK_GRAY)
							McLine("Y ${it.column.height}", McColor.GRAY)
						}
					}
				}
			}

			Div({ classes(FeatureVisualsStyle.knobs) }) {
				Knob("seaLevel", SEA_LEVELS, seaLevel, Int::toString) { seaLevel = it }
				Knob("desert temperature", DESERT_TEMPERATURES, desertMinTemperature, Double::toString) { desertMinTemperature = it }
			}
		}

		val code = """
			val terrain = noiseSettings("islands") {
				seaLevel = $seaLevel
			}

			dimension("islands", type = islandsType) {
				noiseGenerator(settings = terrain, biomeSource = multiNoise {
					add(multiNoiseEntry(Biomes.DESERT) {
						temperature = doubleOrPair($desertMinTemperature, 1.0)
					})
					// forest, plains, snowy plains, peaks...
				})
			}
		""".trimIndent()
		Window("Islands.kt") {
			Div({ id(WORLDGEN_CODE_ID) }) {
				key(code) { CodeBlock(code, "kotlin") }
			}
		}
		highlightCodeIn(WORLDGEN_CODE_ID, code)
	}
}

@Composable
private fun <T> Knob(label: String, options: List<T>, selected: T, format: (T) -> String, onSelect: (T) -> Unit) {
	Div({ classes(FeatureVisualsStyle.knob) }) {
		Span(label, FeatureVisualsStyle.knobLabel)
		Div({ classes(FeatureVisualsStyle.knobOptions) }) {
			options.forEach { option ->
				Button({
					classes(FeatureVisualsStyle.pill)
					if (option == selected) classes(FeatureVisualsStyle.pillActive)
					onClick { onSelect(option) }
				}) { Text(format(option)) }
			}
		}
	}
}

// --- Gameplay ---

private enum class GamePhase { LOBBY, PLAYING, ENDED }

private data class ChatMessage(val text: String, val color: String = McColor.WHITE)

private data class GameTitle(val title: String, val subtitle: String, val color: String = McColor.WHITE)

private const val ROUND_SECONDS = 20
private const val TICK_MS = 100L
private const val DASH_COOLDOWN_TICKS = 25

private val LOBBY_HOTBAR = listOf("nether_star")
private val KIT_HOTBAR = listOf("iron_sword", "bow", "feather", "cooked_beef")
private const val DASH_SLOT = 2

/** Each control drives one oop feature: state machine, timer, events, team scores, cooldowns and player commands. */
@Composable
fun GameplayScene() {
	var phase by remember { mutableStateOf(GamePhase.LOBBY) }
	var ticks by remember { mutableStateOf(0) }
	var red by remember { mutableStateOf(0) }
	var blue by remember { mutableStateOf(0) }
	var health by remember { mutableStateOf(20) }
	var dashTicks by remember { mutableStateOf(0) }
	var dashes by remember { mutableStateOf(0) }
	var title by remember { mutableStateOf<GameTitle?>(null) }
	val chat = remember { mutableStateListOf(ChatMessage("<Steve> gl hf")) }
	val roundTicks = ROUND_SECONDS * 1000 / TICK_MS.toInt()
	val remaining = 1.0 - ticks.toDouble() / roundTicks
	val night = phase == GamePhase.PLAYING && remaining < 0.5

	LaunchedEffect(phase) {
		if (phase != GamePhase.PLAYING) return@LaunchedEffect
		title = GameTitle("Fight!", "Round 1")
		while (ticks < roundTicks) {
			delay(TICK_MS)
			ticks++
			when (ticks) {
				roundTicks / 5 -> title = null
				roundTicks * 3 / 10 -> {
					blue++
					chat += ChatMessage("Steve was slain by Alex", McColor.GRAY)
				}
				roundTicks / 2 -> chat += ChatMessage("Night falls, mobs are spawning", McColor.GOLD)
			}
		}
		val redWins = red >= blue
		chat += ChatMessage("${if (redWins) "Red" else "Blue"} wins $red - $blue", McColor.YELLOW)
		title = GameTitle("GG", "${if (redWins) "Red" else "Blue"} wins", if (redWins) McColor.RED else McColor.BLUE)
		phase = GamePhase.ENDED
	}

	LaunchedEffect(dashes) {
		if (dashes == 0) return@LaunchedEffect
		dashTicks = DASH_COOLDOWN_TICKS
		while (dashTicks > 0) {
			delay(TICK_MS)
			dashTicks--
		}
	}

	val playing = phase == GamePhase.PLAYING
	val secondsLeft = (remaining * ROUND_SECONDS).toInt()

	Div({ classes(FeatureVisualsStyle.gameWrapper) }) {
		Div({
			classes(McUiStyle.world, FeatureVisualsStyle.mcScene)
			style { property("background-image", mcPanorama(3)) }
		}) {
			Div({
				classes(FeatureVisualsStyle.nightOverlay)
				if (night) classes(FeatureVisualsStyle.nightOverlayVisible)
			})

			Div({ classes(FeatureVisualsStyle.hudTop) }) {
				when (phase) {
					GamePhase.LOBBY -> McBossBar("Waiting for players 3/8", McBossBarColor.YELLOW, 3 / 8.0)
					GamePhase.PLAYING -> McBossBar("Round ends in 0:${secondsLeft.toString().padStart(2, '0')}", McBossBarColor.RED, remaining, notches = 10)
					GamePhase.ENDED -> {}
				}
			}

			title?.let {
				Div({ classes(FeatureVisualsStyle.hudTitle) }) { McLine(it.title, it.color, scale = 4) }
				Div({ classes(FeatureVisualsStyle.hudSubtitle) }) { McLine(it.subtitle, scale = 2) }
			}

			Div({ classes(FeatureVisualsStyle.hudSidebar) }) {
				McSidebar(
					"SKY WARS", McColor.GOLD, listOf(
						McSidebarLine("State", value = phase.name.lowercase()),
						McSidebarLine(""),
						McSidebarLine("Red", McColor.RED, red.toString()),
						McSidebarLine("Blue", McColor.BLUE, blue.toString()),
						McSidebarLine(""),
						McSidebarLine("Time", value = "0:${secondsLeft.toString().padStart(2, '0')}"),
					)
				)
			}

			McChat(FeatureVisualsStyle.hudChat, FeatureVisualsStyle.aboveHotbar) {
				chat.takeLast(2).forEach { McChatLine(it.text, it.color) }
			}

			Div({ classes(FeatureVisualsStyle.hudHotbar) }) {
				McHotbar(
					if (phase == GamePhase.LOBBY) LOBBY_HOTBAR else KIT_HOTBAR,
					enchanted = setOf("nether_star"),
					health = health,
					cooldowns = if (dashTicks > 0) mapOf(DASH_SLOT to dashTicks.toDouble() / DASH_COOLDOWN_TICKS) else emptyMap(),
				)
			}
		}

		Div({ classes(FeatureVisualsStyle.stateMachine) }) {
			GamePhase.entries.forEachIndexed { index, state ->
				if (index > 0) Span({ classes(FeatureVisualsStyle.stateArrow) }) { Text("→") }
				Span({
					classes(FeatureVisualsStyle.pill)
					if (state == phase) classes(FeatureVisualsStyle.pillActive)
				}) { Text(state.name) }
			}
		}

		Div({ classes(FeatureVisualsStyle.controls) }) {
			McButton("game.start()", enabled = phase == GamePhase.LOBBY) {
				ticks = 0
				phase = GamePhase.PLAYING
			}
			McButton("red.score += 1", enabled = playing) {
				red++
				chat += ChatMessage("Alex was slain by Steve", McColor.GRAY)
			}
			McButton("dash.use()", enabled = playing && dashTicks == 0) { dashes++ }
			McButton("player.damage(4f)", enabled = playing && health > 0) { health = (health - 4).coerceAtLeast(0) }
			McButton("game.reset()", enabled = phase != GamePhase.LOBBY) {
				phase = GamePhase.LOBBY
				ticks = 0
				red = 0
				blue = 0
				health = 20
				title = null
			}
		}
	}
}

// --- Helpers ---

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
		if (reached == steps) McChat(FeatureVisualsStyle.hudChat) { McChatLine("Target acquired!") }
		Span("raycast { step = 0.25; onHitBlock { ... } }", FeatureVisualsStyle.sceneLabel, FeatureVisualsStyle.labelLeft)
	}
}

@Composable
fun MenuScene() {
	var pressed by remember { mutableStateOf<String?>(null) }
	Div({
		classes(McUiStyle.world, McUiStyle.menuBackground, FeatureVisualsStyle.mcScene, FeatureVisualsStyle.dialogScene)
		style { property("background-image", mcPanorama(0)) }
	}) {
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

// --- Build ---

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

	val tooltipPlacement by style {
		marginRight(1.cssRem)
		marginTop((-3).cssRem)
		property("align-self", "flex-end")

		smMax(self) {
			marginRight(0.px)
			marginTop((-1).cssRem)
			property("align-self", "center")
		}
	}

	val chatScreen by style {
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.cssRem)
		minHeight(9.cssRem)
		overflow(Overflow.Hidden)
		padding(1.cssRem)
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

	val toastPlacement by style {
		property("align-self", "flex-end")
	}

	val mapRow by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(1.2.cssRem)
		justifyContent(JustifyContent.Center)
	}

	/** Vanilla draws `map_background` 7 px around the 128 px map. */
	val mapFrame by style {
		flexShrink(0)
		height(gui(142))
		padding(gui(7))
		position(Position.Relative)
		width(gui(142))
		property("background", "${mcTexture("map/map_background")} 0 0 / 100% 100%")
		property("box-sizing", "border-box")
		property("image-rendering", "pixelated")
	}

	val mapCanvas by style {
		cursor(Cursor.Crosshair)
		display(DisplayStyle.Block)
		height(100.percent)
		width(100.percent)
		property("image-rendering", "pixelated")
	}

	val mapTooltip by style {
		position(Position.Absolute)
		zIndex(1)
		property("pointer-events", "none")
	}

	val knobs by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.9.cssRem)
	}

	val knob by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.35.cssRem)
	}

	val knobLabel by style {
		color(Color("var(--landing-muted)"))
		fontFamily(MONO, "monospace")
		fontSize(0.75.cssRem)
	}

	val knobOptions by style {
		display(DisplayStyle.Flex)
		gap(0.35.cssRem)
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

	/** The frame of scenes drawing the world through [McUiStyle.world]. */
	val mcScene by style {
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.cssRem)
		boxShadow(0.px, 24.px, 60.px, 0.px, rgba(5, 12, 20, 0.5))
		overflow(Overflow.Hidden)
		property("aspect-ratio", "16 / 10")
	}

	val dialogScene by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)
	}

	val dialog by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.5.cssRem)
		width(60.percent)

		"button" style { width(100.percent) }
	}

	val nightOverlay by style {
		backgroundColor(rgba(8, 12, 40, 0.6))
		opacity(0)
		position(Position.Absolute)
		transition(1.5.s, "opacity")
		property("inset", 0.px)
	}

	val nightOverlayVisible by style {
		opacity(1)
	}

	/** HUD placements follow the game: boss bars from the top, titles around the center, sidebar on the right edge. */
	val hudTop by style {
		position(Position.Absolute)
		top(gui(3))
		property("left", PIXEL_HALF)
		property("transform", pixelTranslate("-50%"))
	}

	val hudTitle by style {
		position(Position.Absolute)
		property("left", PIXEL_HALF)
		property("top", PIXEL_HALF)
		property("transform", pixelTranslate("-50%", gui(-40).toString()))
	}

	val hudSubtitle by style {
		position(Position.Absolute)
		property("left", PIXEL_HALF)
		property("top", PIXEL_HALF)
		property("transform", pixelTranslate("-50%", gui(10).toString()))
	}

	val hudSidebar by style {
		position(Position.Absolute)
		right(gui(1))
		property("top", PIXEL_HALF)
		property("transform", pixelTranslate("0px", "-50%"))
	}

	val hudChat by style {
		bottom(gui(8))
		left(0.px)
		position(Position.Absolute)
	}

	/** The game keeps the chat 40 px above the bottom, clear of the hotbar. */
	val aboveHotbar by style {
		bottom(gui(40))
	}

	val hudHotbar by style {
		bottom(0.px)
		position(Position.Absolute)
		property("left", PIXEL_HALF)
		property("transform", pixelTranslate("-50%"))
	}

	val stateMachine by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.4.cssRem)
	}

	val stateArrow by style {
		color(Color("var(--landing-muted)"))
	}

	val controls by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
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
		property("background-image", mcTexture("particle/flame"))
		property("filter", "drop-shadow(0 0 4px rgba(255, 140, 30, 0.8))")
	}

	/** First frame of the `end_rod` particle animation. */
	val endRod by style {
		property("background-image", mcTexture("particle/glitter_7"))
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
		property("background", "${mcTexture("block/target_side")} center / cover")
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
