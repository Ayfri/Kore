package io.github.ayfri.kore.website.components.features

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.silk.components.icons.lucide.*

/** [scene] is the index of the category scene previewed when hovering the item, [logos] are `/icons/<name>.svg` brand icons. */
data class FeatureItem(
	val name: String,
	val description: String,
	val href: String,
	val scene: Int? = null,
	val logos: List<String> = emptyList(),
)

/** A resource name shown as a chip, [href] is null when the resource has no dedicated guide. */
data class ResourceChip(
	val name: String,
	val href: String? = null,
	val scene: Int? = null,
)

/** An interactive visual of a category, [content] receives a callback to switch to another scene of the same category. */
data class Scene(
	val name: String,
	val content: @Composable (open: (Int) -> Unit) -> Unit,
)

/** A page section: [title] labels it in the nav, [headline] sells it, and [scenes] (when any) sit next to the text. */
data class FeatureCategory(
	val id: String,
	val title: String,
	val headline: String,
	val tagline: String,
	val icon: @Composable () -> Unit,
	val items: List<FeatureItem> = emptyList(),
	val chips: List<ResourceChip> = emptyList(),
	val scenes: List<Scene> = emptyList(),
)

data class FeatureStat(
	val value: String,
	val label: String,
)

data class ShowcaseFile(
	val path: String,
	val language: String,
	val code: String,
)

data class Showcase(
	val name: String,
	val kotlin: String,
	val outputs: List<ShowcaseFile>,
)

val featureStats = listOf(
	FeatureStat("65+", "typed commands"),
	FeatureStat("50+", "JSON resource types"),
	FeatureStat("80+", "generated registries"),
	FeatureStat("4", "mod loaders to export to"),
)

val showcases = listOf(
	Showcase(
		"Loot table",
		"""
			dataPack("arena") {
				lootTable("boss_chest") {
					pool {
						rolls = constant(3f)
						entries {
							item(Items.DIAMOND) { weight = 1 }
							item(Items.GOLDEN_APPLE) { weight = 4 }
							item(Items.IRON_INGOT) { weight = 10 }
						}
					}
				}
			}
		""".trimIndent(),
		listOf(
			ShowcaseFile(
				"data/arena/loot_table/boss_chest.json",
				"json",
				"""
					{
						"pools": [
							{
								"rolls": 3.0,
								"entries": [
									{
										"type": "minecraft:item",
										"name": "minecraft:diamond",
										"weight": 1
									},
									{
										"type": "minecraft:item",
										"name": "minecraft:golden_apple",
										"weight": 4
									},
									{
										"type": "minecraft:item",
										"name": "minecraft:iron_ingot",
										"weight": 10
									}
								]
							}
						]
					}
				""".trimIndent(),
			),
		),
	),
	Showcase(
		"Execute chain",
		"""
			dataPack("arena") {
				function("reward_winners") {
					execute {
						asTarget(allPlayers {
							scores { score("kills") greaterThanOrEqualTo 10 }
						})
						at(self())
						run {
							effect(self()) { give(Effects.GLOWING, 10, 0, true) }
							playSound(SoundEvents.Entity.Player.LEVELUP, PlaySoundMixer.MASTER, self())
							title(self(), TitleLocation.TITLE, textComponent("Champion!", Color.GOLD))
						}
					}
				}
			}
		""".trimIndent(),
		listOf(
			ShowcaseFile(
				"data/arena/function/reward_winners.mcfunction",
				"mcfunction",
				"execute as @a[scores={kills=10..}] at @s run function arena:generated_scopes/generated_278536229",
			),
			ShowcaseFile(
				"data/arena/function/generated_scopes/generated_278536229.mcfunction",
				"mcfunction",
				"""
					effect give @s minecraft:glowing 10 0 true
					playsound minecraft:entity.player.levelup master @s
					title @s title {type:"text",color:"gold",text:"Champion!"}
				""".trimIndent(),
			),
		),
	),
)

