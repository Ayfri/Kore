import arguments.*
import arguments.chatcomponents.clickEvent
import arguments.chatcomponents.events.openUrl
import arguments.chatcomponents.events.showText
import arguments.chatcomponents.hoverEvent
import arguments.chatcomponents.textComponent
import arguments.colors.RGB
import arguments.enums.Dimension
import arguments.enums.Gamemode
import arguments.numbers.*
import arguments.scores.score
import arguments.selector.SelectorNbtData
import arguments.selector.SelectorType
import arguments.selector.Sort
import arguments.selector.scores
import commands.*
import commands.execute.Anchor
import commands.execute.execute
import commands.execute.run
import features.featuresTests
import features.tags.tags
import functions.function
import generated.*
import generated.Attributes
import helpers.helpersTests
import io.github.cdimascio.dotenv.dotenv
import net.benwoodworth.knbt.addNbtCompound
import serialization.selectorTests
import java.util.*
import kotlin.io.path.Path

val configuration = dotenv()
val minecraftSaveTestPath = Path(configuration["TEST_FOLDER"])

fun DataPack.setTestPath() {
	path = minecraftSaveTestPath
}

fun main() {
	featuresTests()
	helpersTests()
	selectorTests()
	runUnitTests()

	val dataPack = dataPack("test") {
		setTestPath()

		loadTeams()
		unloadTeams()
		gradient()

		tags("minecraft") {
			tag("blocks", "op") {
				add("command_block")
				add("repeating_command_block")
				add("chain_command_block")
				add("barrier", required = true)
			}
		}

		function("test") {
			fun nearestEntity(block: SelectorNbtData.() -> Unit = {}) = allEntities(true) {
				sort = Sort.NEAREST
				block()
			}

			execute {
				storeResult {
					score(self(), "test")
				}

				run {
					attributes(self(), Attributes.GENERIC_ARMOR).get()
				}
			}

			advancements.grant(allPlayers {
				dx = 1.0
				level = rangeOrInt(1..5)
				gamemode = !Gamemode.CREATIVE
			}, Advancements.Story.IRON_TOOLS)

			clear(targets = allPlayers(), item = Items.STONE, maxCount = 1)

			bossBar("test") {
				remove()
				add(textComponent("test"))
				setColor(Color.BLUE)
				setStyle(BossBarStyle.NOTCHED_6)
				setMax(100)
				setVisible(true)
				setValue(50)
			}

			bossBars.list()

			addBlankLine()
			cloneFiltered(coordinate(20, 50, 30), coordinate(40, 60, 50), coordinate(), blockTag("wool"), CloneMode.MOVE)

			data(selector(SelectorType.ALL_ENTITIES, true) {
				advancements {
					Advancements.Story.IRON_TOOLS {
						this["witch"] = true
					}
				}

				scores {
					score("test") matches 4..8
				}

				gamemode = !Gamemode.SURVIVAL
				type = "zombie"
			}) {
				modify("Position") {
					set(nbtListOf(0.0, 150.0, 0.0))
				}

				addBlankLine()
				modify("Motion") {
					insert(1, vec3(2.relativePos, 2.pos, 2.pos), "test")
				}
			}

			defaultGamemode(Gamemode.SURVIVAL)

			effect(selector(SelectorType.ALL_PLAYERS)) {
				clear()
				give(Effects.REGENERATION, 10, 1, false)
			}

			attributes(nearestPlayer(), Attributes.GENERIC_ARMOR) {
				base.set(10.0)
				modifiers {
					val uuid = UUID.randomUUID()
					add(uuid, "test", 10.0, AttributeModifierOperation.ADDITION)
					get(uuid, .5)
					remove(uuid)
				}
			}

			execute {
				ifCondition {
					score(player("Ayfri"), "test") lessThan score(entity("other"), "test")
				}

				at(selector(SelectorType.ALL_PLAYERS))
				asTarget(selector(SelectorType.SELF))
				align(Axes.XY)
				inDimension(Dimension.OVERWORLD)
				facingEntity(nearestEntity(), Anchor.EYES)

				rotatedAs(allEntities(true) {
					sort = Sort.RANDOM
				})

				run {
					advancement {
						grant(self(), Advancements.Adventure.ADVENTURING_TIME)
					}

					recipeGive(self(), Recipes.POLISHED_BLACKSTONE_BRICK_STAIRS_FROM_POLISHED_BLACKSTONE_BRICKS_STONECUTTING)
				}
			}

			addBlankLine()
			items.replace(coordinate(0, 0, 0), CONTAINER[5], Items.DIAMOND_SWORD {
				this["Enchantments"] = nbtList {
					addNbtCompound {
						this["lvl"] = 5
						this["id"] = Enchantments.SHARPNESS
					}
				}
			}, 20)

			comment("Replacing head with dirt")
			items.replace(nearestEntity { type = "minecraft:zombie" }, ARMOR.HEAD, Items.DIRT, 64)
			enchant(allPlayers(), Enchantments.FEATHER_FALLING, 3)

			loot {
				target {
					give(nearestPlayer())
				}

				source {
					fish(LootTables.Entities.COD, coordinate(PosNumber.Type.RELATIVE), Hand.MAIN_HAND)
				}
			}

			schedules {
				append("repeat", 1.ticks)
				clear("test")
			}

			scoreboard {
				objectives {
					add("test", "dummy", textComponent("Test"))
					setDisplay(DisplaySlot.sidebar, "test")
				}

				players {
					add(allPlayers(), "test", 1)
				}

				objective(allPlayers(), "test") {
					this += 5
					reset()
				}
			}

			summon(Entities.ARROW)
			locateBiome(Biomes.OLD_GROWTH_BIRCH_FOREST)
			gamerule(Gamerules.DO_DAYLIGHT_CYCLE, false)
			gamerule(Gamerules.RANDOM_TICK_SPEED, 12)

			setBlock(
				coordinate(PosNumber.Type.RELATIVE),
				Blocks.OAK_SIGN(states = mapOf("rotation" to "4"), nbtData = {
					this["Text1"] = textComponent("Hello")
					this["Text2"] = textComponent("World")
					this["Text3"] = textComponent {
						text = "This is a text component"
						color = Color.RED
						italic = true
					}
				})
			)

			tellraw(allPlayers(), textComponent {
				text = "Hello World"
				color = Color.RED
				italic = true
				clickEvent {
					openUrl("https://www.google.com")
				}
				hoverEvent {
					showText("This is a hover event") {
						color = Color.BLUE
					}
				}
			})

			teams {
				val team = "admin"

				add(team, textComponent("Admin"))
				modify(team) {
					color(Color.DARK_RED)
					friendlyFire(false)
					deathMessageVisibility(Visibility.HIDE_FOR_OTHER_TEAMS)
					prefix(textComponent {
						text = "<Admin>"
						color = Color.RED
						bold = true
					})
				}
			}
		}

		val reference = function("test2") {
			debug("Executing test2")
		}

		function("test3") {
			function(reference)
			debug("Executing test3")
		}

		iconPath = Path("datapackDSL", "src", "test", "resources", "Kotlin Full Color Logo Mark RGB.png")

		pack {
			format = 10
			description = textComponent("My ") {
				color = RGB(255, 0, 0)
			} + textComponent("nice ") {
				color = Color.GREEN
			} + textComponent("datapack") {
				color = Color.BLUE
				bold = true
			}
		}
	}

//	dataPack.generate()
}
