
import arguments.*
import arguments.numbers.pos
import arguments.numbers.range
import arguments.numbers.relativePos
import arguments.numbers.ticks
import commands.*
import functions.function
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.addNbtCompound
import net.benwoodworth.knbt.put
import tags.tags
import kotlin.io.path.Path

fun main() {
	val dataPack = dataPack("test") {
		addLoad(this)
		
		tags("minecraft") {
			tag("blocks", "op") {
				add("minecraft:command_block")
				add("minecraft:repeating_command_block")
				add("minecraft:chain_command_block")
				add("minecraft:barrier", required = true)
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
			
			advancement(AdvancementAction.GRANT, selector, advancement("story/iron_tools"))
			
			clear(targets = selector(SelectorType.ALL_PLAYERS), item = item("stone"), maxCount = 1)
			
			bossBar("test") {
				setColor(BossBarColor.BLUE)
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
					advancement(AdvancementAction.GRANT, selector(SelectorType.ALL_PLAYERS), advancement("story/iron_tools"))
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
			
			schedule {
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
					
					add(team, NbtString("Admin"))
					modify(team) {
						color(Color.DARK_RED)
						friendlyFire(false)
						deathMessageVisibility(Visibility.HIDE_FOR_OTHER_TEAMS)
						prefix(nbt {
							put("text", "<Admin>")
							put("color", Color.RED.asArg())
							put("bold", true)
						})
					}
				}
			}
		}
		
		iconPath = Path("datapackDSL", "src", "test", "resources", "Kotlin Full Color Logo Mark RGB.png")
		
		pack {
			format = 10
			description = nbtList {
				addNbtCompound {
					put("text", "My ")
					put("color", Color.RED.toString())
				}
				
				addNbtCompound {
					put("text", "nice ")
					put("color", Color.GREEN.toString())
				}
				
				addNbtCompound {
					put("text", "datapack")
					put("color", Color.BLUE.toString())
					put("bold", true)
				}
			}
		}
		
		filter {
			block {
				namespace = "test"
			}
		}
	}
	
	dataPack.generateZip()
}
