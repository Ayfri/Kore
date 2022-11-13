
import arguments.*
import commands.*
import functions.function

fun main() {
	val dataPack = dataPack("test") {
		function("load", "minecraft") {
			val selector = selector(SelectorType.ALL_ENTITIES) {
				dx = 1.0
				level = range(1..5)
				gamemode = Gamemode.SURVIVAL
			}
			
			advancement(AdvancementAction.GRANT, selector, advancement("story/iron_tools"))
			
			clear(targets = selector(SelectorType.ALL_PLAYERS), item = item("stone"), maxCount = 1)
			
			bossbar("test") {
				setColor(BossBarColor.BLUE)
				setStyle(BossBarStyle.NOTCHED_6)
				setMax(100)
				setVisible(true)
				setValue(50)
			}
			
			bossbars.list()
			
			addBlank()
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
				
				addBlank()
				comment("Ouais ceci n'a aucun sens mais juste je teste si ça compile mdr")
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
		}
		
		pack {
			description = "Test"
		}
	}
	
	dataPack.generate()
}
