
import arguments.*
import arguments.enums.Dimension
import arguments.enums.Gamemode
import arguments.numbers.*
import arguments.selector.SelectorNbtData
import arguments.selector.SelectorType
import arguments.selector.Sort
import commands.*
import functions.function
import generated.*
import generated.Attributes
import tags.tags
import java.util.*
import kotlin.io.path.Path

fun main() {
	val dataPack = dataPack("test") {
		loadTeams(this)
		unloadTeams(this)
		gradient(this)

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

			attributes {
				get(self()) {
					val first = get(Attributes.GENERIC_ARMOR)
					first.get()
				}
			}

			val selector = allPlayers {
				dx = 1.0
				level = range(1..5)
				gamemode = !Gamemode.CREATIVE
			}

			advancements.grant(selector, Advancements.Story.IronTools)

			clear(targets = allPlayers(), item = item(Items.STONE), maxCount = 1)

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
					Advancements.Story.IronTools {
						this["witch"] = true
					}
				}

				scores {
					score("test", 4..8)
				}

				gamemode = !Gamemode.SURVIVAL
				type = "zombie"
			}) {
				modify("Position") {
					set(nbtListOf(0.0, 150.0, 0.0))
				}

				addBlankLine()
				modify("Motion") {
					insert(1, coordinate(2.relativePos, 2.pos, 2.pos), "test")
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
					add(uuid, Attributes.GENERIC_MOVEMENT_SPEED, 10.0, AttributeModifierOperation.ADD)
					get(uuid, .5)
					remove(uuid)
				}
			}

			execute {
				ifCondition {
					score(player("Ayfri"), "test", entity("other"), "test") { first, second ->
						first lessThan second
					}
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
						grant(self(), Advancements.Story.IronTools)
					}
				}
			}

			addBlankLine()
			items.replaceBlock(coordinate(0, 0, 0), CONTAINER[5], item(Items.STONE), 20)

			comment("Replacing head with dirt")
			items.replaceEntity(nearestEntity { type = "minecraft:zombie" }, ARMOR.HEAD, item(Items.DIRT), 64)

			loot {
				target {
					give(nearestPlayer())
				}

				source {
					fish("minecraft:cod", coordinate(PosNumber.Type.RELATIVE), Hand.MAIN_HAND)
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

			publish()
			publish(false)
			publish(true, Gamemode.ADVENTURE)
			publish(true, Gamemode.ADVENTURE, 12000)

			setBlock(
				coordinate(PosNumber.Type.RELATIVE),
				block("oak_sign", states = mapOf("rotation" to "4"), nbtData = nbt {
					this["Text1"] = "Hello"
					this["Text2"] = "World"

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
					action = ClickAction.OPEN_URL
					value = "https://www.google.com".nbt
				}
				hoverEvent {
					action = HoverAction.SHOW_TEXT
					value = textComponent {
						text = "This is a hover event"
						color = Color.BLUE
					}.toNbtTag()
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

		iconPath = Path("datapackDSL", "src", "test", "resources", "Kotlin Full Color Logo Mark RGB.png")

		pack {
			format = 10
			description = textComponent {
				text = "My "
				color = RGB(255, 0, 0)
			} + textComponent {
				text = "nice "
				color = Color.GREEN
			} + textComponent {
				text = "datapack"
				color = Color.BLUE
				bold = true
			}
		}

		filter {
			block {
				namespace = "test"
			}
		}
	}

	dataPack.generate()
}
