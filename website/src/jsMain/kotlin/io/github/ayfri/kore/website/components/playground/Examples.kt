package io.github.ayfri.kore.website.components.playground

/**
 * Starter snippets for the playground. They follow the playground contract: the editor defines
 * `fun playground(): DataPack`, and the hidden harness supplies `main()` around it.
 *
 * Every snippet compiles against the published `kore` module only, so none of them may reach for
 * `oop`, `helpers` or `bindings` - the compile backend has just `kore` on its classpath.
 */
data class PlaygroundExample(
	val title: String,
	val description: String,
	val category: String,
	val code: String,
)

const val BASICS_CATEGORY = "Basics"
const val DATA_DRIVEN_CATEGORY = "Data-driven"
const val GAMEPLAY_CATEGORY = "Gameplay"

val playgroundExamples = listOf(
	PlaygroundExample(
		title = "Hello world",
		description = "A pack with a load function and a greeting.",
		category = BASICS_CATEGORY,
		code = """
			import io.github.ayfri.kore.arguments.chatcomponents.textComponent
			import io.github.ayfri.kore.arguments.types.literals.allPlayers
			import io.github.ayfri.kore.commands.tellraw
			import io.github.ayfri.kore.dataPack
			import io.github.ayfri.kore.functions.function
			import io.github.ayfri.kore.functions.load
			import io.github.ayfri.kore.pack.pack

			fun playground() = dataPack("starter_kore") {
				pack { description = textComponent("Starter datapack generated with Kore") }

				load("bootstrap") {
					tellraw(allPlayers(), textComponent("[starter_kore] loaded"))
				}

				function("hello") {
					tellraw(allPlayers(), textComponent("Hello from Kore"))
				}
			}
		""".trimIndent(),
	),
	PlaygroundExample(
		title = "Load and tick",
		description = "Split one-time setup from the per-tick loop.",
		category = BASICS_CATEGORY,
		code = """
			import io.github.ayfri.kore.arguments.chatcomponents.textComponent
			import io.github.ayfri.kore.arguments.types.literals.allPlayers
			import io.github.ayfri.kore.commands.execute.execute
			import io.github.ayfri.kore.commands.say
			import io.github.ayfri.kore.commands.scoreboard.scoreboard
			import io.github.ayfri.kore.dataPack
			import io.github.ayfri.kore.functions.load
			import io.github.ayfri.kore.functions.tick
			import io.github.ayfri.kore.pack.pack

			fun playground() = dataPack("arena") {
				pack { description = textComponent("Bootstrap and tick lifecycles") }

				load("setup") {
					scoreboard.objectives.add("round")
					scoreboard.objectives.add("lives")
				}

				tick("game_loop") {
					execute {
						asTarget(allPlayers())
						run {
							say("tick")
						}
					}
				}
			}
		""".trimIndent(),
	),
	PlaygroundExample(
		title = "Selectors",
		description = "Name a selector once, reuse it everywhere.",
		category = BASICS_CATEGORY,
		code = """
			import io.github.ayfri.kore.arguments.chatcomponents.textComponent
			import io.github.ayfri.kore.arguments.enums.Gamemode
			import io.github.ayfri.kore.arguments.scores.greaterThan
			import io.github.ayfri.kore.arguments.scores.greaterThanOrEqualTo
			import io.github.ayfri.kore.arguments.selector.scores
			import io.github.ayfri.kore.arguments.types.literals.allPlayers
			import io.github.ayfri.kore.commands.effect
			import io.github.ayfri.kore.commands.tellraw
			import io.github.ayfri.kore.dataPack
			import io.github.ayfri.kore.functions.function
			import io.github.ayfri.kore.generated.Effects
			import io.github.ayfri.kore.pack.pack

			fun playground() = dataPack("arena_selectors") {
				pack { description = textComponent("Named selectors reused everywhere") }

				val activePlayers = allPlayers {
					scores {
						"round" greaterThanOrEqualTo 1
						"lives" greaterThan 0
					}
					gamemode = !Gamemode.SPECTATOR
				}

				function("start_wave") {
					effect(activePlayers) { give(Effects.RESISTANCE, duration = 5, amplifier = 0) }
					tellraw(activePlayers, textComponent("Wave started"))
				}
			}
		""".trimIndent(),
	),
	PlaygroundExample(
		title = "Advancement",
		description = "A data-driven feature: JSON generated from typed Kotlin.",
		category = DATA_DRIVEN_CATEGORY,
		code = """
			import io.github.ayfri.kore.arguments.chatcomponents.textComponent
			import io.github.ayfri.kore.dataPack
			import io.github.ayfri.kore.features.advancements.AdvancementFrameType
			import io.github.ayfri.kore.features.advancements.advancement
			import io.github.ayfri.kore.features.advancements.display
			import io.github.ayfri.kore.generated.Items
			import io.github.ayfri.kore.pack.pack

			fun playground() = dataPack("advancement_demo") {
				pack { description = textComponent("Advancement demo") }

				advancement("root") {
					display(Items.DIAMOND_SWORD, "Getting started", "Made with Kore") {
						frame = AdvancementFrameType.CHALLENGE
					}
				}
			}
		""".trimIndent(),
	),
	PlaygroundExample(
		title = "Recipes",
		description = "A shaped crafting recipe and a blasting recipe.",
		category = DATA_DRIVEN_CATEGORY,
		code = """
			import io.github.ayfri.kore.arguments.chatcomponents.textComponent
			import io.github.ayfri.kore.dataPack
			import io.github.ayfri.kore.features.recipes.recipes
			import io.github.ayfri.kore.features.recipes.types.blasting
			import io.github.ayfri.kore.features.recipes.types.craftingShaped
			import io.github.ayfri.kore.features.recipes.types.ingredient
			import io.github.ayfri.kore.features.recipes.types.key
			import io.github.ayfri.kore.features.recipes.types.pattern
			import io.github.ayfri.kore.features.recipes.types.result
			import io.github.ayfri.kore.generated.Items
			import io.github.ayfri.kore.generated.Tags
			import io.github.ayfri.kore.pack.pack

			fun playground() = dataPack("custom_recipes") {
				pack { description = textComponent("Crafting and smelting recipes") }

				recipes {
					craftingShaped("diamond_stick") {
						pattern(
							" D ",
							" D ",
							" S "
						)

						key("D", Items.DIAMOND)
						key("S", Items.STICK)

						result(Items.DIAMOND_SWORD)
					}

					blasting("stone_to_diamond") {
						ingredient(Tags.Item.STONE_CRAFTING_MATERIALS)

						result(Items.DIAMOND)
						cookingTime = 200
						experience = 10.0
					}
				}
			}
		""".trimIndent(),
	),
	PlaygroundExample(
		title = "Loot table",
		description = "Pools, entries and conditions, rolled from a function.",
		category = DATA_DRIVEN_CATEGORY,
		code = """
			import io.github.ayfri.kore.arguments.chatcomponents.textComponent
			import io.github.ayfri.kore.arguments.types.literals.self
			import io.github.ayfri.kore.commands.loot
			import io.github.ayfri.kore.dataPack
			import io.github.ayfri.kore.features.itemmodifiers.functions.setCount
			import io.github.ayfri.kore.features.loottables.conditions
			import io.github.ayfri.kore.features.loottables.entries
			import io.github.ayfri.kore.features.loottables.entries.item
			import io.github.ayfri.kore.features.loottables.functions
			import io.github.ayfri.kore.features.loottables.lootTable
			import io.github.ayfri.kore.features.loottables.pool
			import io.github.ayfri.kore.features.predicates.conditions.randomChance
			import io.github.ayfri.kore.features.predicates.providers.constant
			import io.github.ayfri.kore.functions.function
			import io.github.ayfri.kore.generated.Items
			import io.github.ayfri.kore.pack.pack

			fun playground() = dataPack("treasure") {
				pack { description = textComponent("A loot table rolled from a function") }

				val bonusChest = lootTable("bonus_chest") {
					pool {
						rolls = constant(3f)

						entries {
							item(Items.DIAMOND) {
								functions {
									setCount(2f)
								}
							}

							item(Items.EMERALD)
						}

						conditions {
							randomChance(0.5f)
						}
					}
				}

				function("open_chest") {
					loot(self(), bonusChest)
				}
			}
		""".trimIndent(),
	),
	PlaygroundExample(
		title = "Tags",
		description = "Block and item tags, then a check against one of them.",
		category = DATA_DRIVEN_CATEGORY,
		code = """
			import io.github.ayfri.kore.arguments.chatcomponents.textComponent
			import io.github.ayfri.kore.arguments.maths.vec3
			import io.github.ayfri.kore.arguments.types.literals.allPlayers
			import io.github.ayfri.kore.commands.execute.execute
			import io.github.ayfri.kore.commands.say
			import io.github.ayfri.kore.dataPack
			import io.github.ayfri.kore.features.tags.blockTag
			import io.github.ayfri.kore.features.tags.itemTag
			import io.github.ayfri.kore.functions.function
			import io.github.ayfri.kore.generated.Blocks
			import io.github.ayfri.kore.generated.Items
			import io.github.ayfri.kore.pack.pack

			fun playground() = dataPack("arena_tags") {
				pack { description = textComponent("Block and item tags") }

				val arenaFloor = blockTag("arena_floor") {
					add(Blocks.STONE)
					add(Blocks.POLISHED_ANDESITE)
					this += Blocks.SMOOTH_STONE
				}

				itemTag("gems") {
					add(Items.DIAMOND)
					add(Items.EMERALD)
					add("mymod:ruby", required = false)
				}

				function("check_floor") {
					execute {
						asTarget(allPlayers())
						ifCondition {
							block(vec3(), arenaFloor)
						}
						run {
							say("Standing on the arena floor")
						}
					}
				}
			}
		""".trimIndent(),
	),
	PlaygroundExample(
		title = "Custom item",
		description = "Item components, and the predicate that recognizes the item later.",
		category = GAMEPLAY_CATEGORY,
		code = """
			import io.github.ayfri.kore.arguments.chatcomponents.textComponent
			import io.github.ayfri.kore.arguments.colors.Color
			import io.github.ayfri.kore.arguments.components.item.customName
			import io.github.ayfri.kore.arguments.components.item.enchantments
			import io.github.ayfri.kore.arguments.components.item.lore
			import io.github.ayfri.kore.arguments.types.literals.allPlayers
			import io.github.ayfri.kore.arguments.types.literals.self
			import io.github.ayfri.kore.commands.execute.execute
			import io.github.ayfri.kore.commands.give
			import io.github.ayfri.kore.commands.say
			import io.github.ayfri.kore.dataPack
			import io.github.ayfri.kore.features.predicates.conditions.matchTool
			import io.github.ayfri.kore.features.predicates.predicate
			import io.github.ayfri.kore.functions.function
			import io.github.ayfri.kore.generated.Enchantments
			import io.github.ayfri.kore.generated.Items
			import io.github.ayfri.kore.pack.pack

			fun playground() = dataPack("arena_blade") {
				pack { description = textComponent("A custom item and the predicate that recognizes it") }

				val arenaBlade = Items.DIAMOND_SWORD {
					customName(textComponent("Arena Blade", Color.AQUA))
					lore("Forged for the arena")
					enchantments {
						Enchantments.SHARPNESS to 5
					}
				}

				val holdingArenaBlade = predicate("holding_arena_blade") {
					matchTool(arenaBlade)
				}

				function("give_blade") {
					give(allPlayers(), arenaBlade)
				}

				function("check_weapon") {
					execute {
						asTarget(self())
						ifCondition(holdingArenaBlade)
						run {
							say("Correct weapon equipped")
						}
					}
				}
			}
		""".trimIndent(),
	),
	PlaygroundExample(
		title = "Scheduling",
		description = "Telegraph an action now, run it a few seconds later.",
		category = GAMEPLAY_CATEGORY,
		code = """
			import io.github.ayfri.kore.arguments.chatcomponents.textComponent
			import io.github.ayfri.kore.arguments.colors.Color
			import io.github.ayfri.kore.arguments.maths.vec3
			import io.github.ayfri.kore.arguments.numbers.seconds
			import io.github.ayfri.kore.arguments.types.literals.allPlayers
			import io.github.ayfri.kore.commands.function
			import io.github.ayfri.kore.commands.schedule
			import io.github.ayfri.kore.commands.summon
			import io.github.ayfri.kore.commands.tellraw
			import io.github.ayfri.kore.dataPack
			import io.github.ayfri.kore.functions.function
			import io.github.ayfri.kore.generated.EntityTypes
			import io.github.ayfri.kore.pack.pack

			fun playground() = dataPack("telegraph") {
				pack { description = textComponent("Delay an action with schedule") }

				val explosionWarning = function("explosion_warning") {
					tellraw(allPlayers(), textComponent("Boom in 5 seconds!", Color.RED))
				}

				val explodeNow = function("explode_now") {
					summon(EntityTypes.TNT, vec3())
				}

				function("trigger_explosion") {
					function(explosionWarning)
					schedule(explodeNow).replace(5.seconds)
				}
			}
		""".trimIndent(),
	),
	PlaygroundExample(
		title = "Macros",
		description = "A function parameterized at runtime, with typed macro names.",
		category = GAMEPLAY_CATEGORY,
		code = """
			import io.github.ayfri.kore.arguments.chatcomponents.textComponent
			import io.github.ayfri.kore.arguments.maths.vec3
			import io.github.ayfri.kore.arguments.types.literals.player
			import io.github.ayfri.kore.commands.function
			import io.github.ayfri.kore.commands.teleport
			import io.github.ayfri.kore.dataPack
			import io.github.ayfri.kore.functions.Macros
			import io.github.ayfri.kore.functions.function
			import io.github.ayfri.kore.functions.getValue
			import io.github.ayfri.kore.functions.load
			import io.github.ayfri.kore.pack.pack
			import io.github.ayfri.kore.utils.nbt
			import io.github.ayfri.kore.utils.set

			class TeleportMacros : Macros() {
				val player by "player"
			}

			fun playground() = dataPack("macros_demo") {
				pack { description = textComponent("Functions parameterized with macros") }

				val teleportToSpawn = function("teleport_to_spawn", ::TeleportMacros) {
					teleport(player(macros.player), vec3())
				}

				load("bootstrap") {
					function(teleportToSpawn, arguments = nbt { this["player"] = "jeb_" })
				}
			}
		""".trimIndent(),
	),
)

/** Examples grouped for the picker, in declaration order so a category keeps the order it was written in. */
val playgroundExamplesByCategory = playgroundExamples.groupBy { it.category }

val defaultExample = playgroundExamples.first()
