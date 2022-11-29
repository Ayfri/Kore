
import arguments.Color
import arguments.enums.Gamemode
import arguments.numbers.rangeOrIntStart
import arguments.textComponent
import commands.DisplaySlot
import commands.execute
import entities.executeAs
import entities.executeAt
import entities.getScore
import entities.giveItem
import entities.joinTeam
import entities.player
import entities.teleportTo
import functions.function
import items.itemStack
import items.summon
import scoreboard.copyFrom
import scoreboard.create
import scoreboard.plusAssign
import scoreboard.scoreboard
import scoreboard.set
import scoreboard.setDisplayName
import scoreboard.setDisplaySlot
import teams.addMembers
import teams.ensureExists
import teams.setColor
import teams.setPrefix
import teams.team

fun main() {
	dataPack("oop") {
		
		val player = player("Ayfri") {
			gamemode = Gamemode.SURVIVAL
			team = "red"
		}
		val item = itemStack("diamond_sword")
		
		function("main") {
			player.giveItem(item)
			player.executeAt {
				run {
					item.summon()
				}
			}
			
			player.executeAs {
				run {
					it.teleportTo(it)
				}
			}
			
			execute {
				ifCondition {
					score(player.asSelector(), "deathCount", rangeOrIntStart(2))
				}
				
				run {
					player.joinTeam("red")
				}
			}
			
			player.getScore("test").apply {
				set(10)
				this += 5
				
				copyFrom(player.asSelector(), "foodLevel")
			}
			
			scoreboard("other.test") {
				create()
				setDisplaySlot(DisplaySlot.sidebar)
				setDisplayName("Test")
			}
			
			team("red") {
				ensureExists()
				setColor(Color.DARK_RED)
				setPrefix(textComponent {
					text = "RED "
					color = Color.DARK_RED
					bold = true
				})
				
				addMembers(player)
			}
		}
	}.generate()
}
