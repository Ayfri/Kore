
import arguments.*
import arguments.enums.Attribute
import arguments.enums.Dimension
import arguments.enums.Gamemode
import arguments.numbers.pos
import arguments.numbers.range
import arguments.numbers.relativePos
import arguments.numbers.ticks
import arguments.selector.SelectorNbtData
import arguments.selector.SelectorType
import arguments.selector.Sort
import commands.*
import functions.function
import net.benwoodworth.knbt.put
import tags.tags
import java.util.*
import kotlin.io.path.Path

fun main() {
	val dataPack = dataPack("test") {
		loadTeams(this)
		unloadTeams(this)
		gradiant(this)
		
		tags("minecraft") {
			tag("blocks", "op") {
				add("command_block")
				add("repeating_command_block")
				add("chain_command_block")
				add("barrier", required = true)
			}
		}
		
		function("test") {
			val selector = selector(SelectorType.ALL_ENTITIES) {
				dx = 1.0
				level = range(1..5)
				gamemode = !Gamemode.CREATIVE
			}
			
			fun nearestEntity(block: SelectorNbtData.() -> Unit = {}) = allEntities(true) {
				sort = Sort.NEAREST
				block()
			}
			
			advancements.grant(selector, advancement("story/iron_tools"))
			
			clear(targets = selector(SelectorType.ALL_PLAYERS), item = item("stone"), maxCount = 1)
			
			bossBar("test") {
				setColor(Color.BLUE)
				setStyle(BossBarStyle.NOTCHED_6)
				setMax(100)
				setVisible(true)
				setValue(50)
			}
			
			bossBars.list()
			
			addBlankLine()
			cloneFiltered(coordinate(20, 50, 30), coordinate(40, 60, 50), coordinate(0, 0, 0), blockTag("wool"), CloneMode.MASKED)
			
			data(selector(SelectorType.ALL_ENTITIES, true) {
				advancements {
					advancement("story/kill_all_mobs") {
						put("witch", true)
					}
				}
				
				scores {
					score("test", 4..8)
				}
				
				gamemode = !Gamemode.SURVIVAL
				type = "zombie"
			}) {
				modify("Position") {
					set(coordinate(0, 0, 0))
				}
				
				addBlankLine()
				modify("Motion") {
					insert(1, coordinate(pos(2).relative, pos(2), pos(2)), "test")
				}
			}
			
			defaultGamemode(Gamemode.SURVIVAL)
			
			effect(selector(SelectorType.ALL_PLAYERS)) {
				clear()
				give("minecraft:regeneration", 10, 1, false)
			}
			
			attributes(nearestPlayer(), Attribute.GENERIC_ARMOR) {
				base.set(10.0)
				modifiers {
					val uuid = UUID.randomUUID()
					add(uuid, "test", 10.0, AttributeModifierOperation.ADD)
					get(uuid, .5)
					remove(uuid)
				}
			}
			
			execute {
				ifCondition {
					score(literal("Ayfri"), "test", literal("other"), "test") { first, second ->
						first lessThan second
					}
				}
				
				at(selector(SelectorType.ALL_PLAYERS))
				asTarget(selector(SelectorType.SELF))
				align(Axes.XY)
				inDimension(Dimension.OVERWORLD)
				facingEntity(selector(SelectorType.ALL_ENTITIES, true) {
					sort = Sort.NEAREST
				})
				
				rotatedAs(selector(SelectorType.ALL_ENTITIES, true) {
					sort = Sort.RANDOM
				})
				
				run {
					advancement {
						grant(self(), advancement("story/iron_tools"))
					}
				}
			}
			
			addBlankLine()
			items.replaceBlock(coordinate(0, 0, 0), CONTAINER[5], item("stone"), 20)
			
			comment("Replacing head with dirt")
			items.replaceEntity(nearestEntity { type = "minecraft:zombie" }, ARMOR.HEAD, item("dirt"), 64)
			
			loot {
				target {
					give(nearestPlayer())
				}
				
				source {
					fish("minecraft:cod", coordinate(0.relativePos, 0.relativePos, 0.relativePos), Hand.MAIN_HAND)
				}
			}
			
			schedules {
				append("repeat", 1.ticks)
				clear("test")
			}
			
			scoreboard {
				objectives {
					add("test", "dummy", "Test")
					setDisplay(DisplaySlot.sidebar, "test")
				}
				
				players {
					add(allPlayers(), "test", 1)
				}
				
				objective(allPlayers(), "test") {
					this += 5
					reset()
				}
				
				setBlock(coordinate(0, 0, 0), block("sign", states = mapOf("rotation" to "4"), nbtData = nbt {
					put("Text1", "Hello")
					put("Text2", "World")
					
					json("Text3") {
						put("text", "test")
						put("color", Color.RED.asArg())
						put("bold", true)
					}
				}))
				
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