val featureCategories = listOf(
	FeatureCategory(
		id = "commands",
		title = "Commands & functions",
		headline = "The whole command set, autocompleted",
		tagline = "Every vanilla command as a typed Kotlin call. Your IDE lists every item, block and effect, and the compiler rejects a typo before /reload ever sees it.",
		icon = { LucideTerminal() },
		scenes = listOf(
			Scene("Autocomplete") { AutocompleteScene() },
			Scene("Macros") { MacrosScene() },
			Scene("Execute & selectors") { SelectorsScene() },
		),
		items = listOf(
			FeatureItem("Every vanilla command", "From /say to /worldborder, with typed arguments, enums and named parameters.", "/docs/commands/commands", scene = 0),
			FeatureItem("Execute builder", "as, at, positioned, if/unless and store chains. A multi-command run block becomes its own function automatically.", "/docs/commands/execute", scene = 2),
			FeatureItem("Functions & lifecycle tags", "Namespaced functions, load and tick tags, and calls between functions by reference.", "/docs/commands/functions", scene = 1),
			FeatureItem("Macros", "Macro functions with typed arguments, called with literal values or straight from storage.", "/docs/commands/macros", scene = 1),
			FeatureItem("Target selectors", "@a, @e, @s and friends built with filters, sorting, limits and score conditions.", "/docs/concepts/selectors", scene = 2),
			FeatureItem("Raw escape hatch", "Anything not covered yet is one addLine(\"...\") away, so the DSL never blocks you.", "/docs/commands/functions"),
		),
	),
	FeatureCategory(
		id = "game-data",
		title = "Game data, typed",
		headline = "Items, text and NBT as real objects",
		tagline = "Build an item once with its name, lore and components, then reuse it anywhere an item is expected.",
		icon = { LucideBraces() },
		scenes = listOf(
			Scene("Item tooltip") { TooltipScene() },
			Scene("Chat") { ChatScene() },
			Scene("Storage") { StorageScene() },
		),
		items = listOf(
			FeatureItem("Chat components", "Text, translations, scores, selectors, hover and click events, with + to concatenate.", "/docs/concepts/chat-components", scene = 1),
			FeatureItem("Item components", "Every item component, from custom names to tools, food and equippables.", "/docs/concepts/components", scene = 0),
			FeatureItem("NBT & SNBT", "One NBT DSL for commands, predicates and chat components, with path helpers.", "/docs/concepts/nbts", scene = 2),
			FeatureItem("Data storage", "Storage as runtime variables for strings, lists and compounds beyond integers.", "/docs/concepts/data-storage", scene = 2),
			FeatureItem("Scoreboards", "Objectives, display slots and score operations without the string juggling.", "/docs/concepts/scoreboards"),
			FeatureItem("Time & colors", "Ticks, seconds and days with arithmetic, plus named, RGB and ARGB colors serialized per context.", "/docs/concepts/time"),
			FeatureItem("Runtime logic", "How Kotlin at build time maps to scoreboards, storage and execute at runtime.", "/docs/concepts/runtime-logic"),
		),
	),
	FeatureCategory(
		id = "data-driven",
		title = "Data-driven resources",
		headline = "Every JSON file, without writing JSON",
		tagline = "Each resource is a Kotlin builder that serializes to the exact vanilla schema and lands in the right folder.",
		icon = { LucideFileBraces() },
		scenes = listOf(
			Scene("Datapack tree") { open -> FileTreeScene(open) },
			Scene("Loot table") { LootTableScene() },
			Scene("Recipe") { RecipeScene() },
			Scene("Advancement") { AdvancementScene() },
		),
		chips = listOf(
			ResourceChip("Advancements", "/docs/data-driven/advancements", scene = 3),
			ResourceChip("Advancement triggers", "/docs/data-driven/advancements/triggers"),
			ResourceChip("Banner patterns"),
			ResourceChip("Chat types"),
			ResourceChip("Damage types"),
			ResourceChip("Dialogs", "/docs/data-driven/dialogs"),
			ResourceChip("Enchantments", "/docs/data-driven/enchantments"),
			ResourceChip("Enchantment providers", "/docs/data-driven/enchantments"),
			ResourceChip("GameTest instances", "/docs/advanced/test-features"),
			ResourceChip("GameTest environments", "/docs/advanced/test-features"),
			ResourceChip("Instruments"),
			ResourceChip("Item modifiers", "/docs/data-driven/item-modifiers"),
			ResourceChip("Jukebox songs"),
			ResourceChip("Loot tables", "/docs/data-driven/loot-tables", scene = 1),
			ResourceChip("Mob variants", "/docs/data-driven/variants"),
			ResourceChip("Mob sound variants", "/docs/data-driven/variants"),
			ResourceChip("Painting variants", "/docs/data-driven/variants"),
			ResourceChip("Predicates", "/docs/data-driven/predicates"),
			ResourceChip("Recipes", "/docs/data-driven/recipes", scene = 2),
			ResourceChip("Sulfur cube archetypes", "/docs/data-driven/sulfur-cube-archetypes"),
			ResourceChip("Tags", "/docs/data-driven/tags"),
			ResourceChip("Timelines", "/docs/data-driven/timelines"),
			ResourceChip("Trade sets", "/docs/data-driven/villager-trades"),
			ResourceChip("Trial spawners", "/docs/data-driven/trial-spawners"),
			ResourceChip("Trim materials", "/docs/data-driven/trims"),
			ResourceChip("Trim patterns", "/docs/data-driven/trims"),
			ResourceChip("Villager trades", "/docs/data-driven/villager-trades"),
			ResourceChip("World clocks", "/docs/data-driven/world-clocks"),
		),
	),
	FeatureCategory(
		id = "worldgen",
		title = "World generation",
		headline = "Shape entire worlds from Kotlin",
		tagline = "Custom dimensions, biomes and structures, down to density functions and noise, without hand-editing thousand-line JSON.",
		icon = { LucideMountainSnow() },
		scenes = listOf(Scene("Biome map") { BiomeMapScene() }),
		chips = listOf(
			ResourceChip("Biomes", "/docs/data-driven/worldgen/biomes"),
			ResourceChip("Block predicates", "/docs/data-driven/worldgen/block-predicates"),
			ResourceChip("Carvers", "/docs/data-driven/worldgen/carvers"),
			ResourceChip("Configured features", "/docs/data-driven/worldgen/features"),
			ResourceChip("Density functions", "/docs/data-driven/worldgen/noise"),
			ResourceChip("Dimensions", "/docs/data-driven/worldgen/dimensions"),
			ResourceChip("Dimension types", "/docs/data-driven/worldgen/dimensions"),
			ResourceChip("Environment attributes", "/docs/data-driven/worldgen/environment-attributes"),
			ResourceChip("Flat level presets", "/docs/data-driven/worldgen/world-presets"),
			ResourceChip("Multi-noise parameter lists", "/docs/data-driven/worldgen/dimensions"),
			ResourceChip("Noise settings", "/docs/data-driven/worldgen/noise"),
			ResourceChip("Noises", "/docs/data-driven/worldgen/noise"),
			ResourceChip("Placed features", "/docs/data-driven/worldgen/features"),
			ResourceChip("Processor lists", "/docs/data-driven/worldgen/structures"),
			ResourceChip("Providers", "/docs/data-driven/worldgen/providers"),
			ResourceChip("Structure sets", "/docs/data-driven/worldgen/structures"),
			ResourceChip("Structures", "/docs/data-driven/worldgen/structures"),
			ResourceChip("Template pools", "/docs/data-driven/worldgen/structures"),
			ResourceChip("World presets", "/docs/data-driven/worldgen/world-presets"),
		),
	),
	FeatureCategory(
		id = "oop",
		title = "Gameplay objects",
		headline = "Minigame systems in a few lines",
		tagline = "The oop module turns teams, timers, boss bars and events into handles you create once and call like regular objects.",
		icon = { LucideGamepad2() },
		scenes = listOf(Scene("Minigame") { GameplayScene() }),
		items = listOf(
			FeatureItem("Entities & players", "Selector-backed handles with commands, effects and execute helpers built in.", "/docs/oop/entities-and-players"),
			FeatureItem("Events", "React to player and entity actions through advancement-based listeners.", "/docs/oop/events"),
			FeatureItem("World events", "Tick, weather and day/night triggers plus configurable intervals.", "/docs/oop/world-events"),
			FeatureItem("Game state machine", "Register states, transition between them and run hooks on change.", "/docs/oop/game-state-machine"),
			FeatureItem("Timers & cooldowns", "Countdown timers, boss bar timers and per-player cooldowns.", "/docs/oop/timers"),
			FeatureItem("Teams, scoreboards & boss bars", "Create, configure and update them through typed handles and operators.", "/docs/oop/teams"),
			FeatureItem("Items & spawners", "Create, spawn and serialize items, and batch-summon entities from reusable spawners.", "/docs/oop/items"),
			FeatureItem("Dynamic strings", "substring, split, join, replace, trim and case conversion at runtime.", "/docs/oop/dynamic-strings"),
		),
	),
	FeatureCategory(
		id = "helpers",
		title = "Ready-made helpers",
		headline = "The hard math, already solved",
		tagline = "Raycasts, particle shapes, trigonometry on scoreboards, menus and text rendering, written and tested so you don't have to.",
		icon = { LucideWandSparkles() },
		scenes = listOf(
			Scene("VFX particles") { ParticlesScene() },
			Scene("Raycast") { RaycastScene() },
			Scene("Menus") { MenuScene() },
		),
		items = listOf(
			FeatureItem("Raycasts", "Step-based raycasts with block hits, max distance and per-step callbacks.", "/docs/helpers/raycasts", scene = 1),
			FeatureItem("Scoreboard math", "Fixed-point sine, cosine, atan2 and exact integer square root on scores.", "/docs/helpers/scoreboard-math"),
			FeatureItem("Score vectors", "Runtime 3D vectors: dot and cross products, normalize, motion and teleports.", "/docs/helpers/score-vectors"),
			FeatureItem("Scheduler", "Delayed actions, repeating loops and timed callbacks without scoreboard chains.", "/docs/helpers/scheduler"),
			FeatureItem("State delegates", "Kotlin var properties backed by scoreboards or NBT storage.", "/docs/helpers/state-delegates"),
			FeatureItem("Menus & sidebars", "Dialog-based menus with buttons and sub-pages, and server-style sidebars.", "/docs/helpers/menus", scene = 2),
			FeatureItem("Inventory manager", "Listen to slot events and control player or block containers.", "/docs/helpers/inventory-manager"),
			FeatureItem("VFX particles", "Circles, lines, spheres, spirals and helixes in world, relative or local space.", "/docs/helpers/vfx-particles", scene = 0),
			FeatureItem("Displays & mannequins", "Block, item and text displays with transformations and interpolation, plus mannequins.", "/docs/helpers/display-entities"),
			FeatureItem("Text renderers", "Turn Markdown, MiniMessage or ANSI text into chat components.", "/docs/helpers/minimessage-renderer"),
			FeatureItem("Areas & NBT paths", "3D bounding boxes with spatial queries, and typed NBT paths.", "/docs/helpers/area"),
		),
	),
	FeatureCategory(
		id = "build",
		title = "Build & ship",
		headline = "From save to in‑game in one command",
		tagline = "Every save rebuilds the pack, copies it into your worlds and reloads the server. When it's ready, export it anywhere.",
		icon = { LucideRocket() },
		scenes = listOf(Scene("Terminal") { TerminalScene() }),
		items = listOf(
			FeatureItem("Gradle plugin", "Build the pack, copy it into your worlds and reload a running server over RCON on every change.", "/docs/guides/gradle-plugin", logos = listOf("gradle")),
			FeatureItem("Folder, zip or mod jar", "One call generates a folder, a zip, or a jar for Fabric, Forge, Quilt and NeoForge.", "/docs/guides/creating-a-datapack"),
			FeatureItem("Overlays & pack formats", "Target several game versions from one pack with overlays and supported format ranges.", "/docs/guides/creating-a-datapack"),
			FeatureItem("Merge existing packs", "Merge generated output with hand-written datapacks, lifecycle tags included.", "/docs/guides/creating-a-datapack"),
			FeatureItem("Optimization passes", "Prune dead functions, shorten execute chains, dedupe functions, or write your own pass.", "/docs/guides/optimization"),
			FeatureItem("Configuration", "Pretty-printed JSON, custom indentation, generated folder names and debug comments.", "/docs/guides/configuration"),
			FeatureItem("Automated publishing", "Ship every release to Modrinth and CurseForge from GitHub Actions.", "/docs/advanced/github-actions-publishing", logos = listOf("githubactions", "modrinth", "curseforge")),
			FeatureItem("Multiplatform", "Generate packs on the JVM, on Node.js, or as zip bytes right in the browser.", "/docs/advanced/multiplatform", logos = listOf("kotlin", "nodedotjs")),
		),
	),
	FeatureCategory(
		id = "ecosystem",
		title = "Ecosystem & interop",
		headline = "Fits the tools you already use",
		tagline = "Kore plays well with the datapacks, mods and editors around it.",
		icon = { LucidePuzzle() },
		items = listOf(
			FeatureItem("Datapack bindings", "Import any datapack, even from GitHub, and reference its functions and tags with type-safe Kotlin.", "/docs/advanced/bindings", logos = listOf("github")),
			FeatureItem("Lantern Load", "Full Lantern Load boilerplate, version publishing and dependency guards.", "/docs/guides/lantern-load"),
			FeatureItem("Fabric resource conditions", "Load recipes, advancements and loot tables only when mods, tags or flags are present.", "/docs/guides/fabric-resource-conditions"),
			FeatureItem("GameTest", "Write test instances and environments for Minecraft's built-in test framework.", "/docs/advanced/test-features"),
			FeatureItem("Editor plugins", "Kore Assistant browses every declaration as a datapack tree and flags calls to functions declared nowhere. A VS Code extension is also available.", "https://plugins.jetbrains.com/plugin/27025-kore-assistant", logos = listOf("intellijidea", "visualstudiocode")),
			FeatureItem("Project template", "Clone a working project with Gradle already set up and start writing your pack.", "https://github.com/Kore-Minecraft/Kore-Template", logos = listOf("github", "gradle")),
		),
	),
)
